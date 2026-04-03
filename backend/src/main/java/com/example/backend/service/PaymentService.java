package com.example.backend.service;

import com.example.backend.dto.BookingRequest;
import com.example.backend.dto.PaymentCheckoutRequest;
import com.example.backend.dto.PaymentCheckoutResponse;
import com.example.backend.enums.PaymentStatus;
import com.example.backend.model.PaymentRecord;
import com.example.backend.repository.BookingRepository;
import com.example.backend.repository.PaymentRecordRepository;
import com.example.backend.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final BookingService bookingService;
    private final PaymentRecordRepository paymentRecordRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${app.baseUrl}")
    private String baseUrl;

    public PaymentService(
        BookingService bookingService,
        PaymentRecordRepository paymentRecordRepository,
        BookingRepository bookingRepository,
        UserRepository userRepository
    ) {
        this.bookingService = bookingService;
        this.paymentRecordRepository = paymentRecordRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    public PaymentCheckoutResponse createCheckoutSession(PaymentCheckoutRequest request) throws StripeException {
        Stripe.apiKey = secretKey;

        validateMinimumAmount(request.getCurrency(), request.getAmount());

        String orderId = request.getOrderId() == null || request.getOrderId().isBlank()
            ? UUID.randomUUID().toString()
            : request.getOrderId();

        PaymentRecord existing = paymentRecordRepository.findByOrderId(orderId).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.PENDING) {
            if (existing.getCheckoutSessionId() != null && !existing.getCheckoutSessionId().isBlank()) {
                Session existingSession = getCheckoutSession(existing.getCheckoutSessionId());
                PaymentCheckoutResponse resp = new PaymentCheckoutResponse();
                resp.setOrderId(orderId);
                resp.setCheckoutSessionId(existing.getCheckoutSessionId());
                resp.setUrl(existingSession.getUrl());
                return resp;
            }
            PaymentCheckoutResponse resp = new PaymentCheckoutResponse();
            resp.setOrderId(orderId);
            resp.setCheckoutSessionId(existing.getCheckoutSessionId());
            resp.setUrl(existing.getCheckoutSessionId());
            return resp;
        }

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/payment-cancel")
            .setClientReferenceId(orderId)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(request.getCurrency())
                            .setUnitAmount(request.getAmount())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Venue Booking: " + request.getVenueId())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .putMetadata("orderId", orderId)
            .putMetadata("venueId", request.getVenueId())
            .putMetadata("userId", request.getUserId())
            .putMetadata("courtName", request.getCourtName())
            .putMetadata("courtLocation", request.getCourtLocation())
            .putMetadata("sport", request.getSport())
            .putMetadata("courtImage", request.getCourtImage())
            .putMetadata("price", String.valueOf(request.getAmount() / 100))
            .putMetadata("bookingDate", request.getBookingDate())
            .putMetadata("bookingSlot", request.getBookingSlot())
            .putMetadata("membersJoined", String.valueOf(request.getMembersJoined()))
            .putMetadata("totalMembers", String.valueOf(request.getTotalMembers()))
            .build();

        RequestOptions options = RequestOptions.builder()
            .setIdempotencyKey(orderId)
            .build();

        Session session = Session.create(params, options);

        UUID resolvedVenueId = resolveVenueId(request.getVenueId());
        UUID resolvedUserId = resolveUserId(request.getUserId());
        if (resolvedVenueId == null || resolvedUserId == null) {
            throw new IllegalArgumentException("Invalid user or venue id");
        }

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setCheckoutSessionId(session.getId());
        record.setPaymentIntentId(session.getPaymentIntent());
        record.setAmount(request.getAmount());
        record.setCurrency(request.getCurrency());
        record.setVenueId(resolvedVenueId);
        record.setUserId(resolvedUserId);
        record.setCourtName(request.getCourtName());
        record.setCourtLocation(request.getCourtLocation());
        record.setCourtImage(request.getCourtImage());
        record.setSport(request.getSport());
        record.setBookingDate(request.getBookingDate());
        record.setBookingSlot(request.getBookingSlot());
        record.setMembersJoined(request.getMembersJoined());
        record.setTotalMembers(request.getTotalMembers());
        record.setStatus(PaymentStatus.PENDING);
        paymentRecordRepository.save(record);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUserId(request.getUserId());
        bookingRequest.setVenueId(request.getVenueId());
        bookingRequest.setCourtName(request.getCourtName());
        bookingRequest.setCourtLocation(request.getCourtLocation());
        bookingRequest.setSport(request.getSport());
        bookingRequest.setCourtImage(request.getCourtImage());
        bookingRequest.setPrice(request.getAmount().intValue());
        bookingRequest.setBookingDate(request.getBookingDate());
        bookingRequest.setBookingSlot(request.getBookingSlot());
        bookingRequest.setMembersJoined(request.getMembersJoined());
        bookingRequest.setTotalMembers(request.getTotalMembers());
        bookingRequest.setOrderId(orderId);
        bookingRequest.setPaymentIntentId(session.getPaymentIntent());
        bookingService.createPendingBooking(bookingRequest);

        PaymentCheckoutResponse response = new PaymentCheckoutResponse();
        response.setOrderId(orderId);
        response.setCheckoutSessionId(session.getId());
        response.setUrl(session.getUrl());
        return response;
    }

    public PaymentCheckoutResponse createCheckoutSessionForBooking(com.example.backend.model.Booking booking)
        throws StripeException {
        Stripe.apiKey = secretKey;

        Long amount = normalizeBookingAmount(booking.getPrice());
        validateMinimumAmount("inr", amount);

        String orderId = booking.getOrderId();
        if (orderId == null || orderId.isBlank()) {
            orderId = UUID.randomUUID().toString();
        }

        PaymentRecord existing = paymentRecordRepository.findByOrderId(orderId).orElse(null);
        if (existing != null && existing.getStatus() == PaymentStatus.PENDING) {
            if (existing.getCheckoutSessionId() != null && !existing.getCheckoutSessionId().isBlank()) {
                Session existingSession = getCheckoutSession(existing.getCheckoutSessionId());
                PaymentCheckoutResponse resp = new PaymentCheckoutResponse();
                resp.setOrderId(orderId);
                resp.setCheckoutSessionId(existing.getCheckoutSessionId());
                resp.setUrl(existingSession.getUrl());
                return resp;
            }
        }

        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(baseUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(baseUrl + "/payment-cancel")
            .setClientReferenceId(orderId)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("inr")
                            .setUnitAmount(amount)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("Venue Booking: " + booking.getVenueId())
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .putMetadata("orderId", orderId)
            .putMetadata("venueId", String.valueOf(booking.getVenueId()))
            .putMetadata("userId", String.valueOf(booking.getUserId()))
            .putMetadata("courtName", booking.getCourtName())
            .putMetadata("courtLocation", booking.getCourtLocation())
            .putMetadata("sport", booking.getSport())
            .putMetadata("courtImage", booking.getCourtImage())
            .putMetadata("price", String.valueOf(amount / 100))
            .putMetadata("bookingDate", booking.getBookingDate())
            .putMetadata("bookingSlot", booking.getBookingSlot())
            .putMetadata("membersJoined", String.valueOf(booking.getMembersJoined()))
            .putMetadata("totalMembers", String.valueOf(booking.getTotalMembers()))
            .build();

        RequestOptions options = RequestOptions.builder()
            .setIdempotencyKey(orderId)
            .build();

        Session session = Session.create(params, options);

        PaymentRecord record = existing != null ? existing : new PaymentRecord();
        record.setOrderId(orderId);
        record.setCheckoutSessionId(session.getId());
        record.setPaymentIntentId(session.getPaymentIntent());
        record.setAmount(amount);
        record.setCurrency("inr");
        record.setVenueId(booking.getVenueId());
        record.setUserId(booking.getUserId());
        record.setCourtName(booking.getCourtName());
        record.setCourtLocation(booking.getCourtLocation());
        record.setCourtImage(booking.getCourtImage());
        record.setSport(booking.getSport());
        record.setBookingDate(booking.getBookingDate());
        record.setBookingSlot(booking.getBookingSlot());
        record.setMembersJoined(booking.getMembersJoined());
        record.setTotalMembers(booking.getTotalMembers());
        record.setStatus(PaymentStatus.PENDING);
        paymentRecordRepository.save(record);

        booking.setOrderId(orderId);
        booking.setPaymentIntentId(session.getPaymentIntent());
        bookingRepository.save(booking);

        PaymentCheckoutResponse response = new PaymentCheckoutResponse();
        response.setOrderId(orderId);
        response.setCheckoutSessionId(session.getId());
        response.setUrl(session.getUrl());
        return response;
    }

    public PaymentRecord getPaymentRecord(String orderId) {
        return paymentRecordRepository.findByOrderId(orderId).orElse(null);
    }

    public Session getCheckoutSession(String sessionId) throws StripeException {
        Stripe.apiKey = secretKey;
        return Session.retrieve(sessionId);
    }

    @Transactional
    public void markPaid(String orderId, String paymentIntentId) {
        PaymentRecord record = paymentRecordRepository.findByOrderId(orderId).orElse(null);
        if (record != null) {
            record.setStatus(PaymentStatus.SUCCEEDED);
            record.setPaymentIntentId(paymentIntentId);
            paymentRecordRepository.save(record);
        }
    }

    @Transactional
    public void markFailed(String orderId) {
        PaymentRecord record = paymentRecordRepository.findByOrderId(orderId).orElse(null);
        if (record != null) {
            record.setStatus(PaymentStatus.FAILED);
            paymentRecordRepository.save(record);
        }
    }

    public Refund refundPaymentIntent(String paymentIntentId) throws StripeException {
        Stripe.apiKey = secretKey;
        RefundCreateParams params = RefundCreateParams.builder()
            .setPaymentIntent(paymentIntentId)
            .build();
        return Refund.create(params);
    }

    public List<PaymentRecord> listPaymentsForUser(String userIdOrEmail) {
        UUID userId = resolveUserId(userIdOrEmail);
        if (userId == null) {
            return List.of();
        }
        return paymentRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    private UUID resolveUserId(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(rawValue);
        } catch (IllegalArgumentException ex) {
            return userRepository.findByEmail(rawValue)
                .map(user -> user.getId())
                .orElse(null);
        }
    }

    private UUID resolveVenueId(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(rawValue);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Long normalizeBookingAmount(Integer price) {
        if (price == null) {
            return 0L;
        }
        if (price < 1000) {
            return price.longValue() * 100;
        }
        return price.longValue();
    }

    private void validateMinimumAmount(String currency, Long amount) {
        if (currency == null || currency.isBlank() || amount == null) {
            return;
        }
        String normalized = currency.trim().toLowerCase();
        long minUnitAmount = "inr".equals(normalized) ? 5000L : 50L;
        if (amount < minUnitAmount) {
            String minDisplay = "inr".equals(normalized)
                ? "₹50.00"
                : "0.50 " + currency.toUpperCase();
            throw new IllegalArgumentException("Amount too small. Stripe minimum is " + minDisplay + ".");
        }
    }
}

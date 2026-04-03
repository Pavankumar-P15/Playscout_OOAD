package com.example.backend.model;

import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.RefundStatus;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @Column(nullable = false)
    private String courtName;

    @Column(nullable = false)
    private String courtLocation;

    @Column(nullable = false)
    private String sport;

    @Column(nullable = true)
    private String courtImage;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String bookingDate;

    @Column(nullable = false)
    private String bookingSlot;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Integer membersJoined;

    @Column(nullable = false)
    private Integer totalMembers;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus = RefundStatus.NONE;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "payment_intent_id")
    private String paymentIntentId;

    @Column(name = "refund_id")
    private String refundId;

    @Column(name = "refund_requested_at")
    private OffsetDateTime refundRequestedAt;

    @Column(name = "refund_processed_at")
    private OffsetDateTime refundProcessedAt;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getVenueId() { return venueId; }
    public void setVenueId(UUID venueId) { this.venueId = venueId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getCourtImage() { return courtImage; }
    public void setCourtImage(String courtImage) { this.courtImage = courtImage; }

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingSlot() { return bookingSlot; }
    public void setBookingSlot(String bookingSlot) { this.bookingSlot = bookingSlot; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getMembersJoined() { return membersJoined; }
    public void setMembersJoined(Integer membersJoined) { this.membersJoined = membersJoined; }

    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public RefundStatus getRefundStatus() { return refundStatus; }
    public void setRefundStatus(RefundStatus refundStatus) { this.refundStatus = refundStatus; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public String getRefundId() { return refundId; }
    public void setRefundId(String refundId) { this.refundId = refundId; }

    public OffsetDateTime getRefundRequestedAt() { return refundRequestedAt; }
    public void setRefundRequestedAt(OffsetDateTime refundRequestedAt) { this.refundRequestedAt = refundRequestedAt; }

    public OffsetDateTime getRefundProcessedAt() { return refundProcessedAt; }
    public void setRefundProcessedAt(OffsetDateTime refundProcessedAt) { this.refundProcessedAt = refundProcessedAt; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

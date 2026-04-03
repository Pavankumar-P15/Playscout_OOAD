package com.example.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.backend.enums.PaymentStatus;

public class PaymentHistoryResponse {
    private UUID id;
    private String orderId;
    private String courtName;
    private String courtLocation;
    private String sport;
    private String bookingDate;
    private String bookingSlot;
    private Long amount;
    private String currency;
    private PaymentStatus status;
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingSlot() { return bookingSlot; }
    public void setBookingSlot(String bookingSlot) { this.bookingSlot = bookingSlot; }

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

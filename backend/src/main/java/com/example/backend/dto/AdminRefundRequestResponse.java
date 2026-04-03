package com.example.backend.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.example.backend.enums.RefundStatus;

public class AdminRefundRequestResponse {
    private UUID bookingId;
    private UUID userId;
    private String userEmail;
    private String courtName;
    private String courtLocation;
    private String sport;
    private String bookingDate;
    private String bookingSlot;
    private Integer price;
    private RefundStatus refundStatus;
    private OffsetDateTime refundRequestedAt;

    public UUID getBookingId() { return bookingId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

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

    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }

    public RefundStatus getRefundStatus() { return refundStatus; }
    public void setRefundStatus(RefundStatus refundStatus) { this.refundStatus = refundStatus; }

    public OffsetDateTime getRefundRequestedAt() { return refundRequestedAt; }
    public void setRefundRequestedAt(OffsetDateTime refundRequestedAt) { this.refundRequestedAt = refundRequestedAt; }
}

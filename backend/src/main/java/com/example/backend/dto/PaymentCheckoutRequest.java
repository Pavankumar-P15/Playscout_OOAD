package com.example.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentCheckoutRequest {
    @NotNull @Min(1)
    private Long amount; // smallest currency unit, e.g. 5000 = ₹50.00
    @NotBlank
    private String currency;
    @NotBlank
    private String venueId;
    @NotBlank
    private String userId;
    @NotBlank
    private String courtName;
    @NotBlank
    private String courtLocation;
    private String courtImage;
    @NotBlank
    private String sport;
    @NotBlank
    private String bookingDate;
    @NotBlank
    private String bookingSlot;
    @NotNull
    @Min(1)
    private Integer membersJoined;
    @NotNull
    @Min(1)
    private Integer totalMembers;
    private String orderId;

    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getVenueId() { return venueId; }
    public void setVenueId(String venueId) { this.venueId = venueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getCourtLocation() { return courtLocation; }
    public void setCourtLocation(String courtLocation) { this.courtLocation = courtLocation; }

    public String getCourtImage() { return courtImage; }
    public void setCourtImage(String courtImage) { this.courtImage = courtImage; }

    public String getSport() { return sport; }
    public void setSport(String sport) { this.sport = sport; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingSlot() { return bookingSlot; }
    public void setBookingSlot(String bookingSlot) { this.bookingSlot = bookingSlot; }

    public Integer getMembersJoined() { return membersJoined; }
    public void setMembersJoined(Integer membersJoined) { this.membersJoined = membersJoined; }

    public Integer getTotalMembers() { return totalMembers; }
    public void setTotalMembers(Integer totalMembers) { this.totalMembers = totalMembers; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}

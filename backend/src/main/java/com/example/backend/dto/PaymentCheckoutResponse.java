package com.example.backend.dto;

public class PaymentCheckoutResponse {
    private String orderId;
    private String checkoutSessionId;
    private String url;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getCheckoutSessionId() { return checkoutSessionId; }
    public void setCheckoutSessionId(String checkoutSessionId) { this.checkoutSessionId = checkoutSessionId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

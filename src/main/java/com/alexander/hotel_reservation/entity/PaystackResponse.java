package com.alexander.hotel_reservation.entity;

public class PaystackResponse {

    private boolean status;
    private String message;
    private PaystackData data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaystackData getData() {
        return data;
    }

    public void setData(PaystackData data) {
        this.data = data;
    }
}

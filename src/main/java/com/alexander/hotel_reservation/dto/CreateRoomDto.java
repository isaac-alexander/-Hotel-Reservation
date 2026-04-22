package com.alexander.hotel_reservation.dto;

public class CreateRoomDto {

    // room type (single, queen, studio)
    private String roomType;

    // room price
    private double price;

    // availability status
    private boolean available;

    public CreateRoomDto() {
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
package com.app.cookbook.model;

import java.io.Serializable;

public class BookingHotelModel implements Serializable {
    private String userId;
    private long hotelId;

    private String price;

    private String id;
    private String numberOfGuest;
    private long startDate;
    private long endDate;

    public BookingHotelModel(String userId, long hotelId, String price, String numberOfGuest, long startDate, long endDate) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.price = price;
        this.numberOfGuest = numberOfGuest;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public BookingHotelModel(String userId, long hotelId) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.price = "1000";
        this.numberOfGuest = "10";
        this.startDate = 0;
        this.endDate = 0;
    }

    public BookingHotelModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumberOfGuest() {
        return numberOfGuest;
    }

    public void setNumberOfGuest(String numberOfGuest) {
        this.numberOfGuest = numberOfGuest;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}

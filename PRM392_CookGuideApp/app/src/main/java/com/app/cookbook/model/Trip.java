package com.app.cookbook.model;

import java.io.Serializable;

public class Trip implements Serializable {
    private String destination;
    private Integer number;
    private String tripName;
    private String id;
    private String userId;
    private long tripDate;

    public Trip(String destination, Integer number, String tripName, String userId, long tripDate) {
        this.destination = destination;
        this.number = number;
        this.tripName = tripName;
        this.userId = userId;
        this.tripDate = tripDate;
    }

    public Trip() {
    }

    public Trip(String destination, Integer number, String tripName, long tripDate) {
        this.destination = destination;
        this.number = number;
        this.tripName = tripName;
        this.tripDate = tripDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String user) {
        this.userId = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public long getTripDate() {
        return tripDate;
    }

    public void setTripDate(long tripDate) {
        this.tripDate = tripDate;
    }

    public String getUser() {
        return userId;
    }
}

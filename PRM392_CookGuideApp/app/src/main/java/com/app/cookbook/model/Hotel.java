package com.app.cookbook.model;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;

public class Hotel implements Serializable {
    private long id;
    private String name;
    private String image;
    private int count;
    private HashMap<String, UserInfo> favorite;
    private HashMap<String, UserInfo> history;
    private HashMap<String, Rating> rating;
    private long locationId;
    private String locationName;

    public Hotel() {}

    public Hotel(long id, String name, String image,  long locationId, String locationName) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.locationId = locationId;
        this.locationName = locationName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<String, UserInfo> getFavorite() {
        return favorite;
    }

    public void setFavorite(HashMap<String, UserInfo> favorite) {
        this.favorite = favorite;
    }

    public String countFavorites() {
        if (favorite == null || favorite.isEmpty()) {
            return "0";
        } else {
            return String.valueOf(favorite.size());
        }
    }

    public HashMap<String, UserInfo> getHistory() {
        return history;
    }

    public void setHistory(HashMap<String, UserInfo> history) {
        this.history = history;
    }

    public HashMap<String, Rating> getRating() {
        return rating;
    }

    public void setRating(HashMap<String, Rating> rating) {
        this.rating = rating;
    }

    public double getRate() {
        if (rating == null || rating.isEmpty()) return 0;
        double sum = 0;
        for (Rating ratingEntity : rating.values()) {
            sum += ratingEntity.getRate();
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#.#");
        formatter.setDecimalFormatSymbols(symbols);
        return Double.parseDouble(formatter.format(sum / rating.size()));
    }

    public int getCountReviews() {
        if (rating == null || rating.isEmpty()) return 0;
        return rating.size();
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}

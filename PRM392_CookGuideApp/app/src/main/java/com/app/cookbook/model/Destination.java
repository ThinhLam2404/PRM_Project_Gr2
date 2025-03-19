package com.app.cookbook.model;

import java.io.Serializable;
import java.util.HashMap;

public class Destination implements Serializable {
    private long id;
    private String name;
    private String image;
    private String url;
    private boolean featured;
    private int count;
    private HashMap<String, UserInfo> favorite;
    private HashMap<String, UserInfo> history;
    private long locationId;
    private String locationName;

    public Destination() {
    }

    public Destination(long id, String name, String image, String url, boolean featured, long locationId, String locationName) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.url = url;
        this.featured = featured;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
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

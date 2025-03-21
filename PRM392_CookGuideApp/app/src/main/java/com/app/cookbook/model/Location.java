package com.app.cookbook.model;

import java.io.Serializable;

public class Location implements Serializable {
    private long id;
    private String name;
    private String image;
    private int count;

    public Location() {}

    public Location(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Location(long id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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
}

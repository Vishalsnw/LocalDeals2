package com.deals.app.models;

public class Business {
    private String id;
    private String name;
    private String ownerId;
    private String city;
    private String description;
    private long createdAt;

    public Business() {
        // Default constructor required for calls to DataSnapshot.getValue(Business.class)
    }

    public Business(String name, String ownerId, String city, String description) {
        this.name = name;
        this.ownerId = ownerId;
        this.city = city;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    public Business(String id, String name, String ownerId, String city, String description) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.city = city;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
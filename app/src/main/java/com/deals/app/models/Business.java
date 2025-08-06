package com.deals.app.models;

public class Business {
    private String businessId;
    private String businessName;
    private String ownerId;
    private String ownerEmail;
    private String description;
    private String category;
    private String address;
    private String city;
    private String phoneNumber;
    private String website;
    private long createdAt;

    public Business() {
        // Default constructor required for Firestore
    }

    public Business(String businessName, String ownerId, String ownerEmail,
                   String description, String category, String address, String city,
                   String phoneNumber, String website) {
        this.businessName = businessName;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.description = description;
        this.category = category;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.createdAt = System.currentTimeMillis();
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}

package com.deals.app.models;

public class Business {
    private String businessId;
    private String ownerId;
    private String name;
    private String description;
    private String category;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private long createdAt;
    private boolean isVerified;

    public Business() {
        // Required empty constructor for Firestore
    }

    public Business(String ownerId, String name, String description, String category,
                   String address, String city, String phone, String email) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
        this.isVerified = false;
    }

    // Getters and setters
    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
}
package com.deals.app.models;

public class Business {
    private String businessId;
    private String name;
    private String description;
    private String category;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String website;
    private String ownerId;
    private long createdAt;
    private boolean isVerified;

    public Business() {
        // Default constructor required for calls to DataSnapshot.getValue(Business.class)
    }

    public Business(String name, String description, String category, String address, String city, String phone, String email, String website, String ownerId) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.ownerId = ownerId;
        this.createdAt = System.currentTimeMillis();
        this.isVerified = false;
    }

    // Getters and setters
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }
}

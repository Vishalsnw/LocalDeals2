
package com.deals.app.models;

public class Offer {
    private String offerId;
    private String businessId;
    private String businessName;
    private String title;
    private String description;
    private String category;
    private String city;
    private double originalPrice;
    private double discountedPrice;
    private int discountPercentage;
    private String imageUrl;
    private long expirationDate;
    private long createdAt;
    private boolean isActive;

    public Offer() {
        // Required empty constructor for Firestore
    }

    public Offer(String businessId, String businessName, String title, String description, 
                 String category, String city, double originalPrice, double discountedPrice, 
                 String imageUrl, long expirationDate) {
        this.businessId = businessId;
        this.businessName = businessName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.city = city;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercentage = (int) ((originalPrice - discountedPrice) / originalPrice * 100);
        this.imageUrl = imageUrl;
        this.expirationDate = expirationDate;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters and setters
    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public int getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getExpirationDate() { return expirationDate; }
    public void setExpirationDate(long expirationDate) { this.expirationDate = expirationDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}

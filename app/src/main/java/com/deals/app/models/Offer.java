package com.deals.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Offer {
    private String offerId;
    private String title;
    private String description;
    private String category;
    private String businessId;
    private String businessName;
    private double originalPrice;
    private double discountedPrice;
    private int discountPercentage;
    private String terms;
    private long expirationDate;
    private long createdAt;
    private boolean isActive;
    private String city;

    public Offer() {
        // Default constructor required for calls to DataSnapshot.getValue(Offer.class)
    }

    public Offer(String title, String description, String businessId, String businessName,
                String category, String city, double originalPrice, double discountedPrice,
                int discountPercentage, long expiryDate, String ownerId) {
        this.title = title;
        this.description = description;
        this.businessId = businessId;
        this.businessName = businessName;
        this.category = category;
        this.city = city;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercentage = discountPercentage;
        this.expirationDate = expiryDate; // Note: Renamed from expiryDate to expirationDate to match the class field. Assuming this is the intended fix.
        this.ownerId = ownerId; // Note: This field was not present in the original Offer class. Assuming it's a new field.
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
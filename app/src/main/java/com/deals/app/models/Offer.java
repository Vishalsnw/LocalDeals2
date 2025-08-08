package com.deals.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Offer implements Parcelable {
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
    private String ownerId; // Added based on the constructor
    private long dateCreated; // Added based on writeToParcel

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
        this.dateCreated = System.currentTimeMillis(); // Initialize dateCreated
    }

    // Constructor for Parcelable
    protected Offer(Parcel in) {
        offerId = in.readString();
        title = in.readString();
        description = in.readString();
        businessName = in.readString();
        businessId = in.readString();
        category = in.readString();
        city = in.readString();
        originalPrice = in.readDouble();
        discountedPrice = in.readDouble();
        discountPercentage = in.readInt();
        expirationDate = in.readLong();
        isActive = in.readByte() != 0;
        dateCreated = in.readLong();
        ownerId = in.readString();
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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(offerId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(businessName);
        dest.writeString(businessId);
        dest.writeString(category);
        dest.writeString(city);
        dest.writeDouble(originalPrice);
        dest.writeDouble(discountedPrice);
        dest.writeInt(discountPercentage);
        dest.writeLong(expirationDate);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeLong(dateCreated);
        dest.writeString(ownerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}
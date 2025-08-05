
package com.deals.app.models;

public class User {
    private String uid;
    private String email;
    private String name;
    private String city;
    private String role; // "customer" or "business_owner"
    private long createdAt;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String uid, String email, String name, String city, String role) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.city = city;
        this.role = role;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}

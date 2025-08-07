package com.deals.app.models;

public class User {
    private String userId;
    private String email;
    private String name;
    private String role; // "user" or "business"
    private String city;
    private long createdAt;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String name, String role, String city) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.city = city;
        this.createdAt = System.currentTimeMillis();
    }

    public User(String userId, String email, String name, String role, String city) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.city = city;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
package com.deals.app.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String role; // "customer" or "business"
    private long dateCreated;

    public User() {
        // Default constructor required for Firestore
    }

    public User(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.dateCreated = System.currentTimeMillis();
    }

    // Getters and Setters
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }
}

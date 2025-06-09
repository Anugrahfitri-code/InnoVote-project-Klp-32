package com.innovote.models;

public abstract class EventUser {
    protected String username; // Lebih jelas daripada displayName

    public EventUser(String username) {
        this.username = username;
    }

    // Common method untuk semua user
    public String getWelcomeMessage() {
        return "Welcome, " + username + "!";
    }
    
    public String getUsername() {
        return username;
    }
}
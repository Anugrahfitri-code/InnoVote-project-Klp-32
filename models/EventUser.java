package models;

public abstract class EventUser {
    protected String username; 

    public EventUser(String username) {
        this.username = username;
    }
    
    public String getWelcomeMessage() {
        return "Welcome, " + username + "!";
    }
    
    public String getUsername() {
        return username;
    }
}
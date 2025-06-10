package models;

import java.util.Objects; 

public abstract class User { 
    protected String id; 
    protected String username;  
    protected String password;  
    protected String role;    

    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }
 
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {  
        return password;
    }

    public String getRole() {
        return role;
    }
 
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);  
    }
}

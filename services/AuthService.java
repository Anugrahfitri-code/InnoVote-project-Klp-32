package services;

import models.User;
import models.Participant;
import models.Judge;
import utils.DummyDatabase;
import exceptions.AuthException;
import java.util.UUID; 

public class AuthService {
    private static final int MIN_PASSWORD_LENGTH = 6;

    public static User login(String username, String password) throws AuthException {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username cannot be empty!");
        }

        User user = DummyDatabase.findUser(username);
        if (user == null) {
            throw new AuthException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthException("Invalid password");
        }

        return user;
    }

    public static User register(String username, String password, String role) throws AuthException {
        validateRegistration(username, password);

        String newId = UUID.randomUUID().toString();
        User newUser;

        if ("participant".equalsIgnoreCase(role)) {
            newUser = new Participant(newId, username, password);
        } else if ("judge".equalsIgnoreCase(role)) {
            newUser = new Judge(newId, username, password);
        } else {
            throw new AuthException("Invalid role");
        }

        DummyDatabase.addUser(newUser); 
        return newUser;
    }

    private static void validateRegistration(String username, String password) throws AuthException {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username cannot be empty");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new AuthException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        if (DummyDatabase.findUser(username) != null) {
            throw new AuthException("Username already exists");
        }
    }
    
}

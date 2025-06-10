package screens.auth;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.lang.classfile.Label;

import javafx.geometry.*;
import services.AuthService;
import utils.SceneManager;
import models.User;

public class LoginScreen extends VBox {
    public LoginScreen() {

        this.setPrefWidth(500);  
        this.setPrefHeight(400); 

        Label titleLabel = new Label("InnoVote 2025");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox formBox = new VBox(10, 
            new Label("Username:"), usernameField,
            new Label("Password:"), passwordField,
            loginBtn, registerBtn, errorLabel
        );
        formBox.setPadding(new Insets(20));
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        loginBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            try {
                User user = AuthService.login(
                    usernameField.getText(), 
                    passwordField.getText()
                );
                SceneManager.switchToUserDashboard(user);
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> SceneManager.switchToRegister());

        this.getChildren().addAll(titleLabel, formBox);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
    }
}

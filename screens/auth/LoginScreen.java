package screens.auth;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import com.innovote.services.AuthService;
import com.innovote.models.User;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;
import com.innovote.utils.AlertHelper;
import javafx.scene.control.Alert.AlertType;
import com.innovote.session.Session;
import com.innovote.exceptions.AuthException;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class LoginScreen extends VBox {
    public LoginScreen() {
        // === WINDOW CONFIGURATION ===
        this.setPrefWidth(600);
        this.setPrefHeight(500);
        
        // === MAIN BACKGROUND WITH GRADIENT ===
        this.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + 
            Theme.BACKGROUND_PRIMARY + ", " + 
            Theme.BACKGROUND_SECONDARY + ");"
        );
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(40));

        // === HEADER SECTION ===
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("InnoVote");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";"
        );
        
        Label subtitleLabel = new Label("2025 • Secure Digital Voting");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-opacity: 0.8;"
        );
        
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        // === MAIN FORM CARD ===
        VBox formCard = new VBox(25);
        formCard.setAlignment(Pos.CENTER);
        formCard.setPadding(new Insets(40, 35, 40, 35));
        formCard.setMaxWidth(420);
        formCard.setPrefWidth(420);
        
        formCard.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
        );

        // === INPUT FIELDS SECTION ===
        VBox inputSection = new VBox(20);
        inputSection.setAlignment(Pos.CENTER);
        
        // Username Field Container
        VBox usernameContainer = new VBox(8);
        usernameContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.setPrefWidth(350);
        usernameField.setPrefHeight(45);
        usernameField.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";" +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 12px 15px;" +
            "-fx-font-size: 14px;" +
            "-fx-focus-color: " + Theme.ACCENT_PRIMARY + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        
        // Focus effects for username field
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                usernameField.setStyle(usernameField.getStyle() + 
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;"
                );
            } else {
                usernameField.setStyle(usernameField.getStyle().replace(
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;", 
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                    "-fx-border-width: 1.5px;"
                ));
            }
        });
        
        usernameContainer.getChildren().addAll(usernameLabel, usernameField);

        // Password Field Container
        VBox passwordContainer = new VBox(8);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefWidth(350);
        passwordField.setPrefHeight(45);
        passwordField.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";" +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 12px 15px;" +
            "-fx-font-size: 14px;" +
            "-fx-focus-color: " + Theme.ACCENT_PRIMARY + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        
        // Focus effects for password field
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                passwordField.setStyle(passwordField.getStyle() + 
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;"
                );
            } else {
                passwordField.setStyle(passwordField.getStyle().replace(
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;", 
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                    "-fx-border-width: 1.5px;"
                ));
            }
        });
        
        passwordContainer.getChildren().addAll(passwordLabel, passwordField);
        
        inputSection.getChildren().addAll(usernameContainer, passwordContainer);

        // === ERROR LABEL ===
        Label errorLabel = new Label();
        errorLabel.setStyle(
            "-fx-text-fill: #e74c3c;" + 
            "-fx-font-size: 13px;" +
            "-fx-padding: 5px 0;"
        );
        errorLabel.setVisible(false);

        // === BUTTONS SECTION ===
        VBox buttonSection = new VBox(15);
        buttonSection.setAlignment(Pos.CENTER);
        
        // Login Button
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefWidth(350);
        loginBtn.setPrefHeight(48);
        loginBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, " + 
            Theme.ACCENT_PRIMARY + ", " + 
            Theme.ACCENT_PRIMARY_HOVER + ");" +
            "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + ";" +
            "-fx-font-size: 15px;" +
            "-fx-font-weight: 600;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
        );
        
        // Enhanced hover effects for login button
        loginBtn.setOnMouseEntered(e -> {
            loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, " + 
                Theme.ACCENT_PRIMARY_HOVER + ", " + 
                Theme.ACCENT_PRIMARY + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);" +
                "-fx-scale-y: 1.02;" +
                "-fx-scale-x: 1.02;"
            );
        });
        
        loginBtn.setOnMouseExited(e -> {
            loginBtn.setStyle(
                "-fx-background-color: linear-gradient(to right, " + 
                Theme.ACCENT_PRIMARY + ", " + 
                Theme.ACCENT_PRIMARY_HOVER + ");" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: 600;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);"
            );
        });

        // Divider with "or" text
        HBox dividerBox = new HBox(15);
        dividerBox.setAlignment(Pos.CENTER);
        
        Separator leftLine = new Separator();
        leftLine.setPrefWidth(120);
        leftLine.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");
        
        Label orLabel = new Label("or");
        orLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-font-size: 12px;"
        );
        
        Separator rightLine = new Separator();
        rightLine.setPrefWidth(120);
        rightLine.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");
        
        dividerBox.getChildren().addAll(leftLine, orLabel, rightLine);

        // Register Button
        Button registerBtn = new Button("Create New Account");
        registerBtn.setPrefWidth(350);
        registerBtn.setPrefHeight(48);
        registerBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effects for register button
        registerBtn.setOnMouseEntered(e -> {
            registerBtn.setStyle(
                "-fx-background-color: " + Theme.ACCENT_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
            );
        });
        
        registerBtn.setOnMouseExited(e -> {
            registerBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: 500;" +
                "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
            );
        });
        
        buttonSection.getChildren().addAll(loginBtn, dividerBox, registerBtn);

        // === ASSEMBLE FORM CARD ===
        formCard.getChildren().addAll(inputSection, errorLabel, buttonSection);

        // === EVENT HANDLERS ===
        loginBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            // Basic validation
            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                User loggedInUser = AuthService.login(username, password);
                Session.login(loggedInUser);
                AlertHelper.showAlert(
                    AlertType.INFORMATION, 
                    "Welcome!", 
                    "Successfully logged in as " + loggedInUser.getUsername()
                );
                SceneManager.switchToUserDashboard(loggedInUser);
            } catch (AuthException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
                AlertHelper.showAlert(AlertType.ERROR, "Login Failed", ex.getMessage());
            } catch (Exception ex) {
                errorLabel.setText("An unexpected error occurred. Please try again.");
                errorLabel.setVisible(true);
                AlertHelper.showAlert(
                    AlertType.ERROR, 
                    "Error", 
                    "An unexpected error occurred: " + ex.getMessage()
                );
            }
        });

        registerBtn.setOnAction(e -> SceneManager.switchToRegister());

        // Enable Enter key for login
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> loginBtn.fire());

        // === ASSEMBLE MAIN LAYOUT ===
        this.getChildren().addAll(headerBox, formCard);
    }
}

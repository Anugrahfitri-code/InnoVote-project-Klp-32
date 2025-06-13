package screens.auth;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import services.AuthService;
import models.User;
import utils.SceneManager;
import utils.Theme;
import utils.AlertHelper;
import javafx.scene.control.Alert.AlertType;
import exceptions.AuthException;

public class RegisterScreen extends VBox {
    public RegisterScreen() {
        this.setPrefWidth(600);
        this.setPrefHeight(580);
    
        this.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, " + 
            Theme.BACKGROUND_PRIMARY + ", " + 
            Theme.BACKGROUND_SECONDARY + ");"
        );
        this.setAlignment(Pos.CENTER);
        this.setSpacing(30);
        this.setPadding(new Insets(40));

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Join InnoVote");
        titleLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";"
        );
        
        Label subtitleLabel = new Label("Create your account to get started");
        subtitleLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
            "-fx-opacity: 0.8;"
        );
        
        headerBox.getChildren().addAll(titleLabel, subtitleLabel);

        VBox formCard = new VBox(25);
        formCard.setAlignment(Pos.CENTER);
        formCard.setPadding(new Insets(40, 35, 40, 35));
        formCard.setMaxWidth(450);
        formCard.setPrefWidth(450);
        
        formCard.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";" +
            "-fx-background-radius: 15px;" +
            "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
        );

        VBox inputSection = new VBox(20);
        inputSection.setAlignment(Pos.CENTER);
        
        VBox usernameContainer = new VBox(8);
        usernameContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a unique username");
        usernameField.setPrefWidth(380);
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

        VBox passwordContainer = new VBox(8);
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a secure password");
        passwordField.setPrefWidth(380);
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

        VBox roleContainer = new VBox(8);
        roleContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label roleLabel = new Label("Account Type");
        roleLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-font-weight: 500;"
        );
        
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Participant", "Judge");
        roleCombo.setPromptText("Select your role");
        roleCombo.setPrefWidth(380);
        roleCombo.setPrefHeight(45);
        roleCombo.getSelectionModel().selectFirst(); 
        roleCombo.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";" +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 12px 15px;" +
            "-fx-font-size: 14px;" +
            "-fx-focus-color: " + Theme.ACCENT_PRIMARY + ";" +
            "-fx-faint-focus-color: transparent;"
        );
        
        roleCombo.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                roleCombo.setStyle(roleCombo.getStyle() + 
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;"
                );
            } else {
                roleCombo.setStyle(roleCombo.getStyle().replace(
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";" +
                    "-fx-border-width: 2px;", 
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                    "-fx-border-width: 1.5px;"
                ));
            }
        });
        
        roleContainer.getChildren().addAll(roleLabel, roleCombo);
        
        inputSection.getChildren().addAll(usernameContainer, passwordContainer, roleContainer);

        Label errorLabel = new Label();
        errorLabel.setStyle(
            "-fx-text-fill: #e74c3c;" + 
            "-fx-font-size: 13px;" +
            "-fx-padding: 5px 0;"
        );
        errorLabel.setVisible(false);

        VBox buttonSection = new VBox(15);
        buttonSection.setAlignment(Pos.CENTER);
        
        Button registerBtn = new Button("Create Account");
        registerBtn.setPrefWidth(380);
        registerBtn.setPrefHeight(48);
        registerBtn.setStyle(
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
        
        registerBtn.setOnMouseEntered(e -> {
            registerBtn.setStyle(
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
        
        registerBtn.setOnMouseExited(e -> {
            registerBtn.setStyle(
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

        HBox dividerBox = new HBox(15);
        dividerBox.setAlignment(Pos.CENTER);
        
        Separator leftLine = new Separator();
        leftLine.setPrefWidth(150);
        leftLine.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");
        
        Label orLabel = new Label("or");
        orLabel.setStyle(
            "-fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-font-size: 12px;"
        );
        
        Separator rightLine = new Separator();
        rightLine.setPrefWidth(150);
        rightLine.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");
        
        dividerBox.getChildren().addAll(leftLine, orLabel, rightLine);

        Button backToLoginBtn = new Button("Already have an account? Sign In");
        backToLoginBtn.setPrefWidth(380);
        backToLoginBtn.setPrefHeight(48);
        backToLoginBtn.setStyle(
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
        
        backToLoginBtn.setOnMouseEntered(e -> {
            backToLoginBtn.setStyle(
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
        
        backToLoginBtn.setOnMouseExited(e -> {
            backToLoginBtn.setStyle(
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
        
        buttonSection.getChildren().addAll(registerBtn, dividerBox, backToLoginBtn);

        VBox roleDescriptionBox = new VBox(8);
        roleDescriptionBox.setAlignment(Pos.CENTER);
        roleDescriptionBox.setPadding(new Insets(15, 0, 0, 0));
        
        Label roleDescTitle = new Label("Account Types:");
        roleDescTitle.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: 600;"
        );
        
        Label participantDesc = new Label("• Participant: Submit and vote on innovations");
        participantDesc.setStyle(
            "-fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-font-size: 11px;"
        );
        
        Label judgeDesc = new Label("• Judge: Evaluate and score submissions");
        judgeDesc.setStyle(
            "-fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
            "-fx-font-size: 11px;"
        );
        
        roleDescriptionBox.getChildren().addAll(roleDescTitle, participantDesc, judgeDesc);

        formCard.getChildren().addAll(inputSection, errorLabel, buttonSection, roleDescriptionBox);

        registerBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String selectedRole = roleCombo.getValue();
            
            if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
                errorLabel.setText("Please fill in all fields");
                errorLabel.setVisible(true);
                return;
            }
            
            if (username.length() < 3) {
                errorLabel.setText("Username must be at least 3 characters long");
                errorLabel.setVisible(true);
                return;
            }
            
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters long");
                errorLabel.setVisible(true);
                return;
            }
            
            try {
                User newUser = AuthService.register(
                    username,
                    password,
                    selectedRole.toLowerCase()
                );
                AlertHelper.showAlert(
                    AlertType.INFORMATION, 
                    "Welcome to InnoVote!", 
                    "Account successfully created for " + newUser.getUsername() + " as " + selectedRole
                );
                SceneManager.switchToAuth(); 
            } catch (AuthException ex) {
                errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
                AlertHelper.showAlert(AlertType.ERROR, "Registration Failed", ex.getMessage());
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

        backToLoginBtn.setOnAction(e -> SceneManager.switchToAuth());

        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> roleCombo.requestFocus());
        roleCombo.setOnAction(e -> registerBtn.fire());

        this.getChildren().addAll(headerBox, formCard);
    }
    
}
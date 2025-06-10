package screens.auth;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import services.AuthService;
import models.User; 
import utils.SceneManager;

public class RegisterScreen extends VBox {
    public RegisterScreen() {
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Participant", "Judge");
        
        Button registerBtn = new Button("Register");
        Label errorLabel = new Label();

        registerBtn.setOnAction(e -> {
            try {
                User user = AuthService.register(
                    usernameField.getText(),
                    passwordField.getText(),
                    roleCombo.getValue().toLowerCase()
                );
                SceneManager.switchToUserDashboard(user);
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        this.getChildren().addAll(
            new Label("Username:"), usernameField,
            new Label("Password:"), passwordField,
            new Label("Role:"), roleCombo,
            registerBtn, errorLabel
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }
}

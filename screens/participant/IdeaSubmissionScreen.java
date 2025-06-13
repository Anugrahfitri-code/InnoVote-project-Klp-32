package com.innovote.screens.participant;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import com.innovote.models.Participant;
import com.innovote.services.IdeaService; // Impor IdeaService
import com.innovote.utils.AlertHelper;    // Impor AlertHelper
import com.innovote.exceptions.IdeaException; // Impor IdeaException
import com.innovote.utils.SceneManager; // Impor SceneManager

public class IdeaSubmissionScreen extends VBox { // Anda bisa gunakan VBox, GridPane, atau BorderPane

    private Participant currentParticipant;

    public IdeaSubmissionScreen(Participant participant) {
        this.currentParticipant = participant;

        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER); // Pusatkan konten

        Label titleLabel = new Label("Submit Your New Idea");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TextField titleField = new TextField();
        titleField.setPromptText("Idea Title (Max 100 characters)");
        titleField.setMaxWidth(400); // Batasi lebar input

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Detailed Description (Max 500 characters)");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(8);
        descriptionArea.setMaxWidth(400);

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (e.g., Environment, AI, Education)");
        categoryField.setMaxWidth(400);

        Button submitButton = new Button("Submit Idea");
        submitButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        submitButton.setOnAction(e -> {
            try {
                String title = titleField.getText();
                String description = descriptionArea.getText();
                String category = categoryField.getText();

                IdeaService.submitIdea(currentParticipant, title, description, category);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Your idea '" + title + "' has been submitted successfully!");

                // Kembali ke dashboard setelah submit sukses
                SceneManager.switchToScreen(new ParticipantDashboard(currentParticipant));

            } catch (IdeaException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Submission Error", ex.getMessage());
            }
        });

        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;");
        backButton.setOnAction(e -> {
            SceneManager.switchToScreen(new ParticipantDashboard(currentParticipant));
        });

        this.getChildren().addAll(titleLabel, titleField, descriptionArea, categoryField, submitButton, backButton);
    }
}
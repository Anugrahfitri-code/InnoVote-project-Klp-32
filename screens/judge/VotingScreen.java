package com.innovote.screens.judge;

import com.innovote.exceptions.VotingException;
import com.innovote.models.Idea;
import com.innovote.models.Judge;
import com.innovote.services.VotingService;
import com.innovote.utils.AlertHelper;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class VotingScreen extends VBox {

    private Judge currentJudge;
    private Idea ideaToVoteOn;

    public VotingScreen(Judge judge, Idea idea) {
        this.currentJudge = judge;
        this.ideaToVoteOn = idea;

        this.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");
        this.setPadding(new Insets(30, 40, 30, 40));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Submit Your Vote");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        VBox ideaDetailsBox = new VBox(8);
        ideaDetailsBox.setAlignment(Pos.CENTER);
        ideaDetailsBox.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-padding: 20; -fx-background-radius: 8;");
        ideaDetailsBox.setMaxWidth(600);

        Label ideaTitle = new Label(ideaToVoteOn.getTitle());
        ideaTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Label ideaDescription = new Label(ideaToVoteOn.getDescription());
        ideaDescription.setWrapText(true);
        ideaDescription.setMaxWidth(550);
        ideaDescription.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-size: 14px;");
        ideaDescription.setTextAlignment(TextAlignment.CENTER);

        Label ideaCategory = new Label("Category: " + ideaToVoteOn.getCategory());
        ideaCategory.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-style: italic;");

        Label ideaAuthor = new Label("Submitted by: " + ideaToVoteOn.getParticipant().getUsername());
        ideaAuthor.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        Region spacer = new Region();
        VBox.setMargin(spacer, new Insets(5, 0, 5, 0));

        ideaDetailsBox.getChildren().addAll(ideaTitle, ideaDescription, spacer, ideaCategory, ideaAuthor);

        GridPane voteFormGrid = new GridPane();
        voteFormGrid.setHgap(10);
        voteFormGrid.setVgap(15);
        voteFormGrid.setPadding(new Insets(25, 0, 25, 0));
        voteFormGrid.setAlignment(Pos.CENTER);
        voteFormGrid.setMaxWidth(500);

        ColumnConstraints labelColumn = new ColumnConstraints();
        labelColumn.setPrefWidth(140); 
        voteFormGrid.getColumnConstraints().add(labelColumn);

        Label scoreLabel = new Label("Score:");
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-weight: bold;");

        TextField scoreField = new TextField();
        scoreField.setPromptText("e.g., 4"); 
        applyInputStyles(scoreField);

        scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d*)?")) {
                scoreField.setText(oldValue);
            }
        });

        Label commentLabel = new Label("Comment:");
        commentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-weight: bold;");

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Provide your feedback here (optional)");
        applyInputStyles(commentArea);
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(5);

        voteFormGrid.add(scoreLabel, 0, 0);
        voteFormGrid.add(scoreField, 1, 0);
        voteFormGrid.add(commentLabel, 0, 1);
        voteFormGrid.add(commentArea, 1, 1);

        Button submitVoteButton = new Button("Submit Vote");
        applyButtonStyle(submitVoteButton);
        submitVoteButton.setPrefWidth(200);

        submitVoteButton.setOnAction(e -> {
            try {
                double score = Double.parseDouble(scoreField.getText());
                String comment = commentArea.getText();

                VotingService.submitVote(currentJudge, ideaToVoteOn, score, comment);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Your vote for '" + ideaToVoteOn.getTitle() + "' has been submitted!");

                SceneManager.switchToScreen(new JudgeDashboard(currentJudge));

            } catch (NumberFormatException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Invalid Input", "Please enter a valid number for the score (e.g., 4).");
            } catch (VotingException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Voting Error", ex.getMessage());
            }
        });

        Button backButton = new Button("Back to Dashboard");
        applyButtonStyle(backButton); 
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> SceneManager.switchToScreen(new JudgeDashboard(currentJudge)));

        HBox buttonBox = new HBox(20, backButton, submitVoteButton);
        buttonBox.setAlignment(Pos.CENTER);

        this.getChildren().addAll(
            titleLabel,
            ideaDetailsBox,
            voteFormGrid,
            buttonBox
        );
    }

    private void applyButtonStyle(Button button) {
        String baseColor = Theme.ACCENT_PRIMARY;
        String hoverColor = Theme.ACCENT_PRIMARY_HOVER;
        String textFill = "#000000";
        String hoverTextFill = "#000000";
        String style = String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 10 20; -fx-cursor: hand;", baseColor, textFill);
        button.setStyle(style);
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 10 20; -fx-cursor: hand;", hoverColor, hoverTextFill)));
        button.setOnMouseExited(e -> button.setStyle(style));
    }

    private void applyInputStyles(Node node) {
        node.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " + "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " + "-fx-border-color: " + Theme.BORDER_COLOR + "; " + "-fx-background-radius: 4; " + "-fx-border-radius: 4; " + "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + ";");
    }
}

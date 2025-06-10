package com.innovote.screens.judge;

import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane; 
import javafx.scene.control.ButtonType; 
import javafx.scene.control.ButtonBar; 

import com.innovote.models.Judge;
import com.innovote.models.Idea;
import com.innovote.services.VotingService;
import com.innovote.utils.AlertHelper;
import com.innovote.exceptions.VotingException;
import com.innovote.utils.SceneManager;
import com.innovote.screens.judge.JudgeDashboard;
public class VotingScreen extends VBox {

    private Judge currentJudge;
    private Idea ideaToVoteOn; 

    public VotingScreen(Judge judge, Idea idea) {
        this.currentJudge = judge;
        this.ideaToVoteOn = idea;

        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Submit Your Vote for Idea:");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label ideaTitle = new Label("Title: " + ideaToVoteOn.getTitle());
        ideaTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label ideaDescription = new Label("Description: " + ideaToVoteOn.getDescription());
        ideaDescription.setWrapText(true); 
        ideaDescription.setMaxWidth(500); 

        Label ideaCategory = new Label("Category: " + ideaToVoteOn.getCategory());
        Label ideaAuthor = new Label("Submitted by: " + ideaToVoteOn.getParticipant().getUsername());

        GridPane voteFormGrid = new GridPane();
        voteFormGrid.setHgap(10);
        voteFormGrid.setVgap(10);
        voteFormGrid.setPadding(new Insets(20, 0, 0, 0));

        TextField scoreField = new TextField();
        scoreField.setPromptText("Score (1-10)");
        scoreField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                scoreField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Your Comments (optional)");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(5);
        commentArea.setMaxWidth(400); 

        voteFormGrid.add(new Label("Score:"), 0, 0);
        voteFormGrid.add(scoreField, 1, 0);
        voteFormGrid.add(new Label("Comment:"), 0, 1);
        voteFormGrid.add(commentArea, 1, 1);

        Button submitVoteButton = new Button("Submit Vote");
        submitVoteButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 10 20;");
        submitVoteButton.setOnAction(e -> {
            try {
                int score = Integer.parseInt(scoreField.getText());
                String comment = commentArea.getText();

                VotingService.submitVote(currentJudge, ideaToVoteOn, score, comment);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Your vote for '" + ideaToVoteOn.getTitle() + "' has been submitted!");
                
                SceneManager.switchToScreen(new JudgeDashboard(currentJudge));

            } catch (NumberFormatException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Invalid Input", "Please enter a valid number for the score (1-10).");
            } catch (VotingException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Voting Error", ex.getMessage());
            }
        });

        Button backButton = new Button("Back to Dashboard");
        backButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;");
        backButton.setOnAction(e -> {
            SceneManager.switchToScreen(new JudgeDashboard(currentJudge));
        });

        this.getChildren().addAll(
            titleLabel,
            ideaTitle,
            ideaDescription,
            ideaCategory,
            ideaAuthor,
            voteFormGrid,
            submitVoteButton,
            backButton
        );
    }
}

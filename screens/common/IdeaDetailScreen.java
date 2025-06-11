package com.innovote.screens.common;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.innovote.models.Idea;
import com.innovote.models.Vote;
import com.innovote.models.User;
import com.innovote.utils.DummyDatabase;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.List;
import java.util.stream.Collectors;

public class IdeaDetailScreen extends VBox {

    private User currentUser;
    private Idea idea;

    public IdeaDetailScreen(User user, Idea idea) {
        this.currentUser = user;
        this.idea = idea;

        this.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");

        Label screenTitle = new Label("Idea Details");
        screenTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        // Tabel untuk menampilkan detail votes
        TableView<Vote> voteTable = new TableView<>();
        voteTable.setPlaceholder(new Label("No votes yet."));
        voteTable.setPrefHeight(200);
        voteTable.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";");

        TableColumn<Vote, String> voterCol = new TableColumn<>("Voter");
        voterCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getVoter().getUsername()));
        voterCol.setPrefWidth(150);

        TableColumn<Vote, Number> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        scoreCol.setPrefWidth(80);

        TableColumn<Vote, String> commentCol = new TableColumn<>("Comment");
        commentCol.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        commentCol.setPrefWidth(300);

        voteTable.getColumns().addAll(voterCol, scoreCol, commentCol);

        // Memuat votes yang relevan dengan ide ini
        List<Vote> votesForThisIdea = DummyDatabase.getVotes().stream()
                                        .filter(v -> v.getIdea().equals(idea))
                                        .collect(Collectors.toList());
        voteTable.setItems(FXCollections.observableArrayList(votesForThisIdea));

        Button backButton = new Button("Back to Dashboard");
        backButton.setOnAction(e -> SceneManager.switchToUserDashboard(currentUser));
        backButton.setStyle("-fx-background-color: " + Theme.ACCENT_PRIMARY + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 5;");

        VBox ideaInfo = new VBox(5,
            createInfoRow("Title:", idea.getTitle()),
            createInfoRow("Description:", idea.getDescription()),
            createInfoRow("Category:", idea.getCategory()),
            createInfoRow("Author:", idea.getParticipant().getUsername()),
            createInfoRow("Average Score:", String.format("%.1f", idea.getAverageScore()))
        );
        ideaInfo.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-padding: 15; -fx-background-radius: 10;");

        Label votesLabel = new Label("All Votes:");
        votesLabel.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        this.getChildren().addAll(
            screenTitle,
            ideaInfo,
            new Separator(),
            votesLabel,
            voteTable,
            backButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private HBox createInfoRow(String labelText, String valueText) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        Label value = createWrappedLabel(valueText);
        value.setStyle("-fx-text-fill: white;");
        HBox row = new HBox(10, label, value);
        return row;
    }

    private Label createWrappedLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(400);
        return label;
    }
}
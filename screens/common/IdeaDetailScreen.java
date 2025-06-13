package com.innovote.screens.common; // Atau sesuaikan dengan struktur paket Anda

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.innovote.models.Idea;
import com.innovote.models.Vote;
import com.innovote.models.User; // Untuk kembali ke dashboard yang benar
import com.innovote.utils.DummyDatabase; // Untuk mendapatkan votes yang terkait
import com.innovote.utils.SceneManager;
import javafx.beans.property.ReadOnlyStringWrapper; // Untuk TableView
import javafx.scene.control.Alert.AlertType;
import com.innovote.utils.AlertHelper; // Jika Anda menggunakan AlertHelper
import java.util.List;
import java.util.stream.Collectors; // Untuk koleksi stream

public class IdeaDetailScreen extends VBox {

    private User currentUser; // Bisa Judge atau Participant
    private Idea idea;

    public IdeaDetailScreen(User user, Idea idea) {
        this.currentUser = user;
        this.idea = idea;

        Label screenTitle = new Label("Idea Details");
        screenTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label ideaTitle = new Label("Title: " + idea.getTitle());
        ideaTitle.setStyle("-fx-font-weight: bold;");

        Label ideaDesc = new Label("Description: " + idea.getDescription());
        ideaDesc.setWrapText(true);

        Label ideaCategory = new Label("Category: " + idea.getCategory());
        Label ideaAuthor = new Label("Author: " + idea.getParticipant().getUsername());
        Label ideaAvgScore = new Label(String.format("Average Score: %.1f", idea.getAverageScore()));

        // Tabel untuk menampilkan detail votes
        TableView<Vote> voteTable = new TableView<>();
        voteTable.setPlaceholder(new Label("No votes yet.")); // Pesan jika tidak ada vote
        voteTable.setPrefHeight(200); // Berikan tinggi agar tidak terlalu kecil

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
        backButton.setOnAction(e -> SceneManager.switchToUserDashboard(currentUser)); // Kembali ke dashboard yang sesuai

        this.getChildren().addAll(
            screenTitle,
            new HBox(10, new Label("Title: "), ideaTitle), // Grouping label and value
            new HBox(10, new Label("Description: "), ideaDesc),
            new HBox(10, new Label("Category: "), ideaCategory),
            new HBox(10, new Label("Author: "), ideaAuthor),
            new HBox(10, new Label("Average Score: "), ideaAvgScore),
            new Separator(), // Garis pemisah
            new Label("All Votes:"),
            voteTable,
            backButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }
}

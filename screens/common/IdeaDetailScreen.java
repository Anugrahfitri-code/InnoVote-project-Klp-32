package innovote.screens.common;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.innovote.models.Idea;
import com.innovote.models.Vote;
import com.innovote.models.User;
import com.innovote.utils.DummyDatabase;
import com.innovote.utils.SceneManager;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.List;
import java.util.stream.Collectors;

public class IdeaDetailScreen extends VBox {

    private User currentUser;
    private Idea idea;

    public IdeaDetailScreen(User user, Idea idea) {
        this.currentUser = user;
        this.idea = idea;

        Label screenTitle = new Label("Idea Details");
        screenTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Tabel untuk menampilkan detail votes
        TableView<Vote> voteTable = new TableView<>();
        voteTable.setPlaceholder(new Label("No votes yet."));
        voteTable.setPrefHeight(200);

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

        this.getChildren().addAll(
            screenTitle,
            new HBox(10, new Label("Title:"), new Label(idea.getTitle())),
            new HBox(10, new Label("Description:"), createWrappedLabel(idea.getDescription())),
            new HBox(10, new Label("Category:"), new Label(idea.getCategory())),
            new HBox(10, new Label("Author:"), new Label(idea.getParticipant().getUsername())),
            new HBox(10, new Label("Average Score:"), new Label(String.format("%.1f", idea.getAverageScore()))),
            new Separator(),
            new Label("All Votes:"),
            voteTable,
            backButton
        );
        this.setSpacing(10);
        this.setPadding(new Insets(20));
    }

    private Label createWrappedLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(400); 
        return label;
    }
}
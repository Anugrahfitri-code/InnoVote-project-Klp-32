package screens.common;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import models.Idea;
import models.Vote;
import User;
import utils.DummyDatabase;
import utils.SceneManager;
import utils.Theme;
import javafx.beans.property.ReadOnlyStringWrapper;
import java.util.List;
import java.util.stream.Collectors;

public class IdeaDetailScreen extends VBox {

    private User currentUser;
    private Idea idea;

    public IdeaDetailScreen(User user, Idea idea) {
        this.currentUser = user;
        this.idea = idea;

        this.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                    Theme.BACKGROUND_PRIMARY + ", " + Theme.BACKGROUND_SECONDARY + ");");

        VBox header = createHeader();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        VBox ideaCard = createIdeaInfoCard();
        VBox votesSection = createVotesSection();

        // Action buttons section
        HBox actionButtons = createActionButtons();
        mainContent.getChildren().addAll(ideaCard, votesSection, actionButtons);
        scrollPane.setContent(mainContent);

        this.getChildren().addAll(header, scrollPane);
        this.setSpacing(0);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    private VBox createHeader() {
        VBox header = new VBox();
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20, 20, 10, 20));
        header.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";" +
                        "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                        "-fx-border-width: 0 0 2 0;");

        Label screenTitle = new Label("💡 Idea Details");
        screenTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; " +
                            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        Label subtitle = new Label("Comprehensive view of idea and community feedback");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        header.getChildren().addAll(screenTitle, subtitle);
        return header;
    }

    private VBox createIdeaInfoCard() {
        VBox ideaCard = new VBox(15);
        ideaCard.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 25;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 2);");
        // Card header
        HBox cardHeader = new HBox();
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label cardTitle = new Label("📋 Idea Information");
        cardTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                        "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scoreLabel = new Label(String.format("⭐ %.1f", idea.getAverageScore()));
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; " +
                            "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";" +
                            "-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 8 15;");

        cardHeader.getChildren().addAll(cardTitle, spacer, scoreLabel);

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(12);
        infoGrid.setAlignment(Pos.TOP_LEFT);

        addInfoToGrid(infoGrid, 0, "📝 Title:", idea.getTitle(), true);
        addInfoToGrid(infoGrid, 1, "📄 Description:", idea.getDescription(), false);
        addInfoToGrid(infoGrid, 2, "🏷 Category:", idea.getCategory(), false);
        addInfoToGrid(infoGrid, 3, "👤 Author:", idea.getParticipant().getUsername(), false);

        ideaCard.getChildren().addAll(cardHeader, separator1, infoGrid);
        return ideaCard;
    }

    private void addInfoToGrid(GridPane grid, int row, String labelText, String valueText, boolean isTitle) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";" +
                    "-fx-font-size: 14px; -fx-min-width: 120;");

        Label value = createWrappedLabel(valueText);
        if (isTitle) {
            value.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
                        "-fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            value.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
                        "-fx-font-size: 14px;");
        }

        grid.add(label, 0, row);
        grid.add(value, 1, row);

        if (grid.getColumnConstraints().isEmpty()) {
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setMinWidth(120);
            col1.setPrefWidth(120);

            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.ALWAYS);

            grid.getColumnConstraints().addAll(col1, col2);
        }
    }

    private VBox createVotesSection() {
        VBox votesSection = new VBox(15);
        votesSection.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 25;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 2);");

        HBox votesHeader = createVotesHeader();

        Separator separator2 = new Separator();
        separator2.setStyle("-fx-background-color: " + Theme.BORDER_COLOR + ";");

        TableView<Vote> voteTable = createEnhancedVoteTable();

        votesSection.getChildren().addAll(votesHeader, separator2, voteTable);
        return votesSection;
    }

    private HBox createVotesHeader() {
        HBox votesHeader = new HBox();
        votesHeader.setAlignment(Pos.CENTER_LEFT);

        Label votesTitle = new Label("🗳 Community Votes");
        votesTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " +
                            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Vote statistics
        List<Vote> votesForThisIdea = DummyDatabase.getVotes().stream()
                                        .filter(v -> v.getIdea().equals(idea))
                                        .collect(Collectors.toList());

        Label voteCount = new Label(votesForThisIdea.size() + " votes");
        voteCount.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";" +
                        "-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 6 12;");

        votesHeader.getChildren().addAll(votesTitle, spacer, voteCount);
        return votesHeader;
    }

    private TableView<Vote> createEnhancedVoteTable() {
        TableView<Vote> voteTable = new TableView<>();
        voteTable.setPlaceholder(new Label("No votes submitted yet for this idea."));
        voteTable.setPrefHeight(300);
        voteTable.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + Theme.BORDER_COLOR + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;");

        TableColumn<Vote, String> voterCol = new TableColumn<>("👤 Voter");
        voterCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getVoter().getUsername()));
        voterCol.setPrefWidth(150);
        voterCol.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Vote, Number> scoreCol = new TableColumn<>("⭐ Score");
        scoreCol.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        scoreCol.setPrefWidth(100);
        scoreCol.setStyle("-fx-alignment: CENTER;");

        scoreCol.setCellFactory(column -> new TableCell<Vote, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f", item.doubleValue())); // Format to one decimal place
                    double score = item.doubleValue();
                    if (score >= 4) { 
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); // Green for high scores
                    } else if (score >= 2) { 
                        setStyle("-fx-text-fill: " + Theme.ACCENT_PRIMARY + "; -fx-font-weight: bold;"); // Blue for medium scores
                    } else { 
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;"); // Orange for low scores
                    }
                }
            }
        });

        TableColumn<Vote, String> commentCol = new TableColumn<>("💬 Comment");
        commentCol.setCellValueFactory(cellData -> cellData.getValue().commentProperty());
        commentCol.setPrefWidth(350);
        commentCol.setStyle("-fx-alignment: CENTER-LEFT;");

        voteTable.getColumns().addAll(voterCol, scoreCol, commentCol);

        // Load votes data
        List<Vote> votesForThisIdea = DummyDatabase.getVotes().stream()
                                        .filter(v -> v.getIdea().equals(idea))
                                        .collect(Collectors.toList());
        voteTable.setItems(FXCollections.observableArrayList(votesForThisIdea));

        return voteTable;
    }

    private HBox createActionButtons() {
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));

        Button backButton = new Button("← Back to Dashboard");
        backButton.setOnAction(e -> SceneManager.switchToUserDashboard(currentUser));
        backButton.setStyle("-fx-background-color: " + Theme.ACCENT_PRIMARY + ";" +
                            "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + ";" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-radius: 25;" +
                            "-fx-padding: 12 25;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");

        backButton.setOnMouseEntered(e ->
            backButton.setStyle("-fx-background-color: " + Theme.ACCENT_PRIMARY_HOVER + ";" +
                                "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 25;" +
                                "-fx-padding: 12 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 6, 0, 0, 2);"));

        backButton.setOnMouseExited(e ->
            backButton.setStyle("-fx-background-color: " + Theme.ACCENT_PRIMARY + ";" +
                                "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + ";" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-background-radius: 25;" +
                                "-fx-padding: 12 25;" +
                                "-fx-cursor: hand;" +
                                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);"));

        actionButtons.getChildren().add(backButton);
        return actionButtons;
    }

    private Label createWrappedLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(500);
        return label;
    }

    private String adjustBrightness(String color, double factor) {
        try {
            Color c = Color.web(color);
            double red = Math.max(0, Math.min(1, c.getRed() + factor));
            double green = Math.max(0, Math.min(1, c.getGreen() + factor));
            double blue = Math.max(0, Math.min(1, c.getBlue() + factor));
            return String.format("#%02X%02X%02X",
                (int)(red * 255),
                (int)(green * 255),
                (int)(blue * 255));
        } catch (Exception e) {
            return color;
        }
    }
}
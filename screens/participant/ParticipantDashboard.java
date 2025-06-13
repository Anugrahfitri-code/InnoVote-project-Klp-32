package com.innovote.screens.participant;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // Import Pos
import javafx.collections.FXCollections;
import com.innovote.models.Idea;
import com.innovote.models.Participant;
import com.innovote.services.IdeaService;
import com.innovote.utils.SceneManager;
import com.innovote.session.Session;
import com.innovote.exceptions.IdeaException;
import com.innovote.utils.AlertHelper;
import java.util.Optional;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.ReadOnlyStringWrapper;
import com.innovote.screens.common.IdeaDetailScreen;
import javafx.scene.Node; // Import Node
import javafx.scene.effect.DropShadow; // Import DropShadow
import javafx.scene.paint.Color; // Import Color
import javafx.scene.shape.Rectangle; // Import Rectangle
import com.innovote.utils.Theme; // Import Theme

public class ParticipantDashboard extends BorderPane {

    private Participant currentParticipant;
    private TableView<Idea> ideaTable;

    public ParticipantDashboard(Participant participant) {
        this.currentParticipant = participant;

        // Set background color for the whole dashboard
        this.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");

        // --- Header Section ---
        VBox headerVBox = createHeaderSection();
        
        // --- Idea List Section ---
        StackPane ideaListSection = createIdeaListSection();

        // --- Action Buttons Section ---
        HBox actionButtonSection = createActionButtonSection();

        // Set up main layout
        setTop(headerVBox);
        setCenter(ideaListSection);
        setBottom(actionButtonSection);

        // Load ideas initially
        loadParticipantIdeas();
    }

    private VBox createHeaderSection() {
        VBox headerContainer = new VBox(10); // Adjust spacing
        headerContainer.setPadding(new Insets(25, 30, 15, 30)); // Adjusted padding
        headerContainer.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                                Theme.BACKGROUND_PRIMARY + ", " +
                                adjustBrightness(Theme.BACKGROUND_PRIMARY, -0.02) + ");");

        HBox topRow = new HBox(15); // Adjusted spacing
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("👋 Welcome, " + currentParticipant.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";"); // Adjusted font size

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        applySecondaryButtonStyle(logoutBtn); // Apply themed style
        logoutBtn.setPrefWidth(100); // Adjusted button size
        logoutBtn.setPrefHeight(35);
        logoutBtn.setOnAction(e -> {
            Session.logout();
            SceneManager.switchToAuth();
        });

        topRow.getChildren().addAll(welcomeLabel, spacer, logoutBtn);

        Label sectionTitleLabel = new Label("Your Submitted Ideas");
        sectionTitleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + Theme.ACCENT_PRIMARY + ";"); // Adjusted font size

        headerContainer.getChildren().addAll(topRow, sectionTitleLabel);
        return headerContainer;
    }

        private StackPane createIdeaListSection() {
        StackPane cardContainer = new StackPane();
        cardContainer.setPadding(new Insets(0, 30, 0, 30)); // Add padding for table

        // Background card for the table with shadow
        Rectangle background = new Rectangle();
        background.setFill(Color.web(Theme.BACKGROUND_SECONDARY));
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.widthProperty().bind(cardContainer.widthProperty().subtract(60)); // Adjust width
        background.heightProperty().bind(cardContainer.heightProperty());

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(6);
        shadow.setRadius(15);
        background.setEffect(shadow);

        ideaTable = new TableView<>();
        ideaTable.setPlaceholder(new Label("No ideas submitted yet. Click 'Submit New Idea' to start!"));
        ideaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Make columns fill width
        ideaTable.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"); // Table background transparent

        TableColumn<Idea, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        titleCol.setPrefWidth(200); // Set preferred width
        titleCol.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;"); // Adjusted font size

        TableColumn<Idea, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getDescription().length() > 60 ? // Slightly longer snippet
                cell.getValue().getDescription().substring(0, 57) + "..." :
                cell.getValue().getDescription()
        ));
        descriptionCol.setPrefWidth(300); // Set preferred width
        descriptionCol.setStyle("-fx-font-size: 12px;"); // Adjusted font size

        TableColumn<Idea, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        categoryCol.setPrefWidth(120); // Set preferred width
        categoryCol.setStyle("-fx-font-size: 12px;"); // Adjusted font size

        TableColumn<Idea, Number> scoreCol = new TableColumn<>("Avg Score");
        scoreCol.setCellValueFactory(cell -> cell.getValue().averageScoreProperty());
        scoreCol.setPrefWidth(90); // Set preferred width
        scoreCol.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-alignment: CENTER;"); // Adjusted font size and alignment

        // Kolom Aksi untuk Edit, Hapus, dan Lihat Detail
        TableColumn<Idea, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(240); // Adjusted preferred width for action buttons
        actionCol.setResizable(false); // Disable resizing for action column
        actionCol.setStyle("-fx-alignment: CENTER;"); // Center align buttons

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button detailBtn = new Button("Details"); // Changed text to be shorter
            private final HBox pane = new HBox(8, editBtn, deleteBtn, detailBtn); // Adjusted spacing

            {
                pane.setAlignment(Pos.CENTER);
                // Apply themed styles to action buttons
                applySmallPrimaryButtonStyle(editBtn, Theme.ACCENT_PRIMARY); // Green-like for edit
                applySmallSecondaryButtonStyle(deleteBtn, Theme.DANGER_COLOR); // Red-like for delete
                applySmallPrimaryButtonStyle(detailBtn, Theme.INFO_COLOR); // Blue-like for details

                editBtn.setPrefWidth(60); // Set fixed width for small buttons
                deleteBtn.setPrefWidth(60);
                detailBtn.setPrefWidth(60);

                editBtn.setOnAction(event -> {
                    Idea idea = getTableView().getItems().get(getIndex());
                    showEditIdeaDialog(idea);
                });

                deleteBtn.setOnAction(event -> {
                    Idea idea = getTableView().getItems().get(getIndex());
                    confirmAndDeleteIdea(idea);
                });

                detailBtn.setOnAction(event -> {
                    Idea idea = getTableView().getItems().get(getIndex());
                    SceneManager.switchToScreen(new IdeaDetailScreen(currentParticipant, idea));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });

        ideaTable.getColumns().addAll(titleCol, descriptionCol, categoryCol, scoreCol, actionCol);

        cardContainer.getChildren().addAll(background, ideaTable); // Add table on top of background
        StackPane.setMargin(ideaTable, new Insets(15)); // Margin for table inside card
        
        return cardContainer;
    }

    private HBox createActionButtonSection() {
        HBox bottomHBox = new HBox(15); // Adjusted spacing
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        bottomHBox.setPadding(new Insets(15, 30, 25, 30)); // Adjusted padding

        Button submitNewIdeaBtn = new Button("🚀 Submit New Idea");
        applyPrimaryButtonStyle(submitNewIdeaBtn); // Apply themed style
        submitNewIdeaBtn.setPrefWidth(180); // Adjusted button size
        submitNewIdeaBtn.setPrefHeight(45);
        submitNewIdeaBtn.setOnAction(e -> {
            SceneManager.switchToScreen(new IdeaSubmissionScreen(currentParticipant));
        });

        bottomHBox.getChildren().addAll(submitNewIdeaBtn);
        return bottomHBox;
    }

    // Metode untuk memuat ide partisipan
    private void loadParticipantIdeas() {
        try {
            ideaTable.setItems(FXCollections.observableArrayList(IdeaService.getIdeasByParticipant(currentParticipant)));
        } catch (IdeaException e) {
            AlertHelper.showAlert(AlertType.ERROR, "Error Loading Ideas", "Failed to load your ideas: " + e.getMessage());
        }
    }



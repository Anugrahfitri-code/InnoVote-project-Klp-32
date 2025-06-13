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

    // Dialog untuk mengedit ide
    private void showEditIdeaDialog(Idea idea) {
        Dialog<Idea> dialog = new Dialog<>();
        dialog.setTitle("Edit Idea");
        dialog.setHeaderText("Edit details for: " + idea.getTitle());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 30, 10, 10)); // Adjusted padding

        TextField titleField = new TextField(idea.getTitle());
        titleField.setPromptText("Idea Title");
        applyEnhancedInputStyles(titleField); // Apply enhanced style

        TextArea descriptionArea = new TextArea(idea.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4); // Adjusted row count
        descriptionArea.setPromptText("Detailed Description");
        applyEnhancedInputStyles(descriptionArea); // Apply enhanced style

        ComboBox<String> categoryComboBox = new ComboBox<>(); // Changed to ComboBox
        categoryComboBox.getItems().addAll( // Same categories as submission screen
            "🌍 Environment & Sustainability",
            "🤖 Artificial Intelligence",
            "📚 Education & Learning",
            "🏥 Healthcare & Medicine",
            "💼 Business & Finance",
            "🎨 Arts & Creative",
            "🔬 Science & Research",
            "🏠 Smart Home & IoT",
            "🚗 Transportation",
            "🍕 Food & Agriculture",
            "🎮 Gaming & Entertainment",
            "📱 Mobile Technology",
            "🔒 Security & Privacy",
            "Other"
        );
        categoryComboBox.setPromptText("Select Category");
        categoryComboBox.setValue(idea.getCategory()); // Set current category
        applyComboBoxStyles(categoryComboBox); // Apply combo box style

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryComboBox, 1, 2); // Use ComboBox here

        dialog.getDialogPane().setContent(grid);

        // Request focus on title field
        dialog.setOnShown(e -> titleField.requestFocus());

        // Mengaktifkan tombol Save hanya jika judul dan deskripsi tidak kosong
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(titleField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty() || categoryComboBox.getValue() == null);

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || descriptionArea.getText().trim().isEmpty() || categoryComboBox.getValue() == null);
        });
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(titleField.getText().trim().isEmpty() || newValue.trim().isEmpty() || categoryComboBox.getValue() == null);
        });
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(titleField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty() || newValue == null);
        });


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String category = categoryComboBox.getValue().replaceAll("^[^\\p{L}\\d\\s]+\\s*", ""); // Clean category
                    IdeaService.editIdea(idea.getId(), titleField.getText(), descriptionArea.getText(), category);
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Idea '" + titleField.getText() + "' updated successfully.");
                    loadParticipantIdeas(); // Reload ideas to reflect changes
                } catch (IdeaException e) {
                    AlertHelper.showAlert(AlertType.ERROR, "Edit Idea Error", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // Konfirmasi dan hapus ide
    private void confirmAndDeleteIdea(Idea idea) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Idea: " + idea.getTitle());
        alert.setContentText("Are you sure you want to delete this idea? This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                IdeaService.deleteIdea(idea.getId());
                AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Idea '" + idea.getTitle() + "' deleted successfully.");
                loadParticipantIdeas(); // Reload ideas
            } catch (IdeaException e) {
                AlertHelper.showAlert(AlertType.ERROR, "Delete Idea Error", e.getMessage());
            }
        }
    }

    // --- Common Styling Methods (copied and adjusted from IdeaSubmissionScreen) ---

    // Primary Button Style (for Submit New Idea)
    private void applyPrimaryButtonStyle(Button button) {
        String baseStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 22; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);",
            Theme.ACCENT_PRIMARY, adjustBrightness(Theme.ACCENT_PRIMARY, -0.1)
        );

        String hoverStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 10 22; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4); " +
            "-fx-scale-y: 1.03; -fx-scale-x: 1.03;", // Slightly reduced scale effect
            Theme.ACCENT_PRIMARY_HOVER, adjustBrightness(Theme.ACCENT_PRIMARY_HOVER, -0.1)
        );

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }
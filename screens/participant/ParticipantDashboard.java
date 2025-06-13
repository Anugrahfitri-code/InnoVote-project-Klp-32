package com.innovote.screens.participant;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
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
import javafx.scene.layout.Priority; // <-- TAMBAHKAN INI
import javafx.scene.Node; // <-- TAMBAHKAN INI UNTUK MENGAKSES TOMBOL DIALOG
import com.innovote.screens.participant.IdeaSubmissionScreen; // <-- Tambahkan ini

public class ParticipantDashboard extends BorderPane {

    private Participant currentParticipant;
    private TableView<Idea> ideaTable;

    public ParticipantDashboard(Participant participant) {
        this.currentParticipant = participant;

        // --- Header Section ---
        Label welcomeLabel = new Label("Welcome, " + participant.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            SceneManager.switchToAuth();
        });

        HBox topHBox = new HBox(10);
        topHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topHBox.getChildren().addAll(welcomeLabel, new Region(), logoutBtn);
        HBox.setHgrow(new Region(), Priority.ALWAYS);

        Label sectionTitleLabel = new Label("Your Submitted Ideas");
        sectionTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Button submitNewIdeaBtn = new Button("Submit New Idea");
        submitNewIdeaBtn.setOnAction(e -> {
            SceneManager.switchToScreen(new IdeaSubmissionScreen(currentParticipant));
        });

        HBox bottomHBox = new HBox(10);
        bottomHBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        bottomHBox.getChildren().addAll(submitNewIdeaBtn);


        VBox headerVBox = new VBox(10);
        headerVBox.getChildren().addAll(topHBox, sectionTitleLabel);
        headerVBox.setPadding(new Insets(20, 20, 10, 20));


        // --- Idea List ---
        ideaTable = new TableView<>();

        TableColumn<Idea, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        titleCol.setPrefWidth(200);

        TableColumn<Idea, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
            cell.getValue().getDescription().length() > 50 ?
            cell.getValue().getDescription().substring(0, 47) + "..." :
            cell.getValue().getDescription()
        ));
        descriptionCol.setPrefWidth(300);

        TableColumn<Idea, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        categoryCol.setPrefWidth(100);

        TableColumn<Idea, Number> scoreCol = new TableColumn<>("Avg Score");
        scoreCol.setCellValueFactory(cell -> cell.getValue().averageScoreProperty());
        scoreCol.setPrefWidth(80);

        // Kolom Aksi untuk Edit, Hapus, dan Lihat Detail
        TableColumn<Idea, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(220);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final Button detailBtn = new Button("View Details");
            private final HBox pane = new HBox(5, editBtn, deleteBtn, detailBtn);

            {
                // Styling tombol
                editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                detailBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

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

        // Set up main layout
        setTop(headerVBox);
        setCenter(ideaTable);
        setBottom(bottomHBox);

        BorderPane.setMargin(ideaTable, new Insets(0, 20, 10, 20));
        BorderPane.setMargin(bottomHBox, new Insets(10, 20, 20, 20));

        // Load ideas initially
        loadParticipantIdeas();
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
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(idea.getTitle());
        TextArea descriptionArea = new TextArea(idea.getDescription());
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(5);
        TextField categoryField = new TextField(idea.getCategory());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Mengaktifkan tombol Save hanya jika judul dan deskripsi tidak kosong
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(titleField.getText().trim().isEmpty() || descriptionArea.getText().trim().isEmpty());

        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty() || descriptionArea.getText().trim().isEmpty());
        });
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(titleField.getText().trim().isEmpty() || newValue.trim().isEmpty());
        });


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    IdeaService.editIdea(idea.getId(), titleField.getText(), descriptionArea.getText(), categoryField.getText());
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Idea '" + titleField.getText() + "' updated successfully.");
                    loadParticipantIdeas();
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
                loadParticipantIdeas();
            } catch (IdeaException e) {
                AlertHelper.showAlert(AlertType.ERROR, "Delete Idea Error", e.getMessage());
            }
        }
    }
}
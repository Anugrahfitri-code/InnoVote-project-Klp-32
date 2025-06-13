package screens.participant;

import java.util.Optional;

import exceptions.IdeaException;
import models.Idea;
import models.Participant;
import screens.common.IdeaDetailScreen;
import services.IdeaService;
import session.Session;
import utils.AlertHelper;
import utils.SceneManager;
import utils.Theme;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ParticipantDashboard extends BorderPane {

    private Participant currentParticipant;
    private TableView<Idea> ideaTable;

    public ParticipantDashboard(Participant participant) {
        this.currentParticipant = participant;

        this.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");

        VBox headerVBox = createHeaderSection();

        StackPane ideaListSection = createIdeaListSection();
        HBox actionButtonSection = createActionButtonSection();

        setTop(headerVBox);
        setCenter(ideaListSection);
        setBottom(actionButtonSection);

        loadParticipantIdeas();
    }

    private VBox createHeaderSection() {
        VBox headerContainer = new VBox(10);
        headerContainer.setPadding(new Insets(25, 30, 15, 30));
        headerContainer.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                                 Theme.BACKGROUND_PRIMARY + ", " +
                                 adjustBrightness(Theme.BACKGROUND_PRIMARY, -0.02) + ");");

        HBox topRow = new HBox(15);
        topRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("👋 Welcome, " + currentParticipant.getUsername() + "!");
        welcomeLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = createStyledButton("Logout", "negative");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            SceneManager.switchToAuth();
        });

        topRow.getChildren().addAll(welcomeLabel, spacer, logoutBtn);

        Label sectionTitleLabel = new Label("Your Submitted Ideas");
        sectionTitleLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + Theme.ACCENT_PRIMARY + ";"
        );

        headerContainer.getChildren().addAll(topRow, sectionTitleLabel);
        return headerContainer;
    }

    @SuppressWarnings("deprecation")
    private StackPane createIdeaListSection() {
        StackPane cardContainer = new StackPane();
        cardContainer.setPadding(new Insets(0, 30, 0, 30));

        Rectangle background = new Rectangle();
        background.setFill(Color.web(Theme.BACKGROUND_SECONDARY));
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.widthProperty().bind(cardContainer.widthProperty().subtract(cardContainer.getPadding().getLeft() + cardContainer.getPadding().getRight()));
        background.heightProperty().bind(cardContainer.heightProperty());

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(6);
        shadow.setRadius(15);
        background.setEffect(shadow);

        ideaTable = new TableView<>();
        ideaTable.setPlaceholder(new Label("No ideas submitted yet. Click 'Submit New Idea' to start!"));
        ideaTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ideaTable.setPrefHeight(400);

        ideaTable.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        String tableHeaderStyle = "-fx-background-color: " + adjustBrightness(Theme.BACKGROUND_SECONDARY, -0.1) + ";" +
                                  "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";" +
                                  "-fx-font-weight: bold;" +
                                  "-fx-font-size: 13px;" +
                                  "-fx-alignment: CENTER_LEFT; ";

        ideaTable.lookupAll(".column-header").forEach(node -> node.setStyle(tableHeaderStyle));
        Node filler = ideaTable.lookup(".filler");
        if (filler != null) {
            filler.setStyle("-fx-background-color: " + adjustBrightness(Theme.BACKGROUND_SECONDARY, -0.1) + ";");
        }


        TableColumn<Idea, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        titleCol.setPrefWidth(200);
        styleTableCell(titleCol, Theme.BACKGROUND_TERTIARY, Theme.TEXT_PRIMARY);

        TableColumn<Idea, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
                cell.getValue().getDescription().length() > 50 ?
                cell.getValue().getDescription().substring(0, 47) + "..." :
                cell.getValue().getDescription()
        ));
        descriptionCol.setPrefWidth(300);
        styleTableCell(descriptionCol, Theme.BACKGROUND_TERTIARY, Theme.TEXT_SECONDARY);

        TableColumn<Idea, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        categoryCol.setPrefWidth(120);
        styleTableCell(categoryCol, Theme.BACKGROUND_TERTIARY, Theme.TEXT_SECONDARY);

        TableColumn<Idea, Number> scoreCol = new TableColumn<>("Avg Score");
        scoreCol.setCellValueFactory(cell -> cell.getValue().averageScoreProperty());
        scoreCol.setPrefWidth(100);
        scoreCol.setCellFactory(column -> new TableCell<Idea, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-border-color: " + Theme.BORDER_COLOR + ";");
                } else {
                    if (item.doubleValue() >= 0) {
                         setText(String.format("%.1f", item.doubleValue()));
                    } else {
                        setText("N/A");
                    }
                    setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-text-fill: " + Theme.ACCENT_PRIMARY + "; -fx-font-weight: bold; -fx-alignment: CENTER; -fx-border-color: " + Theme.BORDER_COLOR + ";");
                }
            }
        });


        TableColumn<Idea, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setPrefWidth(250);
        actionCol.setSortable(false);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = createStyledButton("Edit", "neutral");
            private final Button deleteBtn = createStyledButton("Delete", "negative");
            private final Button detailBtn = createStyledButton("Details", "primary");
            private final HBox pane = new HBox(8, editBtn, deleteBtn, detailBtn);

            {
                pane.setAlignment(javafx.geometry.Pos.CENTER);
                editBtn.setPrefSize(60, 25);
                deleteBtn.setPrefSize(60, 25);
                detailBtn.setPrefSize(60, 25);

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
                    setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-border-color: " + Theme.BORDER_COLOR + ";");
                } else {
                    setGraphic(pane);
                    setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-alignment: CENTER; -fx-border-color: " + Theme.BORDER_COLOR + ";");
                }
            }
        });

        ideaTable.getColumns().addAll(titleCol, descriptionCol, categoryCol, scoreCol, actionCol);

        cardContainer.getChildren().addAll(background, ideaTable);
        StackPane.setMargin(ideaTable, new Insets(15));

        return cardContainer;
    }

    private HBox createActionButtonSection() {
        HBox bottomHBox = new HBox(15);
        bottomHBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        bottomHBox.setPadding(new Insets(15, 30, 25, 30));
        Button submitNewIdeaBtn = createStyledButton("🚀 Submit New Idea", "primary");
        submitNewIdeaBtn.setPrefWidth(180);
        submitNewIdeaBtn.setPrefHeight(45);
        submitNewIdeaBtn.setOnAction(e -> {
            SceneManager.switchToScreen(new IdeaSubmissionScreen(currentParticipant));
        });

        bottomHBox.getChildren().addAll(submitNewIdeaBtn);
        return bottomHBox;
    }

    private Button createStyledButton(String text, String type) {
        Button button = new Button(text);
        button.setPrefHeight(35);
        button.setMinWidth(80);

        String baseStyle =
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand; ";

        switch (type) {
            case "primary":
                button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_PRIMARY + "; " +
                    "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";");

                button.setOnMouseEntered(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_PRIMARY_HOVER + "; " +
                    "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY_HOVER + ";"));

                button.setOnMouseExited(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_PRIMARY + "; " +
                    "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + ";"));
                break;

            case "negative":
                button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEGATIVE + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";");

                button.setOnMouseEntered(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEGATIVE_HOVER + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";"));

                button.setOnMouseExited(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEGATIVE + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";"));
                break;

            case "neutral":
            default:
                button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEUTRAL + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";");

                button.setOnMouseEntered(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEUTRAL_HOVER + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";"));

                button.setOnMouseExited(e -> button.setStyle(baseStyle +
                    "-fx-background-color: " + Theme.ACCENT_NEUTRAL + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.BORDER_COLOR + ";"));
                break;
        }

        return button;
    }

    private <T> void styleTableCell(TableColumn<Idea, T> column, String bgColor, String textColor) {
        column.setCellFactory(col -> new TableCell<Idea, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 0 1 1 0;");
                } else {
                    if (column.getText().equals("Description") && item instanceof String) {
                        String descriptionText = (String) item;
                        setText(descriptionText.length() > 50 ? descriptionText.substring(0, 47) + "..." : descriptionText);
                    } else {
                        setText(item.toString());
                    }
                    setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 0 1 1 0;");
                }
            }
        });
        column.getStyleClass().add("table-column-header");
        column.setStyle("-fx-background-color: " + adjustBrightness(Theme.BACKGROUND_SECONDARY, -0.1) + "; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 0 1 1 0;");
    }

    private void styleTableView() {
        ideaTable.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-width: 1; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10;"
        );

        ideaTable.setRowFactory(tv -> {
            javafx.scene.control.TableRow<Idea> row = new javafx.scene.control.TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";");
                } else {
                    row.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";");
                }
            });

            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: " + Theme.ACCENT_NEUTRAL + ";");
                }
            });

            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";");
                }
            });
            return row;
        });
    }

    private void loadParticipantIdeas() {
        try {
            ideaTable.setItems(FXCollections.observableArrayList(IdeaService.getIdeasByParticipant(currentParticipant)));
        } catch (IdeaException e) {
            AlertHelper.showAlert(AlertType.ERROR, "Error Loading Ideas", "Failed to load your ideas: " + e.getMessage());
        }
    }

    private void showEditIdeaDialog(Idea idea) {
        Dialog<Idea> dialog = new Dialog<>();
        dialog.setTitle("Edit Idea");
        dialog.setHeaderText("Edit details for: " + idea.getTitle());

        dialog.getDialogPane().setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";"
        );
        Node headerLabel = dialog.getDialogPane().lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: bold;");
        }

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 150, 15, 15));
        grid.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + ";");

        TextField titleField = createStyledTextField(idea.getTitle());
        TextArea descriptionArea = createStyledTextArea(idea.getDescription());
        ComboBox<String> categoryComboBox = createStyledCategoryComboBox(idea.getCategory());

        Label titleLabel = createStyledLabel("Title:");
        Label descLabel = createStyledLabel("Description:");
        Label categoryLabel = createStyledLabel("Category:");

        grid.add(titleLabel, 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(categoryLabel, 0, 2);
        grid.add(categoryComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        Node cancelButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);

        if (saveButton instanceof Button) {
             styleDialogButton((Button) saveButton, "primary");
        }
        if (cancelButton instanceof Button) {
            styleDialogButton((Button) cancelButton, "negative");
        }

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

        // *** FIX STARTS HERE ***
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String category = categoryComboBox.getValue().replaceAll("^[^\\p{L}\\d\\s]+\\s*", "");
                    IdeaService.editIdea(idea.getId(), titleField.getText(), descriptionArea.getText(), category);
                    AlertHelper.showAlert(AlertType.INFORMATION, "Success", "Idea '" + titleField.getText() + "' updated successfully.");
                    loadParticipantIdeas();
                    return idea; // IMPORTANT: Return a non-null result when save is successful
                } catch (IdeaException e) {
                    AlertHelper.showAlert(AlertType.ERROR, "Edit Idea Error", e.getMessage());
                }
            }
            return null; // Return null if cancel or an error occurred during save
        });
        // *** FIX ENDS HERE ***

        dialog.showAndWait();
    }

    private TextField createStyledTextField(String text) {
        TextField field = new TextField(text);
        field.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 8;"
        );
        return field;
    }

    private TextArea createStyledTextArea(String text) {
        TextArea area = new TextArea(text);
        area.setWrapText(true);
        area.setPrefRowCount(4);
        area.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-radius: 4; " +
            "-fx-background-radius: 4; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 8;"
        );
        return area;
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px;"
        );
        return label;
    }

    private ComboBox<String> createStyledCategoryComboBox(String selectedValue) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
            "🌍 Environment & Sustainability", "🤖 Artificial Intelligence", "📚 Education & Learning",
            "🏥 Healthcare & Medicine", "💼 Business & Finance", "🎨 Arts & Creative",
            "🔬 Science & Research", "🏠 Smart Home & IoT", "🚗 Transportation",
            "🍕 Food & Agriculture", "🎮 Gaming & Entertainment", "📱 Mobile Technology",
            "🔒 Security & Privacy", "Other"
        );
        comboBox.setPromptText("Select Category");
        comboBox.setValue(selectedValue);
        comboBox.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-width: 1; " +
            "-fx-background-radius: 4; " +
            "-fx-border-radius: 4; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 6;"
        );
        return comboBox;
    }


    private void styleDialogButton(Button button, String type) {
        if ("primary".equals(type)) {
            button.setStyle(
                "-fx-background-color: " + Theme.ACCENT_PRIMARY + "; " +
                "-fx-text-fill: " + Theme.BACKGROUND_PRIMARY + "; " +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: " + Theme.ACCENT_NEGATIVE + "; " +
                "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                "-fx-font-weight: bold; " +
                "-fx-border-radius: 4; " +
                "-fx-background-radius: 4;"
            );
        }
    }

    private void confirmAndDeleteIdea(Idea idea) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Delete Idea: " + idea.getTitle());
        alert.setContentText("Are you sure you want to delete this idea? This action cannot be undone.");

        alert.getDialogPane().setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";"
        );
        
        Node headerLabel = alert.getDialogPane().lookup(".header-panel .label");
        if (headerLabel != null) {
            headerLabel.setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: bold;");
        }
        Node contentLabel = alert.getDialogPane().lookup(".content.label");
        if (contentLabel != null) {
            contentLabel.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        }

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
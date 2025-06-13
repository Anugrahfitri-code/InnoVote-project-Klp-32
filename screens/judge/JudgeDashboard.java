package com.innovote.screens.judge;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.innovote.exceptions.IdeaException;
import com.innovote.models.Idea;
import com.innovote.models.Judge;
import com.innovote.screens.common.IdeaDetailScreen;
import com.innovote.services.IdeaService;
import com.innovote.session.Session;
import com.innovote.utils.AlertHelper;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class JudgeDashboard extends BorderPane {

    private Judge currentJudge;
    private TableView<Idea> ideaTable;
    private List<Idea> allIdeas;
    private ProgressIndicator loadingIndicator;
    private StackPane contentPane;

    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortOrder;
    private Label statsLabel;

    public JudgeDashboard(Judge judge) {
        this.currentJudge = judge;
        
        this.setStyle("-fx-background-color: linear-gradient(to bottom, " + Theme.BACKGROUND_PRIMARY + ", " + Theme.BACKGROUND_SECONDARY + ");");
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#00000020"));
        shadow.setOffsetY(2);
        shadow.setRadius(10);
        this.setEffect(shadow);
        
        setupHeader();
        setupContent();
        
        addEntranceAnimation();
        
        loadAllIdeas();
    }

    private void setupHeader() {
        HBox welcomeSection = new HBox(15);
        welcomeSection.setAlignment(Pos.CENTER_LEFT);
        
        Circle avatar = new Circle(25);
        avatar.setFill(Color.web(Theme.ACCENT_PRIMARY));
        avatar.setStroke(Color.web(Theme.ACCENT_PRIMARY_HOVER));
        avatar.setStrokeWidth(2);
        
        VBox welcomeText = new VBox(2);
        Label welcomeLabel = new Label("Welcome back!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");
        
        Label judgeNameLabel = new Label(currentJudge.getUsername());
        judgeNameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        judgeNameLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        
        welcomeText.getChildren().addAll(welcomeLabel, judgeNameLabel);
        welcomeSection.getChildren().addAll(avatar, welcomeText);

        Button refreshBtn = createModernButton("🔄 Refresh", Theme.ACCENT_NEUTRAL, Theme.ACCENT_NEUTRAL_HOVER);
        refreshBtn.setOnAction(e -> {
            addButtonClickAnimation(refreshBtn);
            loadAllIdeas();
        });

        Button logoutBtn = createModernButton("👋 Logout", "#e74c3c", "#c0392b");
        logoutBtn.setOnAction(e -> {
            addButtonClickAnimation(logoutBtn);
            Session.logout();
            SceneManager.switchToAuth();
        });

        setupStatsSection();

        HBox topHeader = new HBox(20);
        topHeader.setAlignment(Pos.CENTER_LEFT);
        topHeader.setPadding(new Insets(25, 30, 20, 30));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox buttonGroup = new HBox(10);
        buttonGroup.setAlignment(Pos.CENTER_RIGHT);
        buttonGroup.getChildren().addAll(refreshBtn, logoutBtn);
        
        topHeader.getChildren().addAll(welcomeSection, spacer, buttonGroup);
        
        VBox filterSection = new VBox(15);
        filterSection.setPadding(new Insets(0, 30, 20, 30));
        
        HBox titleRow = new HBox(15);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionTitle = new Label("💡 Ideas to Review");
        sectionTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        
        titleRow.getChildren().addAll(sectionTitle, statsLabel);
        
        setupFilterBar();
        filterSection.getChildren().addAll(titleRow, createFilterBarNode());
        
        VBox headerContainer = new VBox();
        headerContainer.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + "; -fx-background-radius: 15 15 0 0;");
        headerContainer.getChildren().addAll(topHeader, filterSection);
        
        setTop(headerContainer);
    }

    private void setupStatsSection() {
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-padding: 8 12; -fx-background-radius: 15;");
    }

    private void updateStats() {
        if (allIdeas != null) {
            int totalIdeas = allIdeas.size();
            double avgScore = allIdeas.stream()
                .mapToDouble(Idea::getAverageScore)
                .average()
                .orElse(0.0);
            
            statsLabel.setText(String.format("📊 %d ideas • Avg: %.1f", totalIdeas, avgScore));
        }
    }

    private Button createModernButton(String text, String baseColor, String hoverColor) {
        Button button = new Button(text);
        String style = String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 10 20; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);",
            baseColor
        );
        
        button.setStyle(style);
        button.setOnMouseEntered(e -> {
            button.setStyle(style.replace(baseColor, hoverColor));
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(style);
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return button;
    }

    private void setupFilterBar() {
        searchField = new TextField();
        searchField.setPromptText("🔍 Search by title or description...");
        applyModernInputStyles(searchField);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("📁 Filter by Category");
        categoryFilter.getItems().addAll("All", "Technology", "Social", "Environment", "Business", "Health", "Education", "Other");
        categoryFilter.getSelectionModel().select("All");
        applyModernInputStyles(categoryFilter);
        categoryFilter.setOnAction(e -> applyFiltersAndSort());
        styleComboBox(categoryFilter);

        sortOrder = new ComboBox<>();
        sortOrder.setPromptText("📊 Sort By");
        sortOrder.getItems().addAll("Highest Score", "Lowest Score", "Title (A-Z)", "Title (Z-A)");
        sortOrder.getSelectionModel().select("Highest Score");
        applyModernInputStyles(sortOrder);
        sortOrder.setOnAction(e -> applyFiltersAndSort());
        styleComboBox(sortOrder);
    }

    private void styleComboBox(ComboBox<String> comboBox) {
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(comboBox.getPromptText());
                } else {
                    setText(item);
                }
                setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: 500;");
            }
        });
    }

    private Node createFilterBarNode() {
        HBox filterBar = new HBox(15);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(15));
        filterBar.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-background-radius: 10;");
        
        searchField.setPrefWidth(300);
        categoryFilter.setPrefWidth(200);
        sortOrder.setPrefWidth(200);
        
        filterBar.getChildren().addAll(searchField, categoryFilter, sortOrder);
        return filterBar;
    }

    private void setupContent() {
        contentPane = new StackPane();
        
        // Loading indicator
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setStyle("-fx-accent: " + Theme.ACCENT_PRIMARY + ";");
        loadingIndicator.setVisible(false);
        
        setupTable();
        
        contentPane.getChildren().addAll(ideaTable, loadingIndicator);
        
        VBox contentContainer = new VBox();
        contentContainer.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + "; -fx-background-radius: 0 0 15 15;");
        contentContainer.getChildren().add(contentPane);
        
        setCenter(contentContainer);
        BorderPane.setMargin(contentContainer, new Insets(0, 15, 15, 15));
    }

    private void setupTable() {
        ideaTable = new TableView<>();
        ideaTable.setPlaceholder(new Label("📝 No ideas available for review yet.\nCheck back later!"));
        ideaTable.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );

        ideaTable.setRowFactory(tv -> {
            TableRow<Idea> row = new TableRow<>();
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-background-radius: 5;");
                }
            });
            row.setOnMouseExited(e -> row.setStyle(""));
            return row;
        });

        TableColumn<Idea, String> titleCol = createStyledTableColumn("💡 Title", 250);
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        titleCol.setCellFactory(col -> createTitleCell());

        TableColumn<Idea, String> authorCol = createStyledTableColumn("👤 Author", 150);
        authorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getParticipant().getUsername()));

        TableColumn<Idea, String> descriptionCol = createStyledTableColumn("📄 Description", 300);
        descriptionCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
            cell.getValue().getDescription().length() > 80 ?
            cell.getValue().getDescription().substring(0, 77) + "..." :
            cell.getValue().getDescription()
        ));

        TableColumn<Idea, String> categoryCol = createStyledTableColumn("📁 Category", 120);
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());
        categoryCol.setCellFactory(col -> createCategoryCell());

        TableColumn<Idea, Number> scoreCol = createStyledTableColumn("⭐ Score", 100);
        scoreCol.setCellValueFactory(cell -> cell.getValue().averageScoreProperty());
        scoreCol.setCellFactory(column -> createScoreCell());

        TableColumn<Idea, Void> actionCol = createStyledTableColumn("🎯 Actions", 250);
        actionCol.setCellFactory(param -> createActionCell());

        ideaTable.getColumns().addAll(titleCol, authorCol, descriptionCol, categoryCol, scoreCol, actionCol);
        
        ideaTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                Node header = ideaTable.lookup(".column-header-background");
                if (header != null) {
                    header.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + "; -fx-background-radius: 10 10 0 0;");
                }
            }
        });
    }

    private <T> TableColumn<Idea, T> createStyledTableColumn(String title, double width) {
        TableColumn<Idea, T> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setStyle("-fx-alignment: CENTER_LEFT; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        return column;
    }

    private TableCell<Idea, String> createTitleCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    setTooltip(new Tooltip(item));
                    setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-weight: bold;");
                }
            }
        };
    }

    private TableCell<Idea, String> createCategoryCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String categoryColor = getCategoryColor(item);
                    setStyle("-fx-text-fill: " + categoryColor + "; -fx-font-weight: bold; -fx-background-color: " + categoryColor + "20; -fx-background-radius: 10; -fx-padding: 5;");
                }
            }
        };
    }

    private String getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "technology": return "#3498db";
            case "social": return "#e74c3c";
            case "environment": return "#27ae60";
            case "business": return "#f39c12";
            case "health": return "#e91e63";
            case "education": return "#9b59b6";
            default: return "#95a5a6";
        }
    }

    private TableCell<Idea, Number> createScoreCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    double score = item.doubleValue();
                    setText(String.format("%.1f", score));
                    
                    String scoreColor = getScoreColor(score);
                    setStyle("-fx-text-fill: " + scoreColor + "; -fx-font-weight: bold; -fx-alignment: center;");
                }
            }
        };
    }

    private String getScoreColor(double score) {
        if (score >= 4.0) return "#27ae60";
        else if (score >= 3.0) return "#f39c12";
        else if (score >= 2.0) return "#e67e22";
        else return "#e74c3c";
    }

    private TableCell<Idea, Void> createActionCell() {
        return new TableCell<>() {
            private final Button voteBtn = createActionButton("🗳️ Vote", Theme.ACCENT_PRIMARY);
            private final Button detailBtn = createActionButton("👁️ Details", Theme.ACCENT_NEUTRAL);
            private final HBox pane = new HBox(10, voteBtn, detailBtn);
            
            {
                pane.setAlignment(Pos.CENTER);
                voteBtn.setOnAction(event -> handleActionEvent(idea -> {
                    addButtonClickAnimation(voteBtn);
                    SceneManager.switchToScreen(new VotingScreen(currentJudge, idea));
                }));
                detailBtn.setOnAction(event -> handleActionEvent(idea -> {
                    addButtonClickAnimation(detailBtn);
                    SceneManager.switchToScreen(new IdeaDetailScreen(currentJudge, idea));
                }));
            }
            
            private void handleActionEvent(java.util.function.Consumer<Idea> action) {
                if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                    Idea idea = getTableView().getItems().get(getIndex());
                    if (idea != null) {
                        action.accept(idea);
                    }
                }
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        };
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 6 15; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 12px;",
            color
        ));
        
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.1);
            scale.setToY(1.1);
            scale.play();
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return button;
    }

    private void applyModernInputStyles(Node node) {
        node.setStyle(
            "-fx-background-color: " + Theme.BACKGROUND_PRIMARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + Theme.BORDER_COLOR + "; " +
            "-fx-border-width: 1px; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 10 15; " +
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);"
        );
    }

    private void loadAllIdeas() {
        showLoading(true);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), ideaTable);
        fadeOut.setToValue(0.3);
        fadeOut.setOnFinished(e -> {
            try {
                allIdeas = IdeaService.getAllIdeas();
                updateStats();
                applyFiltersAndSort();
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), ideaTable);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } catch (IdeaException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Error Loading Ideas", "Failed to load ideas: " + ex.getMessage());
            } finally {
                showLoading(false);
            }
        });
        fadeOut.play();
    }

    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        ideaTable.setDisable(show);
    }

    private void applyFiltersAndSort() {
        if (allIdeas == null) return;
        
        List<Idea> filteredList = allIdeas.stream()
            .filter(idea -> {
                String searchText = searchField.getText().toLowerCase();
                boolean matchesSearch = idea.getTitle().toLowerCase().contains(searchText) || 
                                     idea.getDescription().toLowerCase().contains(searchText);
                
                String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();
                boolean matchesCategory = "All".equals(selectedCategory) || 
                                        idea.getCategory().equalsIgnoreCase(selectedCategory);
                
                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());

        Comparator<Idea> comparator = Comparator.comparingDouble(Idea::getAverageScore).reversed();
        String selectedSort = sortOrder.getSelectionModel().getSelectedItem();
        if (selectedSort != null) {
            switch (selectedSort) {
                case "Lowest Score": 
                    comparator = Comparator.comparingDouble(Idea::getAverageScore); 
                    break;
                case "Title (A-Z)": 
                    comparator = Comparator.comparing(Idea::getTitle, String.CASE_INSENSITIVE_ORDER); 
                    break;
                case "Title (Z-A)": 
                    comparator = Comparator.comparing(Idea::getTitle, String.CASE_INSENSITIVE_ORDER).reversed(); 
                    break;
                case "Highest Score":
                default: 
                    break;
            }
        }
        
        filteredList.sort(comparator);
        ideaTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void addEntranceAnimation() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), this);
        slideIn.setFromY(-50);
        slideIn.setToY(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        slideIn.play();
        fadeIn.play();
    }

    private void addButtonClickAnimation(Button button) {
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(50), button);
        scaleDown.setToX(0.95);
        scaleDown.setToY(0.95);
        
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(50), button);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        
        scaleDown.setOnFinished(e -> scaleUp.play());
        scaleDown.play();
    }
}

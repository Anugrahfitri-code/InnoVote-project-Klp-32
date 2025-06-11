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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class JudgeDashboard extends BorderPane {

    private Judge currentJudge;
    private TableView<Idea> ideaTable;
    private List<Idea> allIdeas;

    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortOrder;

    public JudgeDashboard(Judge judge) {
        this.currentJudge = judge;
        
        this.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");
        
        Label welcomeLabel = new Label("Welcome, " + judge.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 1px; -fx-border-radius: 4; -fx-font-weight: bold; -fx-padding: 6 12;");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle("-fx-background-color: " + Theme.ACCENT_NEGATIVE + "; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 1px; -fx-border-radius: 4; -fx-font-weight: bold; -fx-padding: 6 12;"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 1px; -fx-border-radius: 4; -fx-font-weight: bold; -fx-padding: 6 12;"));
        logoutBtn.setOnAction(e -> {
            Session.logout();
            SceneManager.switchToAuth();
        });

        Button refreshBtn = new Button("Refresh Ideas");
        applyButtonStyles(refreshBtn, Theme.ACCENT_PRIMARY, Theme.ACCENT_PRIMARY_HOVER);
        refreshBtn.setOnAction(e -> loadAllIdeas());

        HBox topHBox = new HBox(10);
        topHBox.setAlignment(Pos.CENTER_LEFT);
        Region spacer = new Region();
        topHBox.getChildren().addAll(welcomeLabel, spacer, refreshBtn, logoutBtn);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label sectionTitleLabel = new Label("Ideas to Review");
        sectionTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + Theme.TEXT_PRIMARY + ";");

        setupFilterBar();

        VBox headerVBox = new VBox(15);
        headerVBox.getChildren().addAll(topHBox, sectionTitleLabel, createFilterBarNode());
        headerVBox.setPadding(new Insets(20, 25, 20, 25));
        headerVBox.setStyle("-fx-background-color: " + Theme.BACKGROUND_PRIMARY + ";");

        setupTable();

        setTop(headerVBox);
        setCenter(ideaTable);
        BorderPane.setMargin(ideaTable, new Insets(0, 25, 25, 25));
        
        loadAllIdeas();
    }

    private void setupFilterBar() {
        searchField = new TextField();
        searchField.setPromptText("Search by title/description...");
        applyInputStyles(searchField);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by Category");
        categoryFilter.getItems().addAll("All", "Technology", "Social", "Environment", "Business", "Health", "Education", "Other");
        categoryFilter.getSelectionModel().select("All");
        applyInputStyles(categoryFilter);
        categoryFilter.setOnAction(e -> applyFiltersAndSort());
        
        styleComboBox(categoryFilter);

        sortOrder = new ComboBox<>();
        sortOrder.setPromptText("Sort By");
        sortOrder.getItems().addAll("Highest Score", "Lowest Score", "Title (A-Z)", "Title (Z-A)");
        sortOrder.getSelectionModel().select("Highest Score");
        applyInputStyles(sortOrder);
        sortOrder.setOnAction(e -> applyFiltersAndSort());
        
        styleComboBox(sortOrder);
    }
    
    private void styleComboBox(ComboBox<String> comboBox) {
        String targetTextStyle = "-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-family: 'Segoe UI Semibold';";
        
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); 
                if (empty || item == null) {
                    setText(comboBox.getPromptText());
                } else {
                    setText(item);
                    setStyle(targetTextStyle);
                }
            }
        });
    }

    private Node createFilterBarNode() {
        HBox filterSortBar = new HBox(10);
        filterSortBar.setAlignment(Pos.CENTER_LEFT);
        Label searchLabel = createFilterLabel("Search:");
        Label categoryLabel = createFilterLabel("Category:");
        Label sortLabel = createFilterLabel("Sort:");
        filterSortBar.getChildren().addAll(searchLabel, searchField, categoryLabel, categoryFilter, sortLabel, sortOrder);
        return filterSortBar;
    }
    
    private Label createFilterLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; -fx-font-weight: bold;");
        return label;
    }

    private void setupTable() {
        ideaTable = new TableView<>();
        ideaTable.setPlaceholder(new Label("No ideas available for review."));
        ideaTable.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-border-width: 0.5px;");

        ideaTable.setRowFactory(tv -> new TableRow<>());

        TableColumn<Idea, String> titleCol = createStyledTableColumn("Title", 200);
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());

        TableColumn<Idea, String> authorCol = createStyledTableColumn("Author", 120);
        authorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getParticipant().getUsername()));

        TableColumn<Idea, String> descriptionCol = createStyledTableColumn("Description", 300);
        descriptionCol.setCellValueFactory(cell -> new ReadOnlyStringWrapper(
            cell.getValue().getDescription().length() > 50 ?
            cell.getValue().getDescription().substring(0, 47) + "..." :
            cell.getValue().getDescription()
        ));

        TableColumn<Idea, String> categoryCol = createStyledTableColumn("Category", 100);
        categoryCol.setCellValueFactory(cell -> cell.getValue().categoryProperty());

        TableColumn<Idea, Number> scoreCol = createStyledTableColumn("Avg Score", 100);
        scoreCol.setCellValueFactory(cell -> cell.getValue().averageScoreProperty());
        scoreCol.setCellFactory(column -> createNumericCell());

        TableColumn<Idea, Void> actionCol = createStyledTableColumn("Action", 220); 
        actionCol.setCellFactory(param -> createActionCell());

        ideaTable.getColumns().addAll(titleCol, authorCol, descriptionCol, categoryCol, scoreCol, actionCol);

        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Node headerBackground = lookup(".column-header-background");
                if(headerBackground != null) {
                    headerBackground.setStyle("-fx-background-color: " + Theme.BACKGROUND_TERTIARY + ";");
                }
            }
        });
    }

    private <T> TableColumn<Idea, T> createStyledTableColumn(String title, double width) {
        TableColumn<Idea, T> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setStyle("-fx-alignment: CENTER_LEFT; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-font-size: 14px; -fx-font-family: 'Segoe UI Semibold';");
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.toString());
                setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
            }
        });
        return column;
    }
    
    private TableCell<Idea, Number> createNumericCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item.doubleValue()));
                }
                setAlignment(Pos.CENTER);
                setStyle("-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
            }
        };
    }
    
    private TableCell<Idea, Void> createActionCell() {
        return new TableCell<>() {
            private final Button voteBtn = new Button("Vote");
            private final Button detailBtn = new Button("View Details");
            private final HBox pane = new HBox(10, voteBtn, detailBtn);
            {
                voteBtn.setMinWidth(Region.USE_PREF_SIZE);
                detailBtn.setMinWidth(Region.USE_PREF_SIZE);
                applyButtonStyles(voteBtn, Theme.ACCENT_PRIMARY, Theme.ACCENT_PRIMARY_HOVER);
                applyButtonStyles(detailBtn, Theme.ACCENT_NEUTRAL, Theme.ACCENT_NEUTRAL_HOVER);
                pane.setAlignment(Pos.CENTER);
                voteBtn.setOnAction(event -> handleActionEvent(idea -> SceneManager.switchToScreen(new VotingScreen(currentJudge, idea))));
                detailBtn.setOnAction(event -> handleActionEvent(idea -> SceneManager.switchToScreen(new IdeaDetailScreen(currentJudge, idea))));
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

    private void applyButtonStyles(Button button, String baseColor, String hoverColor) {
        String textFill = (baseColor.equals(Theme.ACCENT_PRIMARY)) ? "#000000" : "#FFFFFF";
        String hoverTextFill = (hoverColor.equals(Theme.ACCENT_PRIMARY_HOVER)) ? "#000000" : "#FFFFFF";
        
        String style = String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 6 12;", baseColor, textFill);
        button.setStyle(style);
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 6 12;", hoverColor, hoverTextFill)));
        button.setOnMouseExited(e -> button.setStyle(style));
    }
    
    private void applyInputStyles(Node node) {
        node.setStyle("-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; -fx-border-color: " + Theme.BORDER_COLOR + "; -fx-background-radius: 4; -fx-border-radius: 4; -fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + ";");
    }
    
    private void loadAllIdeas() {
        try {
            allIdeas = IdeaService.getAllIdeas();
            applyFiltersAndSort();
        } catch (IdeaException e) {
            AlertHelper.showAlert(AlertType.ERROR, "Error Loading Ideas", "Failed to load ideas: " + e.getMessage());
        }
    }

    private void applyFiltersAndSort() {
        if (allIdeas == null) return;
        List<Idea> filteredList = allIdeas.stream()
            .filter(idea -> {
                String searchText = searchField.getText().toLowerCase();
                boolean matchesSearch = idea.getTitle().toLowerCase().contains(searchText) || idea.getDescription().toLowerCase().contains(searchText);
                String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();
                boolean matchesCategory = "All".equals(selectedCategory) || idea.getCategory().equalsIgnoreCase(selectedCategory);
                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());

        Comparator<Idea> comparator = Comparator.comparingDouble(Idea::getAverageScore).reversed();
        String selectedSort = sortOrder.getSelectionModel().getSelectedItem();
        if (selectedSort != null) {
            switch (selectedSort) {
                case "Lowest Score": comparator = Comparator.comparingDouble(Idea::getAverageScore); break;
                case "Title (A-Z)": comparator = Comparator.comparing(Idea::getTitle, String.CASE_INSENSITIVE_ORDER); break;
                case "Title (Z-A)": comparator = Comparator.comparing(Idea::getTitle, String.CASE_INSENSITIVE_ORDER).reversed(); break;
                case "Highest Score":
                default: break;
            }
        }
        filteredList.sort(comparator);
        ideaTable.setItems(FXCollections.observableArrayList(filteredList));
    }
}

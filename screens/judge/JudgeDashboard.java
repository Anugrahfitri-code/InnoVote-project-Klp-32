package com.innovote.screens.judge;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import com.innovote.models.Judge;
import com.innovote.models.Idea;
import com.innovote.session.Session;
import com.innovote.services.IdeaService;
import com.innovote.utils.SceneManager;
import com.innovote.exceptions.IdeaException;
import com.innovote.utils.AlertHelper;
import javafx.scene.control.Alert.AlertType;

import javafx.beans.property.ReadOnlyStringWrapper;
import com.innovote.screens.judge.VotingScreen; 
import com.innovote.screens.common.IdeaDetailScreen; 
import javafx.scene.layout.Priority;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class JudgeDashboard extends BorderPane {

    private Judge currentJudge;
    private TableView<Idea> ideaTable;
    private List<Idea> allIdeas; 

    private TextField searchField;
    private ComboBox<String> categoryFilter;
    private ComboBox<String> sortOrder;

    public JudgeDashboard(Judge judge) {
        this.currentJudge = judge;

        Label welcomeLabel = new Label("Welcome, " + judge.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setOnAction(e -> {
            Session.logout();
            SceneManager.switchToAuth();
        });

        Button refreshBtn = new Button("Refresh Ideas");
        refreshBtn.setOnAction(e -> loadAllIdeas()); 

        HBox topHBox = new HBox(10);
        topHBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topHBox.getChildren().addAll(welcomeLabel, new Region(), refreshBtn, logoutBtn);
        HBox.setHgrow(new Region(), Priority.ALWAYS); 

        Label sectionTitleLabel = new Label("Ideas to Review");
        sectionTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        searchField = new TextField();
        searchField.setPromptText("Search by title/description...");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersAndSort());

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by Category");
        categoryFilter.getItems().addAll("All", "Technology", "Social", "Environment", "Business", "Health", "Education", "Other");
        categoryFilter.getSelectionModel().select("All");
        categoryFilter.setOnAction(e -> applyFiltersAndSort());

        sortOrder = new ComboBox<>();
        sortOrder.setPromptText("Sort By");
        sortOrder.getItems().addAll("Highest Score", "Lowest Score", "Title (A-Z)", "Title (Z-A)");
        sortOrder.getSelectionModel().select("Highest Score");
        sortOrder.setOnAction(e -> applyFiltersAndSort());

        HBox filterSortBar = new HBox(10);
        filterSortBar.setPadding(new Insets(0, 20, 10, 20));
        filterSortBar.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Category:"), categoryFilter,
            new Label("Sort:"), sortOrder
        );
        filterSortBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox headerVBox = new VBox(10);
        headerVBox.getChildren().addAll(topHBox, sectionTitleLabel, filterSortBar);
        headerVBox.setPadding(new Insets(20, 20, 10, 20));

        ideaTable = new TableView<>();

        TableColumn<Idea, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());
        titleCol.setPrefWidth(200);

        TableColumn<Idea, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getParticipant().getUsername()));
        authorCol.setPrefWidth(120);

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

        TableColumn<Idea, Void> actionCol = new TableColumn<>("Action");
        actionCol.setPrefWidth(180);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button voteBtn = new Button("Vote");
            private final Button detailBtn = new Button("View Details");
            private final HBox pane = new HBox(5, voteBtn, detailBtn);

            {
                voteBtn.setOnAction(event -> {
                    Idea idea = getTableView().getItems().get(getIndex());
                    SceneManager.switchToScreen(new VotingScreen(currentJudge, idea));
                });

                detailBtn.setOnAction(event -> {
                    Idea idea = getTableView().getItems().get(getIndex());
                    SceneManager.switchToScreen(new IdeaDetailScreen(currentJudge, idea));
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

        ideaTable.getColumns().addAll(titleCol, authorCol, descriptionCol, categoryCol, scoreCol, actionCol);

        loadAllIdeas();

        setTop(headerVBox);
        setCenter(ideaTable);

        BorderPane.setMargin(ideaTable, new Insets(0, 20, 20, 20));
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
                boolean matchesSearch = idea.getTitle().toLowerCase().contains(searchText) ||
                                         idea.getDescription().toLowerCase().contains(searchText);

                String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();
                boolean matchesCategory = "All".equals(selectedCategory) ||
                                         idea.getCategory().equalsIgnoreCase(selectedCategory);

                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());

        Comparator<Idea> comparator = null;
        String selectedSort = sortOrder.getSelectionModel().getSelectedItem();

        switch (selectedSort) {
            case "Highest Score":
                comparator = Comparator.comparingDouble(Idea::getAverageScore).reversed();
                break;
            case "Lowest Score":
                comparator = Comparator.comparingDouble(Idea::getAverageScore);
                break;
            case "Title (A-Z)":
                comparator = Comparator.comparing(Idea::getTitle);
                break;
            case "Title (Z-A)":
                comparator = Comparator.comparing(Idea::getTitle).reversed();
                break;
            default:
                comparator = Comparator.comparingDouble(Idea::getAverageScore).reversed();
        }

        filteredList.sort(comparator);
        
        ideaTable.setItems(FXCollections.observableArrayList(filteredList));
    }
}

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
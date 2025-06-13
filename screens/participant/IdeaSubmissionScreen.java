package com.innovote.screens.participant;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import com.innovote.models.Participant;
import com.innovote.services.IdeaService;
import com.innovote.utils.AlertHelper;
import com.innovote.exceptions.IdeaException;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;

public class IdeaSubmissionScreen extends ScrollPane {

    private Participant currentParticipant;
    private TextField titleField;
    private TextArea descriptionArea;
    private ComboBox<String> categoryComboBox;
    private Label titleCountLabel;
    private Label descriptionCountLabel;

    public IdeaSubmissionScreen(Participant participant) {
        this.currentParticipant = participant;

        // Main container
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, " + 
                            Theme.BACKGROUND_PRIMARY + ", " + 
                            adjustBrightness(Theme.BACKGROUND_PRIMARY, -0.05) + ");");
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        mainContainer.setSpacing(30);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMinHeight(900);

        // Header section
        VBox headerSection = createHeaderSection();
        
        // Form card
        StackPane formCard = createFormCard();
        
        // Action buttons
        HBox buttonSection = createButtonSection();

        mainContainer.getChildren().addAll(
            headerSection,
            formCard,
            buttonSection
        );

        // Configure ScrollPane
        this.setContent(mainContainer);
        this.setFitToWidth(true);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setStyle("-fx-background-color: transparent;");
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(12);
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("💡 Submit Your Innovation");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; " +
                          "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        Label subtitleLabel = new Label("Share your brilliant idea and make it come to life");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                             "-fx-font-style: italic;");

        // Welcome message
        Label welcomeLabel = new Label("Welcome, " + currentParticipant.getUsername() + "!");
        welcomeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + Theme.ACCENT_PRIMARY + "; " +
                            "-fx-font-weight: 600; " +
                            "-fx-background-color: " + adjustBrightness(Theme.ACCENT_PRIMARY, 0.9) + "; " +
                            "-fx-padding: 8 16; -fx-background-radius: 20;");

        headerSection.getChildren().addAll(titleLabel, subtitleLabel, welcomeLabel);
        return headerSection;
    }

    private StackPane createFormCard() {
        StackPane cardContainer = new StackPane();
        cardContainer.setMaxWidth(800);
        cardContainer.setPrefWidth(800);

        // Background with shadow
        Rectangle background = new Rectangle();
        background.setFill(Color.web(Theme.BACKGROUND_SECONDARY));
        background.setArcWidth(20);
        background.setArcHeight(20);
        background.widthProperty().bind(cardContainer.widthProperty());
        background.setHeight(600);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(6);
        shadow.setRadius(15);
        background.setEffect(shadow);

        VBox formContent = new VBox(25);
        formContent.setAlignment(Pos.TOP_CENTER);
        formContent.setPadding(new Insets(40, 50, 40, 50));
        formContent.setMaxWidth(700);

        // Title section
        VBox titleSection = createTitleSection();
        
        // Description section
        VBox descriptionSection = createDescriptionSection();
        
        // Category section
        VBox categorySection = createCategorySection();

        formContent.getChildren().addAll(titleSection, descriptionSection, categorySection);
        cardContainer.getChildren().addAll(background, formContent);

        return cardContainer;
    }

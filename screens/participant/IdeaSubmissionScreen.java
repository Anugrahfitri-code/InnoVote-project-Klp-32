package screens.participant;

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
import models.Participant;
import services.IdeaService;
import utils.AlertHelper;
import exceptions.IdeaException;
import utils.SceneManager;
import utils.Theme;

public class IdeaSubmissionScreen extends ScrollPane {

    private Participant currentParticipant;
    private TextField titleField;
    private TextArea descriptionArea;
    private ComboBox<String> categoryComboBox;
    private Label titleCountLabel;
    private Label descriptionCountLabel;

    public IdeaSubmissionScreen(Participant participant) {
        this.currentParticipant = participant;

        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, " + 
                            Theme.BACKGROUND_PRIMARY + ", " + 
                            adjustBrightness(Theme.BACKGROUND_PRIMARY, -0.05) + ");");
        mainContainer.setPadding(new Insets(40, 50, 40, 50));
        mainContainer.setSpacing(30);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMinHeight(900);

        VBox headerSection = createHeaderSection();
        StackPane formCard = createFormCard();
        HBox buttonSection = createButtonSection();

        mainContainer.getChildren().addAll(
            headerSection,
            formCard,
            buttonSection
        );

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

        VBox titleSection = createTitleSection();
        VBox descriptionSection = createDescriptionSection();
        VBox categorySection = createCategorySection();

        formContent.getChildren().addAll(titleSection, descriptionSection, categorySection);
        cardContainer.getChildren().addAll(background, formContent);

        return cardContainer;
    }

    private VBox createTitleSection() {
        VBox titleSection = new VBox(8);
        titleSection.setAlignment(Pos.CENTER_LEFT);

        HBox titleHeader = new HBox(8);
        titleHeader.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("✏ Idea Title");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                        "-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        titleCountLabel = new Label("0/100");
        titleCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        titleHeader.getChildren().addAll(titleLabel, spacer, titleCountLabel);

        titleField = new TextField();
        titleField.setPromptText("Enter your innovative idea title...");
        titleField.setMaxWidth(Double.MAX_VALUE);
        titleField.setPrefHeight(45);
        applyEnhancedInputStyles(titleField);

        titleField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 100) {
                titleField.setText(oldText);
            } else {
                titleCountLabel.setText(newText.length() + "/100");
                updateCounterStyle(titleCountLabel, newText.length(), 100);
            }
        });

        Label titleHint = new Label("💡 Make it catchy and descriptive!");
        titleHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                        "-fx-font-style: italic;");

        titleSection.getChildren().addAll(titleHeader, titleField, titleHint);
        return titleSection;
    }

    private VBox createDescriptionSection() {
        VBox descriptionSection = new VBox(8);
        descriptionSection.setAlignment(Pos.CENTER_LEFT);

        HBox descHeader = new HBox(8);
        descHeader.setAlignment(Pos.CENTER_LEFT);

        Label descLabel = new Label("📝 Detailed Description");
        descLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                        "-fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        descriptionCountLabel = new Label("0/500");
        descriptionCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";");

        descHeader.getChildren().addAll(descLabel, spacer, descriptionCountLabel);

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Describe your idea in detail. What problem does it solve? How does it work? Why is it innovative?");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(6);
        descriptionArea.setMaxWidth(Double.MAX_VALUE);
        applyEnhancedInputStyles(descriptionArea);

        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 500) {
                descriptionArea.setText(oldText);
            } else {
                descriptionCountLabel.setText(newText.length() + "/500");
                updateCounterStyle(descriptionCountLabel, newText.length(), 500);
            }
        });

        Label descHint = new Label("🎯 Be specific about the problem you're solving and your solution approach");
        descHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                        "-fx-font-style: italic;");

        descriptionSection.getChildren().addAll(descHeader, descriptionArea, descHint);
        return descriptionSection;
    }

    private VBox createCategorySection() {
        VBox categorySection = new VBox(8);
        categorySection.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label("🏷 Category");
        categoryLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                            "-fx-font-weight: bold;");

        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(
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
        categoryComboBox.setPromptText("Select the best category for your idea");
        categoryComboBox.setMaxWidth(Double.MAX_VALUE);
        categoryComboBox.setPrefHeight(45);
        applyComboBoxStyles(categoryComboBox);

        Label categoryHint = new Label("📂 Choose the category that best fits your innovation");
        categoryHint.setStyle("-fx-font-size: 11px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                            "-fx-font-style: italic;");

        categorySection.getChildren().addAll(categoryLabel, categoryComboBox, categoryHint);
        return categorySection;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(20);
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setPadding(new Insets(20, 0, 0, 0));

        Button backButton = new Button("← Back to Dashboard");
        applySecondaryButtonStyle(backButton);
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> {
            SceneManager.switchToScreen(new ParticipantDashboard(currentParticipant));
        });

        Button submitButton = new Button("🚀 Submit Innovation");
        applyPrimaryButtonStyle(submitButton);
        submitButton.setPrefWidth(200);
        submitButton.setOnAction(e -> handleSubmission());

        buttonSection.getChildren().addAll(backButton, submitButton);
        return buttonSection;
    }

    private void handleSubmission() {
        try {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            String selectedCategory = categoryComboBox.getValue();
            
            // Validation
            if (title.isEmpty()) {
                AlertHelper.showAlert(AlertType.WARNING, "Validation Error", "Please enter an idea title.");
                titleField.requestFocus();
                return;
            }
            
            if (description.isEmpty()) {
                AlertHelper.showAlert(AlertType.WARNING, "Validation Error", "Please provide a detailed description.");
                descriptionArea.requestFocus();
                return;
            }
            
            if (selectedCategory == null) {
                AlertHelper.showAlert(AlertType.WARNING, "Validation Error", "Please select a category for your idea.");
                categoryComboBox.requestFocus();
                return;
            }

            String category = selectedCategory.replaceAll("^[^\\p{L}\\d\\s]+\\s*", "");

            IdeaService.submitIdea(currentParticipant, title, description, category);
            
            AlertHelper.showAlert(AlertType.INFORMATION, "🎉 Success!", 
                "Your innovative idea '" + title + "' has been submitted successfully!\n\n" +
                "It will now be reviewed by our expert judges. Good luck!");

            SceneManager.switchToScreen(new ParticipantDashboard(currentParticipant));

        } catch (IdeaException ex) {
            AlertHelper.showAlert(AlertType.ERROR, "Submission Error", ex.getMessage());
        }
    }

    private void updateCounterStyle(Label counter, int current, int max) {
        double percentage = (double) current / max;
        String color;
        
        if (percentage < 0.7) {
            color = Theme.TEXT_SECONDARY;
        } else if (percentage < 0.9) {
            color = "#FFA500";
        } else {
            color = "#FF6B6B";
        }
        
        counter.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
    }

    private void applyPrimaryButtonStyle(Button button) {
        String baseStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 6, 0, 0, 3);",
            Theme.ACCENT_PRIMARY, adjustBrightness(Theme.ACCENT_PRIMARY, -0.1)
        );

        String hoverStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 30; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 4); " +
            "-fx-scale-y: 1.05; -fx-scale-x: 1.05;",
            Theme.ACCENT_PRIMARY_HOVER, adjustBrightness(Theme.ACCENT_PRIMARY_HOVER, -0.1)
        );

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    private void applySecondaryButtonStyle(Button button) {
        String baseStyle = String.format(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: %s; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 30; " +
            "-fx-cursor: hand;",
            Theme.TEXT_SECONDARY, Theme.TEXT_SECONDARY
        );

        String hoverStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 15 30; " +
            "-fx-cursor: hand;",
            Theme.TEXT_SECONDARY, Theme.TEXT_SECONDARY
        );

        button.setStyle(baseStyle);
        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));
    }

    private void applyEnhancedInputStyles(Node node) {
        String baseStyle = 
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + adjustBrightness(Theme.BORDER_COLOR, 0.3) + "; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 12; " +
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
            "-fx-font-size: 14px; " +
            "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);";

        node.setStyle(baseStyle);

        node.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                node.setStyle(
                    "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 12; " +
                    "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                    "-fx-font-size: 14px; " +
                    "-fx-effect: dropshadow(gaussian, " + Theme.ACCENT_PRIMARY + ", 6, 0, 0, 0);"
                );
            } else {
                node.setStyle(baseStyle);
            }
        });
    }

    private void applyComboBoxStyles(ComboBox<String> comboBox) {
        String baseStyle = 
            "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
            "-fx-border-color: " + adjustBrightness(Theme.BORDER_COLOR, 0.3) + "; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-padding: 8; " +
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
            "-fx-font-size: 14px;";

        comboBox.setStyle(baseStyle);

        comboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                comboBox.setStyle(
                    "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + "; " +
                    "-fx-border-width: 2; " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10; " +
                    "-fx-padding: 8; " +
                    "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                    "-fx-font-size: 14px; " +
                    "-fx-effect: dropshadow(gaussian, " + Theme.ACCENT_PRIMARY + ", 6, 0, 0, 0);"
                );
            } else {
                comboBox.setStyle(baseStyle);
            }
        });
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
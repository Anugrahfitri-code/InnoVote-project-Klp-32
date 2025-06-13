package com.innovote.screens.judge;

import com.innovote.exceptions.VotingException;
import com.innovote.models.Idea;
import com.innovote.models.Judge;
import com.innovote.services.VotingService;
import com.innovote.utils.AlertHelper;
import com.innovote.utils.SceneManager;
import com.innovote.utils.Theme;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField; 
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.ColumnConstraints; 
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

public class VotingScreen extends ScrollPane {

    private Judge currentJudge;
    private Idea ideaToVoteOn;
    private Slider scoreSlider;
    private Label scoreValueLabel;
    private TextArea commentArea;

    public VotingScreen(Judge judge, Idea idea) {
        this.currentJudge = judge;
        this.ideaToVoteOn = idea;

        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, " +
                               Theme.BACKGROUND_PRIMARY + ", " +
                               adjustBrightness(Theme.BACKGROUND_PRIMARY, -0.05) + ");");
        mainContainer.setPadding(new Insets(20, 30, 20, 30));
        mainContainer.setSpacing(15); 
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setMinHeight(600); 

        VBox headerSection = createHeaderSection();

        StackPane ideaDetailsCard = createIdeaDetailsCard();

        VBox votingFormSection = createVotingFormSection();

        HBox buttonSection = createButtonSection();

        mainContainer.getChildren().addAll(
            headerSection,
            ideaDetailsCard,
            votingFormSection,
            buttonSection
        );

        this.setContent(mainContainer);
        this.setFitToWidth(true);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setStyle("-fx-background-color: transparent;");
    }

    private VBox createHeaderSection() {
        VBox headerSection = new VBox(8); 
        headerSection.setAlignment(Pos.CENTER);
        headerSection.setPadding(new Insets(0, 0, 10, 0)); 

        Label titleLabel = new Label("✨ Submit Your Vote");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; " + 
                             "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 2, 0, 0, 1);");

        Label subtitleLabel = new Label("Evaluate this innovative idea and provide your professional feedback");
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + Theme.TEXT_SECONDARY + "; " + 
                               "-fx-font-style: italic;");

        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        return headerSection;
    }

    private StackPane createIdeaDetailsCard() {
        StackPane cardContainer = new StackPane();
        cardContainer.setMaxWidth(550); 
        cardContainer.setPrefWidth(550);

        Rectangle background = new Rectangle();
        background.setFill(Color.web(Theme.BACKGROUND_SECONDARY));
        background.setArcWidth(12); 
        background.setArcHeight(12);
        background.widthProperty().bind(cardContainer.widthProperty());
        background.setHeight(180); 

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.15));
        shadow.setOffsetY(3); 
        shadow.setRadius(8);
        background.setEffect(shadow);

        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(15)); 
        contentBox.setMaxWidth(520);

        HBox titleSection = new HBox(8); 
        titleSection.setAlignment(Pos.CENTER);

        Rectangle titleAccent = new Rectangle(3, 20); 
        titleAccent.setFill(Color.web(Theme.ACCENT_PRIMARY));
        titleAccent.setArcWidth(1.5);
        titleAccent.setArcHeight(1.5);

        Label ideaTitle = new Label(ideaToVoteOn.getTitle());
        ideaTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; " + 
                             "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";");
        ideaTitle.setWrapText(true);

        titleSection.getChildren().addAll(titleAccent, ideaTitle);

        Label ideaDescription = new Label(ideaToVoteOn.getDescription());
        ideaDescription.setWrapText(true);
        ideaDescription.setStyle("-fx-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                                 "-fx-font-size: 13px; -fx-line-spacing: 1px;"); 
        ideaDescription.setTextAlignment(TextAlignment.CENTER);

        HBox infoTags = new HBox(12); 
        infoTags.setAlignment(Pos.CENTER);

        Label categoryTag = createInfoTag("📂 " + ideaToVoteOn.getCategory(), Theme.ACCENT_PRIMARY);
        Label authorTag = createInfoTag("👤 " + ideaToVoteOn.getParticipant().getUsername(), Theme.TEXT_SECONDARY);

        infoTags.getChildren().addAll(categoryTag, authorTag);

        contentBox.getChildren().addAll(titleSection, ideaDescription, infoTags);
        cardContainer.getChildren().addAll(background, contentBox);

        return cardContainer;
    }

    private Label createInfoTag(String text, String color) {
        Label tag = new Label(text);
        tag.setStyle("-fx-background-color: " + adjustBrightness(color, 0.9) + "; " +
                     "-fx-text-fill: " + color + "; " +
                     "-fx-padding: 4 8; " + 
                     "-fx-background-radius: 12; " + 
                     "-fx-font-size: 10px; " + 
                     "-fx-font-weight: 500;");
        return tag;
    }

    private VBox createVotingFormSection() {
        VBox formSection = new VBox(15); 
        formSection.setAlignment(Pos.CENTER);
        formSection.setMaxWidth(500); 
        formSection.setPadding(new Insets(15, 0, 15, 0)); 

        VBox scoreSection = new VBox(10); 
        scoreSection.setAlignment(Pos.CENTER);

        Label scoreLabel = new Label("📊 Rate this Idea");
        scoreLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; " + 
                             "-fx-font-weight: bold;");

        VBox sliderContainer = createScoreSlider();

        scoreSection.getChildren().addAll(scoreLabel, sliderContainer);

        VBox commentSection = new VBox(10); 
        commentSection.setAlignment(Pos.CENTER);

        Label commentLabel = new Label("💭 Your Feedback");
        commentLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + Theme.TEXT_PRIMARY + "; " + // Reduced font size
                              "-fx-font-weight: bold;");

        commentArea = new TextArea();
        commentArea.setPromptText("Share your thoughts, suggestions, or constructive feedback...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(3); 
        commentArea.setMaxWidth(450); 
        commentArea.setPrefHeight(80); 
        applyEnhancedInputStyles(commentArea);

        commentSection.getChildren().addAll(commentLabel, commentArea);

        formSection.getChildren().addAll(scoreSection, commentSection);
        return formSection;
    }

    private VBox createScoreSlider() {
        VBox sliderContainer = new VBox(6); 
        sliderContainer.setAlignment(Pos.CENTER);
        sliderContainer.setMaxWidth(350); 

        scoreSlider = new Slider(0, 4, 2.0);
        scoreSlider.setShowTickLabels(true);
        scoreSlider.setShowTickMarks(true);
        scoreSlider.setMajorTickUnit(1.0);
        scoreSlider.setMinorTickCount(0);
        scoreSlider.setSnapToTicks(true);
        scoreSlider.setPrefWidth(300);

        scoreSlider.setStyle(
            "-fx-control-inner-background: " + Theme.BACKGROUND_SECONDARY + "; " +
            "-fx-accent: " + Theme.ACCENT_PRIMARY + "; " +
            "-fx-text-fill: " + Theme.TEXT_PRIMARY + ";"
        );

        scoreValueLabel = new Label("2.0");
        scoreValueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; " + 
                                 "-fx-text-fill: " + Theme.ACCENT_PRIMARY + "; " +
                                 "-fx-background-color: " + adjustBrightness(Theme.ACCENT_PRIMARY, 0.9) + "; " +
                                 "-fx-padding: 6 12; -fx-background-radius: 15;"); 

        scoreSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double roundedValue = Math.round(newVal.doubleValue() * 10.0) / 10.0;
            scoreValueLabel.setText(String.format("%.1f", roundedValue));
            scoreSlider.setValue(roundedValue);
        });

        HBox scoreLabels = new HBox();
        scoreLabels.setAlignment(Pos.CENTER);
        scoreLabels.setPrefWidth(300);

        Label minLabel = new Label("Poor (0)");
        minLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";"); 

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label maxLabel = new Label("Excellent (4)");
        maxLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + Theme.TEXT_SECONDARY + ";"); 

        scoreLabels.getChildren().addAll(minLabel, spacer, maxLabel);

        sliderContainer.getChildren().addAll(scoreValueLabel, scoreSlider, scoreLabels);
        return sliderContainer;
    }

    private HBox createButtonSection() {
        HBox buttonSection = new HBox(15); 
        buttonSection.setAlignment(Pos.CENTER);
        buttonSection.setPadding(new Insets(20, 0, 0, 0)); 

        Button backButton = new Button("← Back to Dashboard");
        applySecondaryButtonStyle(backButton);
        backButton.setPrefWidth(160); 
        backButton.setOnAction(e -> SceneManager.switchToScreen(new JudgeDashboard(currentJudge)));

        Button submitButton = new Button("✓ Submit Vote");
        applyPrimaryButtonStyle(submitButton);
        submitButton.setPrefWidth(160); 

        submitButton.setOnAction(e -> {
            try {
                double score = scoreSlider.getValue();
                String comment = commentArea.getText().trim();

                if (score < 0 || score > 4) {
                    AlertHelper.showAlert(AlertType.WARNING, "Validation Error", "Score must be between 0 and 4.");
                    return;
                }

                VotingService.submitVote(currentJudge, ideaToVoteOn, score, comment);
                AlertHelper.showAlert(AlertType.INFORMATION, "Success",
                    "Your vote for '" + ideaToVoteOn.getTitle() + "' has been submitted successfully!");

                SceneManager.switchToScreen(new JudgeDashboard(currentJudge));

            } catch (VotingException ex) {
                AlertHelper.showAlert(AlertType.ERROR, "Voting Error", ex.getMessage());
            }
        });

        buttonSection.getChildren().addAll(backButton, submitButton);
        return buttonSection;
    }

    private String getCommentText() {
        return commentArea.getText().trim();
    }

    private void applyPrimaryButtonStyle(Button button) {
        String baseStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " + 
            "-fx-background-radius: 6; " + 
            "-fx-padding: 10 20; " + 
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 0, 1);", 
            Theme.ACCENT_PRIMARY, adjustBrightness(Theme.ACCENT_PRIMARY, -0.1)
        );

        String hoverStyle = String.format(
            "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 10 20; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 4, 0, 0, 2); " +
            "-fx-scale-y: 1.03; -fx-scale-x: 1.03;", 
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
            "-fx-font-size: 12px; " + 
            "-fx-border-color: %s; " +
            "-fx-border-width: 1.5; " + 
            "-fx-border-radius: 6; " + 
            "-fx-background-radius: 6; " +
            "-fx-padding: 10 20; " + 
            "-fx-cursor: hand;",
            Theme.TEXT_SECONDARY, Theme.TEXT_SECONDARY
        );

        String hoverStyle = String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-padding: 10 20; " +
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
            "-fx-border-width: 1; " + 
            "-fx-background-radius: 6; " + 
            "-fx-border-radius: 6; " +
            "-fx-padding: 8; " + 
            "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
            "-fx-font-size: 12px; " + 
            "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 1, 0, 0, 1);" 
        ;

        node.setStyle(baseStyle);

        node.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                node.setStyle(
                    "-fx-background-color: " + Theme.BACKGROUND_SECONDARY + "; " +
                    "-fx-text-fill: " + Theme.TEXT_PRIMARY + "; " +
                    "-fx-border-color: " + Theme.ACCENT_PRIMARY + "; " +
                    "-fx-border-width: 1.5; " +
                    "-fx-background-radius: 6; " +
                    "-fx-border-radius: 6; " +
                    "-fx-padding: 8; " +
                    "-fx-prompt-text-fill: " + Theme.TEXT_SECONDARY + "; " +
                    "-fx-font-size: 12px; " +
                    "-fx-effect: dropshadow(gaussian, " + Theme.ACCENT_PRIMARY + ", 3, 0, 0, 0);"
                );
            } else {
                node.setStyle(baseStyle);
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

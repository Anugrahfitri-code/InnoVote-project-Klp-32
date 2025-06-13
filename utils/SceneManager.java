package utils;

import models.Judge;
import models.Participant;
import models.User;
import screens.auth.LoginScreen;
import screens.auth.RegisterScreen;
import screens.common.IdeaDetailScreen;
import screens.judge.JudgeDashboard;
import screens.judge.VotingScreen;
import screens.participant.IdeaSubmissionScreen;
import screens.participant.ParticipantDashboard;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType; 
import javafx.stage.Stage; 

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchToScreen(Parent screenNode) {
        String title = "InnoVote"; 

        if (screenNode instanceof LoginScreen) {
            title = "InnoVote - Login";
        } else if (screenNode instanceof RegisterScreen) {
            title = "InnoVote - Register";
        } else if (screenNode instanceof JudgeDashboard) {
            title = "InnoVote - Judge Dashboard";
        } else if (screenNode instanceof ParticipantDashboard) {
            title = "InnoVote - Participant Dashboard";
        } else if (screenNode instanceof IdeaSubmissionScreen) {
            title = "InnoVote - Submit Idea";
        } else if (screenNode instanceof VotingScreen) {
            title = "InnoVote - Vote for Idea";
        } else if (screenNode instanceof IdeaDetailScreen) {
            title = "InnoVote - Idea Details";
        }

        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(screenNode);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(screenNode);
        }
        primaryStage.setTitle(title);
        primaryStage.sizeToScene(); 
        primaryStage.centerOnScreen(); 
        primaryStage.show();
    }

    public static void switchToAuth() {
        switchToScreen(new LoginScreen()); 
    }

    public static void switchToRegister() { 
        switchToScreen(new RegisterScreen());
    }

    public static void switchToUserDashboard(User user) {
        if (user instanceof Judge) {
            switchToScreen(new JudgeDashboard((Judge) user));
        } else if (user instanceof Participant) {
            switchToScreen(new ParticipantDashboard((Participant) user));
        } else {
            switchToAuth();
            AlertHelper.showAlert(AlertType.ERROR, "Error", "Unknown user type. Please log in again.");
        }
    }
}

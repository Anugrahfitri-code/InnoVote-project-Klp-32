package app;

import screens.auth.LoginScreen;
import utils.SceneManager;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.setPrimaryStage(primaryStage); 
        SceneManager.switchToScreen(new LoginScreen());
    }

    public static void main(String[] args) {
        launch(args);
    }
}

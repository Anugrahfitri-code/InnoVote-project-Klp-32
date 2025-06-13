package com.innovote.app;

import com.innovote.screens.auth.LoginScreen;
import com.innovote.utils.SceneManager;

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

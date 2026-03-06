/**
 * Course: Direct Supply
 * Author: CJ Valente
 */

package com.example;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point for the quiz application
 * This class launches JavaFX and loads the initial welcome screen.
 * This screen prompts the user to enter their name before beginning.
 * This application uses JavaFX with FXML for UI.
 */

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/welcomeScreen.fxml"));
        Parent root = loader.load();
        stage.setTitle("Welcome to C.J's Quiz!");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }
}

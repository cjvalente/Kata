package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.QuizSession;

import java.io.IOException;

public class WelcomeScreenController {

    @FXML
    private Button startButton;
    private String name;

    @FXML
    private TextField nameField;

    @FXML
    private void startQuiz() {

        name = nameField.getText().trim();

        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Name");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your name to start the quiz.");
            alert.showAndWait();
            return;
        }
        Stage stage = (Stage) startButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/setUp.fxml")
        );
        try {
            QuizSession.name = name;
            Parent root = loader.load();
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(newScene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Load Exception: " + e.getMessage());
        }


    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}

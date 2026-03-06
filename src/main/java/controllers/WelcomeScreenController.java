/**
 * Course: Direct Supply
 * Author: CJ Valente
 */

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

/**
 * Controller for the Welcome Screen of the quiz application.
 * This controller manages the first screen displayed.
 * Prompts the user to enter their name before starting the quiz
 * Once a name is entered and the start button is pressed, the app transitions
 * to the setup screen.
 * The user's name is stored in the {@link QuizSession} class through a static variable
 * so that it can be accessed throughout the lifetime of the app.
 */
public class WelcomeScreenController {

    @FXML
    private Button startButton;

    @FXML
    private TextField nameField;

    /**
     * initialize so that enter key binds to the start button
     */
    public void initialize() {
        // When the user presses Enter in the text field, startQuiz() runs
        nameField.setOnAction(_ -> startQuiz());
    }

    /**
     * Handles The Start Quiz Button pressed
     * Retrieves the user's name from the text field,
     * validates that it is not empty, and then transitions to the quiz setup screen.
     * If the name field is empty, a warning alert is displayed.
     * If a valid name is entered, it is stored in {@link QuizSession}'s name variable
     * and the app loads {@code setUp.fxml}.
     */
    @FXML
    private void startQuiz() {
        //Get the user's name from the text field
        String name = nameField.getText().trim();

        //validate that the name is not empty
        if (name.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Name");
            alert.setHeaderText(null);
            alert.setContentText("Please enter your name to start the quiz.");
            alert.showAndWait();
        } else {
            //Get the current stage from the button
            Stage stage = (Stage) startButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/setUp.fxml"));
            try {
                //Set player's name for the current quiz session
                QuizSession.setName(name);

                //load the setup screen
                Parent root = loader.load();

                //Create, set, and show the new scene
                Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(newScene);
                stage.setMaximized(true);
                stage.show();
            } catch (IOException e) {
                showAlert("Load Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Displays an error alert to the user
     * @param message the message to display on the alert
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

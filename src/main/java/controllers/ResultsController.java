/**
 * Course: Direct Supply
 * Author: CJ Valente
 */
package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.LeaderboardEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Controller responsible for managing the Results screen
 * This class handles:
 *      Displaying user's final quiz score
 *      Loading and displaying dummy leaderboard data loaded from {@code leaderboard.txt}
 *      Filling a {@link TableView} with leaderboard entries
 *      Allowing a play again option, sending the user back to the setup screen
 *      Catch and show error alerts if anything occurs
 * The controller receives the score data from {@link QuizController}.
 */
public class ResultsController {
    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;
    @FXML
    private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> nameColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> scoreColumn;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;

    private final ObservableList<LeaderboardEntry> leaderboardData =
            FXCollections.observableArrayList();

    /**
     * Initializes the leaderboard table and loads the data
     * Links table columns to {@link LeaderboardEntry} properties
     * Loads leaderboard text from file
     * Populates the {@link TableView}
     */
    public void initialize() {
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());

        loadLeaderboardFromFile();
        leaderboardTable.setItems(leaderboardData);
    }

    /**
     * Displays the user's final quiz score
     * @param score the number of questions that was answered correctly
     * @param totalQuestions total number of questions in the quiz.
     */
    public void setScore(int score, int totalQuestions) {
        scoreLabel.setText(score + " / " + totalQuestions);
    }

    /**
     * Loads leaderboard data from text file and populates the leaderboard list
     * Leaderboard file is formatted as "rank name score"
     */
    private void loadLeaderboardFromFile() {
        leaderboardData.clear();
        String filepath = "src/main/resources/leaderboard.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(" ", 3);
                if (parts.length == 3) {
                    int rank = Integer.parseInt(parts[0]);
                    String playerName = parts[1];
                    String playerScore = parts[2];
                    leaderboardData.add(new LeaderboardEntry(rank, playerName, playerScore));
                }
            }
        } catch (IOException e) {
            showAlert("Error loading leaderboard: " + e.getMessage());
        }
    }

    /**
     * Handles the play again button
     * Reloads the quiz setup screen, allowing user to start a new quiz
     */
    @FXML
    private void playAgain() {
        Stage stage = (Stage) playAgainButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/setUp.fxml")
        );
        try {
            Parent root = loader.load();
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
            stage.setScene(newScene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            showAlert("Load Exception: " + e.getMessage());
        }
    }

    /**
     * Show an alert to the user
     * @param message describes the error
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

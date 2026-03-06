package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.LeaderboardEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ResultsController {
    @FXML
    private TableView<LeaderboardEntry> leaderboardTable;
    @FXML
    private TableColumn<LeaderboardEntry, Integer> rankColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> nameColumn;
    @FXML
    private TableColumn<LeaderboardEntry, String> scoreColumn;

    private int score;
    private int totalQuestions;
    private String name;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button playAgainButton;

    private final ObservableList<LeaderboardEntry> leadboardData = FXCollections.observableArrayList();

    public void initialize() {
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());

        loadLeaderboardFromFile("src/main/resources/leaderboard.txt");
        leaderboardTable.setItems(leadboardData);
    }

    private void loadLeaderboardFromFile(String filePath) {
        leadboardData.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Example line: 1 CJ 10/10
                String[] parts = line.split(" ", 3);
                if (parts.length == 3) {
                    int rank = Integer.parseInt(parts[0]);
                    String playerName = parts[1];
                    String playerScore = parts[2];
                    leadboardData.add(new LeaderboardEntry(rank, playerName, playerScore));
                }
            }
        } catch (IOException e) {
            showAlert("Error loading leaderboard: " + e.getMessage());
        }
    }

    public void setScore(int score, int totalQuestions) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        scoreLabel.setText(score + " / " + totalQuestions);
    }

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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

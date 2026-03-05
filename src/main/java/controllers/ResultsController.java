package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ResultsController {
    private int score;
    private int totalQuestions;
    @FXML
    private Label scoreLabel;
    public void setScore(int score, int totalQuestions) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        scoreLabel.setText(score + " / " + totalQuestions);
    }
}

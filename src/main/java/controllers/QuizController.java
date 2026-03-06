/**
 * Course: Kata for Direct Supply Software Engineering Intern
 * Author: CJ Valente
 * Purpose: This class controls all data flowing in and out of the quiz screen.
 */

package controllers;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class QuizController {

    @FXML
    private Label questionNumLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private VBox questionsVbox;
    @FXML
    private Button nextButton;
    @FXML
    private ProgressBar progressBar;


    private Map<String, Question> questions;
    private List<Question> questionList;
    private int currentQuestionNum = 0;
    private int score = 0;
    private boolean questionAnswered = false;
    private String name;

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
        this.questionList = new ArrayList<>(questions.values());
        loadQuestion();
    }

    private void loadQuestion() {
        questionAnswered = false;
        if (currentQuestionNum >= questionList.size()) {
            return;
        }
        Question currentQuestion = questionList.get(currentQuestionNum);
        questionLabel.setText(currentQuestion.getQuestionText());
        setQuestionNumLabelText(currentQuestionNum + 1);
        questionsVbox.getChildren().clear();

        for (String answer : currentQuestion.getAllAnswers()) {
            Button answerButton = new Button(answer);
            answerButton.setMaxWidth(Double.MAX_VALUE);
            answerButton.setPrefHeight(45);
            answerButton.setStyle(defaultButtonStyle());
            answerButton.setOnAction(_ -> handleAnswerSelection(answerButton, answer, currentQuestion));
            questionsVbox.getChildren().add(answerButton);
        }

    }

    @FXML
    private void nextButtonPressed() {
        if (!questionAnswered || currentQuestionNum > questionList.size()){
            return;
        }
        currentQuestionNum++;
        if (currentQuestionNum == questionList.size()) {
            loadResultsScreen(score);
        } else {
            updateProgressBar();
            loadQuestion();
        }

    }

    private void setQuestionNumLabelText(int currentQuestionNum) {
        if (currentQuestionNum <= questionList.size()) {
            questionNumLabel.setText("Question: " + currentQuestionNum + "/" + questionList.size());
        }
    }

    private void updateProgressBar() {
        int numTotalQuestions = questionList.size();
        double progressOfOneQuestion = (double) 1 / numTotalQuestions;
        double currentProgress = progressBar.getProgress();
        if (currentProgress < 1.0) {
            progressBar.setProgress(currentProgress + progressOfOneQuestion);
        }
    }

    private void handleAnswerSelection(Button clickedButton, String selectedAnswer, Question question) {
        if (questionAnswered) {
            return;
        }
        questionAnswered = true;
        boolean isCorrect = question.isCorrect(selectedAnswer);

        if (isCorrect) {
            score++;
        }
        for (var node : questionsVbox.getChildren()) {
            node.setDisable(true);

            if (node instanceof Button button) {

                String buttonText = button.getText();

                if (question.isCorrect(buttonText)) {
                    button.setStyle(correctStyle());
                    fadeIn(button);
                } else if (button == clickedButton) {
                    button.setStyle(incorrectStyle());
                    fadeIn(button);
                }
            }
        }


    }


    private void loadResultsScreen(int score) {
        Stage stage = (Stage) nextButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/results.fxml")
        );
        try {
            Parent root = loader.load();
            ResultsController resultsController = loader.getController();
            resultsController.setScore(score, questionList.size());
            Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

            stage.setScene(newScene);


            stage.setMaximized(true);

            stage.show();
        } catch (IOException e) {
            showAlert("Load Exception: " + e.getMessage());
        }
    }

    private String defaultButtonStyle() {
        return """
                -fx-background-color: #2366C0;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """;
    }

    private String correctStyle() {
        return """
                -fx-background-color: #2e7d32;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                """;
    }

    private String incorrectStyle() {
        return """
                -fx-background-color: #c62828;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                """;
    }

    private void fadeIn(Button button) {
        FadeTransition ft = new FadeTransition(Duration.millis(400), button);
        ft.setFromValue(0.4);
        ft.setToValue(1.0);
        ft.play();
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}

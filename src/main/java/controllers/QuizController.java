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

/**
 * Controller responsible for managing the quiz gameplay screen
 *This class handles:
 *      Displaying questions and answer choices
 *      Handling user answer selection
 *      Tracking quiz progress and score
 *      Updating progress bar
 *      Applying visual feedback for correct/incorrect answers
 *      Transitioning to the results screen when quiz is done
 * The controller receives quiz data and dynamically generates
 * answer buttons for each question.
 */
public class QuizController {
    private static final int ANSWER_BUTTON_HEIGHT = 45;
    private static final int FADE_TRANSITION_MILLIS = 400;
    private static final double FADE_FROM_VALUE = 0.4;

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


    private List<Question> questionList;
    private int currentQuestionNum = 0;
    private int score = 0;
    private boolean questionAnswered = false;

    /**
     * Receives the quiz questions and initializes the quiz
     * @param questions map containing question text mapped to Question object
     */
    public void setQuestions(Map<String, Question> questions) {
        this.questionList = new ArrayList<>(questions.values());
        loadQuestion();
    }

    /**
     * Loads the current question and dynamically generates answer buttons.
     */
    private void loadQuestion() {
        questionAnswered = false;
        if (currentQuestionNum < questionList.size()) {

            //grab question we are on and set text
            Question currentQuestion = questionList.get(currentQuestionNum);
            questionLabel.setText(currentQuestion.getQuestionText());

            //update current question label
            setQuestionNumLabelText(currentQuestionNum + 1);

            //clear main vbox on screen that holds answer buttons.
            questionsVbox.getChildren().clear();

            //Fill the vbox with answer buttons based on how many possible answers there are.
            for (String answer : currentQuestion.getAllAnswers()) {
                Button answerButton = new Button(answer);
                answerButton.setMaxWidth(Double.MAX_VALUE);
                answerButton.setPrefHeight(ANSWER_BUTTON_HEIGHT);
                answerButton.setStyle(defaultButtonStyle());
                answerButton.setOnAction(
                        _ -> handleAnswerSelection(answerButton, answer, currentQuestion)
                );
                questionsVbox.getChildren().add(answerButton);
            }
        }

    }

    /**
     * Handles what happens when next button is pressed
     * If the current question has been answered, the quiz advances to the
     * next question or loads final result screen.
     */
    @FXML
    private void nextButtonPressed() {
        if (questionAnswered && currentQuestionNum < questionList.size()) {
            currentQuestionNum++;
            if (currentQuestionNum == questionList.size()) {
                loadResultsScreen(score);
            } else {
                updateProgressBar();
                loadQuestion();
            }
            questionAnswered = false;
        }
    }

    /**
     * Updates the question number label
     * @param currentQuestionNum the question number currently being displayed
     */
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

    /**
     * Handles logic when a user selects an answer
     * This method:
     *      Determines whether the selected answer is correct
     *      Updates the score if it is.
     *      Disables all answer buttons
     *      Applies immediate visual feedback.
     * @param clickedButton the button the user clicked
     * @param selectedAnswer the selected answer text
     * @param question the current quiz question
     */
    private void handleAnswerSelection(
              Button clickedButton, String selectedAnswer, Question question) {


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

    /**
     * Loads the results screen once the quiz is finished
     * @param score the user's final quiz score.
     */
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

    /**
     * White text, blue background
     * @return CSS string
     */
    private String defaultButtonStyle() {
        return """
                -fx-background-color: #2366C0;
                -fx-text-fill: white;
                -fx-font-size: 15px;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """;
    }

    /**
     * White text, green background
     * @return CSS String
     */
    private String correctStyle() {
        return """
                -fx-background-color: #2e7d32;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                """;
    }

    /**
     * White text, red background
     * @return CSS String
     */
    private String incorrectStyle() {
        return """
                -fx-background-color: #c62828;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 8;
                """;
    }

    /**
     * Method that handles the fade in of the correct answer button and if they are different,
     * the chosen answer button as well.
     * @param button the button we are fading in. Either just the
     * correct button or the correct+incorrect chosen button.
     */
    private void fadeIn(Button button) {
        FadeTransition ft = new FadeTransition(Duration.millis(FADE_TRANSITION_MILLIS), button);
        ft.setFromValue(FADE_FROM_VALUE);
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

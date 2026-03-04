package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class QuizController {

    @FXML
    public Label questionNumLabel;
    @FXML
    public Label questionLabel;
    @FXML
    public VBox questionsVbox;
    @FXML
    public Button nextButton;
    @FXML
    public ProgressBar progressBar;

    private Map<String, Question> questions;
    private List<Question> questionList;
    private int currentQuestion = 1;
    private int score = 0;

    public void setQuestions(Map<String, Question> questions) {
        this.questions = questions;
        this.questionList = new ArrayList<>(questions.values());
        //loadQuestion();
    }

    @FXML
    private void nextButtonPressed() {
        setQuestionNumLabelText(++currentQuestion);
        updateProgressBar();

    }

    private void setQuestionNumLabelText(int currentQuestion) {
        if (currentQuestion <= 10 ) { //switch 10 with questionList.size()
            StringBuilder sb = new StringBuilder();
            sb.append("Question: ").append(currentQuestion);
            questionNumLabel.setText(sb.toString());
        }
    }

    private void updateProgressBar() {
        int numTotalQuestions = 10; //questionList.size();
        double progressOfOneQuestion = (double) 1 / numTotalQuestions;
        double currentProgress = progressBar.getProgress();
        if (currentProgress < 1.0) {
            progressBar.setProgress(currentProgress + progressOfOneQuestion);  //hello
        }

    }



}

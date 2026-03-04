package model;

import java.util.Collections;
import java.util.List;

public class Question {

    private final String questionText;
    private final String correctAnswer;
    private final List<String> allAnswers;

    public Question(String questionText, String correctAnswer, List<String> allAnswers) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.allAnswers = allAnswers;
        Collections.shuffle(this.allAnswers);
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAllAnswers() {
        return allAnswers;
    }

    public boolean isCorrect(String selectedAnswer) {
        return correctAnswer.equals(selectedAnswer);
    }


}

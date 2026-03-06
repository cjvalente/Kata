/**
 * Course: Direct Supply
 * Author: CJ Valente
 */
package model;

import java.util.List;

/**
 * This class holds all data for a single question
 * Filled with data from the API
 */
public class Question {

    private final String questionText;
    private final String correctAnswer;
    private final List<String> allAnswers;

    /**
     * All data within a single question from the API to be displayed on the quiz screen
     * @param questionText the question
     * @param correctAnswer the correct answer text
     * @param allAnswers a list of all the answers
     */
    public Question(String questionText, String correctAnswer, List<String> allAnswers) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.allAnswers = allAnswers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAllAnswers() {
        return allAnswers;
    }

    /**
     * tells us if the answer is correct in the list
     * @param selectedAnswer the answer the user pressed
     * @return boolean of correct/incorrect.
     */
    public boolean isCorrect(String selectedAnswer) {
        return correctAnswer.equals(selectedAnswer);
    }


}

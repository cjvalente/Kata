package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.TriviaApiClient;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SetUpController {
    private static final int MIN_QUESTIONS = 1;
    private static final int MAX_QUESTIONS = 50;
    private static final int DEFAULT_NUM_QUESTIONS = 10;
    private static final int CATEGORY_ID_MIN = 9;
    private static final int CATEGORY_ID_MAX = 32;

    @FXML
    private Spinner<Integer> numQuestionsSpinner;

    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<String> difficultyComboBox;
    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private Button startButton;

    private Map<Integer, String> categoryMap;
    TriviaApiClient api = new TriviaApiClient();

    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        MIN_QUESTIONS, MAX_QUESTIONS, DEFAULT_NUM_QUESTIONS);
        numQuestionsSpinner.setValueFactory(valueFactory);
        //initialize spinner with values 1-50, default 10

        difficultyComboBox.setItems(FXCollections.observableArrayList(
                "Any", "easy", "medium", "hard"));
        difficultyComboBox.setPromptText("Difficulty"); //initialize difficulties, default is any

        typeComboBox.setItems(FXCollections.observableArrayList(
                "Any", "multiple choice", "true/false"));
        typeComboBox.setPromptText("Type");  //initialize types, default is any

        try {
            api.loadCategories(); //load categories from api
            Map<Integer, String> categories = api.getCategoryMap();   //create map
            setCategoryMap(categories); //set map, setting categories equal to the values.
        } catch (Exception e) {
            showAlert("Error with API call");
        }
    }

    public void setCategoryMap(Map<Integer, String> categoryMap) {
        this.categoryMap = categoryMap;
        categoryComboBox.setItems(
            FXCollections.observableArrayList(categoryMap.values()) //set categories equal to values
        );
        categoryComboBox.setPromptText("Category");
    }

    @FXML
    private void startButtonPressed(ActionEvent event) {
        int numQuestions = numQuestionsSpinner.getValue();
        String selectedCategory = categoryComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();
        String selectedType = typeComboBox.getValue();
        Integer selectedCategoryId = null;
        if (selectedCategory != null && !selectedCategory.equals("Any")) {  //edge case
            for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
                if (entry.getValue().equals(selectedCategory)) {
                    selectedCategoryId = entry.getKey();
                }
            }
        }
        if (selectedCategory == null) {
            selectedCategoryId = ThreadLocalRandom.current().nextInt(CATEGORY_ID_MIN, CATEGORY_ID_MAX);
        }
        if (selectedType == null) {
            selectedType = "multiple choice";
        }
        if (selectedDifficulty == null) {
            selectedDifficulty = "medium";
        }

        System.out.println("Questions: " + numQuestions);
        System.out.println("Category ID: " + selectedCategoryId);
        System.out.println("Difficulty: " + selectedDifficulty);
        System.out.println("Type: " + selectedType);  //WORKS!!!!!!!!!
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

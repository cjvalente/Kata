package controllers;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import service.TriviaApiClient;
import java.util.Map;

public class SetUpController {
    private static final int MIN_QUESTIONS = 1;
    private static final int MAX_QUESTIONS = 50;
    private static final int DEFAULT_NUM_QUESTIONS = 10;

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
    private final TriviaApiClient api = new TriviaApiClient();

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
                FXCollections.observableArrayList(categoryMap.values())
        );
        categoryComboBox.setPromptText("Category");
    }

    @FXML
    private void startButtonPressed() {
        int numQuestions = numQuestionsSpinner.getValue();
        String selectedCategory = categoryComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();
        String selectedType = typeComboBox.getValue();
        Integer selectedCategoryId = 0;
        if (selectedCategory != null && !selectedCategory.equals("Any")) {  //edge case
            for (Map.Entry<Integer, String> entry : categoryMap.entrySet()) {
                if (entry.getValue().equals(selectedCategory)) {
                    selectedCategoryId = entry.getKey();
                }
            }
        }
        if (selectedType == null || selectedType.equals("Any")) {
            selectedType = null;
        } else if (selectedType.equals("multiple choice")) {
            selectedType = "multiple";
        } else if (selectedType.equals("true/false")) {
            selectedType = "boolean";
        }
        if (selectedDifficulty == null || selectedDifficulty.equals("Any")) {
            selectedDifficulty = null;
        }

        try {
            String link = api.createLink(numQuestions, selectedCategoryId, selectedDifficulty, selectedType);
            System.out.println("API LINK: " + link);
            Map<String, model.Question> questions = api.loadQuestions(link);
            loadQuizScreen(questions);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading questions.");
        }

    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadQuizScreen(Map<String, model.Question> questions) throws Exception {

        Stage stage = (Stage) startButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/quiz.fxml")
        );


        Parent root = loader.load();
        QuizController quizController = loader.getController();
        quizController.setQuestions(questions);

        Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());

        //newScene.getStylesheets().addAll(stage.getScene().getStylesheets());

        stage.setScene(newScene);


        stage.setMaximized(true);

        stage.show();
    }
}

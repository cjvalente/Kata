/**
 * Course: Direct Supply
 * Author: CJ Valente
 */
package controllers;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import model.QuizSession;
import service.TriviaApiClient;
import java.util.Map;

/**
 * Controller responsible for managing the quiz set up screen
 * Handles all set up options before a quiz begins.
 * users can choose number of questions, category, difficulty, and question type.
 * After set up, the controller builds an API request through {@link TriviaApiClient}
 * and loads quiz questions from the Open Trivia Database API.
 * This class handles:
 *      Initializing UI
 *      Loading quiz categories from the Trivia API
 *      Mapping category ID's to names
 *      Building the API request URL based on user selections
 *      Loading quiz questions
 *      Creating and moving into quiz screen
 *      Handling and displaying errors without crashing
 * The user's name is retrieve from {@link model.QuizSession}
 * and displayed on this screen
 */
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
    private Label welcomeLabel;

    @FXML
    private Button startButton;

    private Map<Integer, String> categoryMap;
    private final TriviaApiClient api = new TriviaApiClient();

    /**
     * Initializes the setup screen
     * Setting the welcome message with the player's name,
     * Initializes the question count spinner,
     * initializes difficulty and question type,
     * loads categories from the Trivia API.
     */
    public void initialize() {
        welcomeLabel.setText("Welcome " + QuizSession.getName() + "!");
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
        } catch (RuntimeException e) {
            showAlert("Run Time Error in category list");
        } catch (Exception e) {
            showAlert("Unknown Error when loading category list");
        }
    }

    /**
     * Stores the category map retrieved from the API and fills the category
     * {@link ComboBox} with the available category names
     * @param categoryMap mapping of category ID's to category names.
     *                    Ex. 1 -> "Sports"
     */
    public void setCategoryMap(Map<Integer, String> categoryMap) {
        this.categoryMap = categoryMap;
        categoryComboBox.setItems(
                FXCollections.observableArrayList(categoryMap.values())
        );
        categoryComboBox.setPromptText("Category");
    }

    /**
     * Handles the Start Quiz Button pressed
     * Method performs:
     *      Retrieves user selections from the UI elements
     *      Converts these selections into words compatible with the API documentation
     *      Builds the API request URL via {@code TriviaApiClient.createLink()}
     *      Transitions screen to the quiz screen
     */
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
        //Ensure format matches API documentation
        selectedType = switch (selectedType) {
            case null -> null;
            case "Any" -> null;
            case "multiple choice" -> "multiple";
            case "true/false" -> "boolean";
            default -> selectedType;
        };
        if (selectedDifficulty == null || selectedDifficulty.equals("Any")) {
            selectedDifficulty = null;
        }

        String link = api.createLink(
                numQuestions, selectedCategoryId, selectedDifficulty, selectedType);
        System.out.println("API LINK: " + link);
        try {
            Map<String, model.Question> questions = api.loadQuestions(link);
            loadQuizScreen(questions);
        } catch (Exception e) {
            showAlert("Error loading questions");
        }

    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadQuizScreen(Map<String, model.Question> questions) {
        if (api.getResponseCode() == 0) {
            Stage stage = (Stage) startButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/quiz.fxml")
            );
            try {
                Parent root = loader.load();
                QuizController quizController = loader.getController();
                quizController.setQuestions(questions);
                Scene newScene = new Scene(root, stage.getWidth(), stage.getHeight());
                stage.setScene(newScene);
                stage.setMaximized(true);
                stage.show();
            } catch (RuntimeException e) {
                showAlert("Run Time error when building quiz screen: " + e.getMessage());
            } catch (Exception t) {
                showAlert("Unknown error when building quiz screen: " + t.getMessage());
            }
        }
    }
}

package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import service.TriviaApiClient;

import java.util.Map;

public class SetUpController {

    @FXML
    private Spinner<Integer> numQuestionsSpinner;

    @FXML
    private ComboBox<String> categoryComboBox, difficultyComboBox, typeComboBox;

    @FXML
    private Button startButton;

    private Map<Integer, String> categoryMap;
    TriviaApiClient api = new TriviaApiClient();

    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10);
        numQuestionsSpinner.setValueFactory(valueFactory);

        difficultyComboBox.setItems(FXCollections.observableArrayList(
            "Any", "easy", "medium", "hard"));
        difficultyComboBox.setPromptText("Difficulty");

        typeComboBox.setItems(FXCollections.observableArrayList("Any", "multiple", "true/false"));
        typeComboBox.setPromptText("Type");
        try {
            api.loadCategories();
            Map<Integer, String> categories = api.getCategoryMap();
            setCategoryMap(categories);
        } catch (Exception e) {
            System.out.println("Error with API call");
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
    private void startButtonPressed(ActionEvent event) {

    }
}

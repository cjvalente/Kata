/**
 * Course: Direct Supply
 * Author: CJ Valente
 */
package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import model.Question;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.text.StringEscapeUtils;

/**
 * Service class responsible for communicating the Open Trivia Database
 * This class handles all network requests related to retrieving quiz data.
 * Loads available quiz categories and questions based on user input.
 * This class does these things:
 *      Sends HTTP requests to API
 *      Load and store trivia categories
 *      Build API request URL based on quiz setup
 *      Parse JSON responses into {@link model.Question} objects
 *      Handles API response code to catch errors
 *      Decode HTML into english
 * Questions are returned in a {@link java.util.LinkedHashMap} to preserve order
 * to make sure the quiz displays questions sequentially.
 *
 */
public class TriviaApiClient {
    private static final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private static final String BASE_URL = "https://opentdb.com/api.php?";
    private static final int FIVE_SECONDS = 5000;
    private static final int SUCCESS = 0;
    private static final int NO_RESULTS = 1;
    private static final int INVALID_PARAMS = 2;
    private static final int TOKEN_NOT_FOUND = 3;
    private static final int TOKEN_EMPTY = 4;
    private static final int RATE_LIMIT = 5;
    private final HttpClient client;
    private Map<Integer, String> categoryMap;
    private int responseCode = 0;


    /**
     * Constructs a new {@code TriviaApiClient}
     * Initializes the HTTP client and creates an empty category map
     */
    public TriviaApiClient() {
        this.client = HttpClient.newHttpClient();
        this.categoryMap = new HashMap<>();
    }

    /**
     * Retrieves the list of trivia categories from the API and stores them in the map
     * Categories are mapped with their IDs
     * @throws Exception if the HTTP request fails or JSON response can't be grabbed
     */
    public void loadCategories() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CATEGORY_URL)).GET()
                .build();

        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray arr = root.getAsJsonArray("trivia_categories");
        Map<Integer, String> map = new HashMap<>();

        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            int id = obj.get("id").getAsInt();
            String name = obj.get("name").getAsString();
            map.put(id, name);
        }

        this.categoryMap = map;
    }

    public Map<Integer, String> getCategoryMap() {
        return this.categoryMap;
    }

    /**
     * Creates the link for API call based on user input from the
     * setup screen.
     * @param amount of questions the user wants. 1-50
     * @param categoryId category user wants.
     * @param difficulty of questions. easy, medium, hard, any
     * @param type of questions. true/false, multiple choice, any
     * @return a link as a string
     */
    public String createLink(int amount, int categoryId, String difficulty, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL).append("amount=").append(amount);
        if (categoryId != 0) {
            sb.append("&category=").append(categoryId);
        }
        if (difficulty != null) {
            sb.append("&difficulty=").append(difficulty);
        }
        if (type != null) {
            sb.append("&type=").append(type);
        }
        return sb.toString();
    }

    /**
     * Sends a request to the trivia API and loads quiz questions
     * Response is validated using the API response code
     * and then fixed into {@link model.Question} objects.
     *
     * @param link API request URL
     * @return map containing question text mapped to {@link Question} object
     * @throws Exception if the request fails or the response cannot be processed
     */
    public Map<String, Question> loadQuestions(String link) throws Exception {
        Map<String, Question> questionMap = new LinkedHashMap<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(link)).GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request, HttpResponse.BodyHandlers.ofString());

            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            responseCode = Integer.parseInt(root.get("response_code").getAsString());
            switch (responseCode) {
                case SUCCESS -> {
                    return parseQuestions(root);
                }
                case NO_RESULTS -> showAlert("API Error: No results for this query.");
                case INVALID_PARAMS -> showAlert("API Error: Invalid parameters used.");
                case TOKEN_NOT_FOUND -> showAlert("API Error: Session token not found.");
                case TOKEN_EMPTY -> showAlert("API Error: Token empty (all questions used).");
                case RATE_LIMIT -> {
                    showAlert("API Error: Rate limit exceeded. Waiting 5 seconds...");

                    Thread.sleep(FIVE_SECONDS);
                    return loadQuestions(link);
                }
                default -> showAlert("API Error: Unknown response code: " + responseCode);
            }
        } catch (java.net.http.HttpTimeoutException e) {
            showAlert("Network timeout while contacting trivia API");
        } catch (java.net.ConnectException e) {
            showAlert("Unable to connect to trivia API");
        } catch (InterruptedException e) {
            showAlert("Thread interrupted while waiting for API");
            Thread.currentThread().interrupt();
        }
        return questionMap;
    }

    /**
     * Parses JSON response returned from API into {@link Question} objects
     * Each question has question text, correct answer, list of possible answers
     * @param root JSON object representing API response
     * @return map containing questions
     */
    private Map<String, Question> parseQuestions(JsonObject root) {
        Map<String, Question> questionMap = new LinkedHashMap<>();
        JsonArray arr = root.getAsJsonArray("results");


        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            String questionText = decodeHtml(obj.get("question").getAsString());
            String correctAnswer = decodeHtml(obj.get("correct_answer").getAsString());
            JsonArray incorrectArray = obj.getAsJsonArray("incorrect_answers");
            List<String> answers = new ArrayList<>();
            answers.add(correctAnswer);
            for (JsonElement wrong : incorrectArray) {
                answers.add(decodeHtml(wrong.getAsString()));
            }
            Collections.shuffle(answers);
            Question question = new Question(questionText, correctAnswer, answers);
            questionMap.put(questionText, question);
        }
        return questionMap;
    }

    /**
     * Gets response code and indicates whether API call failed or worked
     * @return API response code
     */
    public int getResponseCode() {
        return responseCode;
    }

    private String decodeHtml(String text) {
        return StringEscapeUtils.unescapeHtml4(text);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}

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

public class TriviaApiClient {
    private static final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private static final String BASE_URL = "https://opentdb.com/api.php?";

    private final HttpClient client;
    private Map<Integer, String> categoryMap;
    private int responseCode = 0;

    public TriviaApiClient() {
        this.client = HttpClient.newHttpClient();
        this.categoryMap = new HashMap<>();
    }

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

    public String getCategoryName(int id) {
        return this.categoryMap.get(id);
    }

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
                case 0:
                    return parseQuestions(root);
                case 1:
                    showAlert("API Error: No results for this query.");
                    return questionMap;

                case 2:
                    showAlert("API Error: Invalid parameters used.");
                    return questionMap;

                case 3:
                    showAlert("API Error: Session token not found.");
                    return questionMap;

                case 4:
                    showAlert("API Error: Token empty (all questions used).");
                    return questionMap;

                case 5:
                    showAlert("API Error: Rate limit exceeded. Waiting 5 seconds...");
                    Thread.sleep(5000);
                    return loadQuestions(link);

                default:
                    showAlert("API Error: Unknown response code: " + responseCode);
            }
        } catch (java.net.http.HttpTimeoutException e) {
            showAlert("Network timeout while contacting trivia API");
        } catch (java.net.ConnectException e) {
            showAlert("Unable to connect to trivia API");
        } catch (InterruptedException e) {
            showAlert("Thread interrupted while waiting for API");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Unexpected error while loading questions.");
        }
        return questionMap;



    }

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

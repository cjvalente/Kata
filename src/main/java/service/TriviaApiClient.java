package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class TriviaApiClient {
    private static final String CATEGORY_URL = "https://opentdb.com/api_category.php";
    private static final String BASE_URL = "https://opentdb.com/api.php?";

    private final HttpClient client;
    private Map<Integer, String> categoryMap;

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
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(link)).GET()
            .build();

        HttpResponse<String> response = client.send(
            request, HttpResponse.BodyHandlers.ofString());

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray arr = root.getAsJsonArray("results");
        Map<String, Question> questionMap = new LinkedHashMap<>();

        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            String questionText = decodeHtml(obj.get("question").getAsString());
            String correctAnswer = decodeHtml(obj.get("correct_answer").getAsString());

            JsonArray incorrectArray = obj.getAsJsonArray("incorrect_answers");

            List<String> answers = new ArrayList<>();

            answers.add(correctAnswer);

            for (JsonElement wrong : incorrectArray){
                answers.add(decodeHtml(wrong.getAsString()));
            }

            Collections.shuffle(answers);

            Question question = new Question(questionText, correctAnswer, answers);

            questionMap.put(questionText, question);

        }
        return questionMap;



    }

    private String decodeHtml(String text) {
        return text.replace("&quot;", "\"")
            .replace("&#039;", "'")
            .replace("&amp;", "&")
            .replace("&eacute;", "é")
            .replace("&uuml;", "ü")
            .replace("&rsquo;", "'")
            .replace("&ldquo;", "\"")
            .replace("&rdquo;", "\"");
    }



}

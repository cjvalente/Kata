package service;

import com.google.gson.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class TriviaApiClient {
    private static final String CATEGORY_URL = "https://opentdb.com/api_category.php";

    private final HttpClient client;
    private final Gson gson;
    private Map<Integer, String> categoryMap;

    public TriviaApiClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.categoryMap = new HashMap<>();
    }

    public void loadCategories() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CATEGORY_URL)) .GET()
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



}

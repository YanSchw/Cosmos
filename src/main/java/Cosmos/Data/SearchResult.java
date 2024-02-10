package Cosmos.Data;

import java.util.HashMap;

public class SearchResult {

    public HashMap<String, Integer> matches = new HashMap<>();

    public void insertURL(String url) {
        matches.put(url, matches.getOrDefault(url, 0) + 1);
    }

}

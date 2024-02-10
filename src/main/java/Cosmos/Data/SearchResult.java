package Cosmos.Data;

import java.util.HashMap;

public class SearchResult {

    public HashMap<String, WebPage> matches = new HashMap<>();

    public void insert(String url, String title) {
        if (!matches.containsKey(url)) {
            WebPage page = new WebPage();
            page.url = url;
            page.title = title;
            matches.put(url, page);
        }

        WebPage page = matches.get(url);
        page.score += 100;
    }

}

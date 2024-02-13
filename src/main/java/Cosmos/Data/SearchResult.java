package Cosmos.Data;

import java.util.HashMap;

public class SearchResult {

    public String elapsedTime;

    public HashMap<String, WebPage> matches = new HashMap<>();

    public void insert(String url, String title) {
        if (!matches.containsKey(url)) {
            WebPage page = new WebPage();
            page.setURL(url);
            page.setTitle(title);
            matches.put(url, page);
        }

        WebPage page = matches.get(url);
        page.addScore(100);
    }

}

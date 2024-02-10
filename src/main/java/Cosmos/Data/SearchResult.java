package Cosmos.Data;

import java.util.HashMap;

public class SearchResult {

    public HashMap<String, WebPage> matches = new HashMap<>();

    public void insertURL(String url) {
        if (!matches.containsKey(url)) {
            WebPage page = new WebPage();
            page.url = url;
            matches.put(url, page);
        }

        WebPage page = matches.get(url);
        page.score += 100;
    }

}

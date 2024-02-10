package Cosmos.Data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class WebCrawler extends Thread {

    @Override
    public void run() {
        super.run();

        System.out.println("Starting WebCrawler...");

        while (true) {
            indexWebPage(Database.getNextReadyURL());
        }
    }

    private void indexWebPage(String url) {
        System.out.println("Indexing " + url);
        Database.updateURLAlreadyIndexed(url, true);
        String html = null;
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            html = scanner.next();
            scanner.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (html == null) {
            return;
        }

        Document doc = Jsoup.parse(html);

        ArrayList<String> hrefs = extractHRefFromHTML(html);
        for (String href : hrefs) {
            Database.insertNewURL(href, doc.title());
        }
        ArrayList<String> tokens = extractTokensFromHTML(html);
        Database.deleteIndiciesForURL(url);
        Database.insertIndicies(url, tokens);
    }
    private ArrayList<String> extractHRefFromHTML(String html) {
        Document doc = Jsoup.parse(html);

        ArrayList<String> out = new ArrayList<>();
        Elements elements = doc.getAllElements();
        for (int i = 0; i < elements.size(); i++) {
            String href = elements.get(i).attr("href");
            if (!href.isEmpty() && href.startsWith("http")) {
                out.add(href);
            }
        }

        return out;
    }
    private ArrayList<String> extractTokensFromHTML(String html) {
        Document doc = Jsoup.parse(html);
        String text = doc.text();

        ArrayList<String> out = new ArrayList<>();
        String[] tokens = text.split(" ");
        for (String token : tokens) {

            // filter tokens
            if (token.contains("'")) continue;
            if (token.contains("\"")) continue;
            if (token.contains("\\")) continue;
            if (token.contains("/")) continue;
            if (token.contains("<")) continue;
            if (token.contains(">")) continue;
            if (token.contains("=")) continue;
            if (token.contains("[")) continue;
            if (token.contains("]")) continue;

            out.add(token);
        }

        return out;
    }

    private static WebCrawler instance;
    public static void init() {
        instance = new WebCrawler();
        instance.start();
    }

}

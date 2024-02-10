package Cosmos.Data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
        //Database.updateURLAlreadyIndexed(url, true);
        int depth = Database.getDepthFromURL(url);
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
        Database.updateTitle(url, doc.title());

        ArrayList<String> hrefs = extractHRefFromDoc(doc);
        /*for (String href : hrefs) {
            Database.insertNewURL(href, depth + 1);
        }*/
        Database.insertBulkURLs(hrefs, depth + 1);
        ArrayList<String> tokens = extractTokensFromDoc(doc);
        Database.deleteIndiciesForURL(url);
        Database.insertIndicies(url, tokens);
    }
    private ArrayList<String> extractHRefFromDoc(Document doc) {
        ArrayList<String> out = new ArrayList<>();
        Elements elements = doc.getAllElements();
        for (Element element : elements) {
            String href = element.attr("href");
            if (href.startsWith("http") && !href.contains("'") && !href.contains("\"") && href.length() < 512) {
                out.add(href);
            }
        }

        return out;
    }
    private ArrayList<String> extractTokensFromDoc(Document doc) {
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

            out.add(token.toLowerCase());
        }

        return out;
    }

    public static void init(int count) {
        for (int i = 0; i < count; i++) {
            WebCrawler instance = new WebCrawler();
            instance.start();
        }
    }

}

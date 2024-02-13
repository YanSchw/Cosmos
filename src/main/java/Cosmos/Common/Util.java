package Cosmos.Common;

import Cosmos.Data.WebPage;
import java.util.ArrayList;

// This class offers common utilities needed for Cosmos
public class Util {

    public static void weightPages(ArrayList<WebPage> pages, String query) {
        String[] tokens = query.split(" ");
        for (String token : tokens) {
            for (WebPage page : pages) {
                if (page.getURL().toLowerCase().contains(token.toLowerCase())) {
                    page.addScore(7_500);
                }
                if (page.getTitle().toLowerCase().contains(token.toLowerCase())) {
                    page.addScore(7_500);
                }

                String[] path = page.getURL().split("/");
                String lastElement = path[path.length - 1].isEmpty() ? path[path.length - 2] : path[path.length - 1];
                if (lastElement.toLowerCase().contains(token.toLowerCase())) {
                    page.addScore(10_000);
                }
            }
        }
    }

}

package Cosmos.Common;

import Cosmos.Data.WebPage;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilTests {

    @Test
    void weightPagesTest() {

        final String query = "google";
        final ArrayList<WebPage> pages = new ArrayList<>();

        pages.add(new WebPage("https://www.google.com/", "Google.com", 0));
        pages.add(new WebPage("https://github.com/", "Github.com", 0));

        Util.weightPages(pages, query);
        pages.sort((a, b) -> b.getScore() - a.getScore());

        assertEquals("Google.com", pages.get(0).getTitle());
        assertTrue(pages.get(0).getScore() != pages.get(1).getScore());
    }

}

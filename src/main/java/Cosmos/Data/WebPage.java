package Cosmos.Data;

public class WebPage {

    private String url;
    public String title;
    public int score;

    public void setURL(String url) {
        this.url = url;
        score += URLtoScore(this.url);
    }
    public String getURL() {
        return this.url;
    }
    private static int URLtoScore(String url) {
        if (url.length() >= 500) {
            return 0;
        }
        return 20_000 - url.length() * 40;
    }

    public int getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }
}

package Cosmos.Data;

public class WebPage {

    private String url;
    private String title;
    private int score;

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

    public void setScore(int scr) {
        score = scr;
    }
    public void addScore(int scr) {
        score += scr;
    }
    public int getScore() {
        return score;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}

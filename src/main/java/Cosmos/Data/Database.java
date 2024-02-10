package Cosmos.Data;

import Cosmos.Common.Seeds;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class Database {

    private static Connection connectionMain;
    private Connection connectionDatabase;

    public Database() {
        try {
            connectionDatabase = connectToDatabase();
            Statement stmt = connectionDatabase.createStatement();
            stmt.execute("USE cosmos;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection connectToDatabase() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "1234");
    }

    public static void setup() {
        try {
            connectionMain = connectToDatabase();
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

        try {
            Statement stmt = connectionMain.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS cosmos;");
            stmt.execute("USE cosmos;");
            stmt.execute("CREATE TABLE IF NOT EXISTS webcontent(id INT PRIMARY KEY AUTO_INCREMENT, url VARCHAR(512) UNIQUE, title VARCHAR(512), depth INT, already_indexed BOOL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS webindex(id INT PRIMARY KEY AUTO_INCREMENT, contentID INT, idx VARCHAR(512), FOREIGN KEY (contentID) REFERENCES webcontent(id));");

            // Insert default data into Database if empty
            // Including Seed URLs
            ResultSet result = stmt.executeQuery("SELECT * FROM webcontent LIMIT 1;");
            if (!result.next()) {
                ArrayList<String> seeds = Seeds.getSeeds();
                for (String seed : seeds) {
                    System.out.println("Inserting default seed URL: " + seed);
                    stmt.execute("INSERT INTO webcontent VALUES (0, '"+seed+"', '', 0, false);");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Object mutex = new Object();
    public String getNextReadyURL() {
        synchronized (mutex) {
            try {
                Statement stmt = connectionDatabase.createStatement();
                ResultSet result = stmt.executeQuery("SELECT url FROM webcontent WHERE already_indexed = false ORDER BY depth ASC LIMIT 1;");

                result.next();
                String url = result.getString(1);
                updateURLAlreadyIndexed(url, true);
                return url;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /*
    public static void insertNewURL(String url, int depth) {
        if (url.contains("'") || url.contains("\"")) {
            return;
        }
        try {
            Statement stmt = connectionDatabase.createStatement();
            ResultSet result = stmt.executeQuery("SELECT url FROM webcontent WHERE url = '" + url + "' LIMIT 1;");

            if (!result.next()) {
                stmt.execute("INSERT INTO webcontent VALUES (0, '" + url + "', 'No Title', " + depth + ", false);");
            } else {
                // Maybe update depth
                ResultSet resultDepth = stmt.executeQuery("SELECT depth FROM webcontent WHERE url = '" + url + "' LIMIT 1;");
                resultDepth.next();
                if (resultDepth.getInt(1) > depth) {
                    stmt.execute("UPDATE webcontent SET depth = " + depth + " WHERE url = '" + url + "';");
                    // TODO: reset already_indexed
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public void insertBulkURLs(ArrayList<String> urls, int depth) {
        if (urls.isEmpty()) {
            return;
        }
        try {
            Statement stmt = connectionDatabase.createStatement();
            StringBuilder sql = new StringBuilder("INSERT IGNORE INTO webcontent VALUES ");
            for (int i = 0; i < urls.size(); i++) {
                sql.append("(0, '").append(urls.get(i)).append("', 'No Title', ").append(depth).append(", false)");
                if (i < urls.size() - 1) {
                    sql.append(", ");
                }
            }
            stmt.execute(sql + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateTitle(String url, String title) {
        title = title.replaceAll("'", "");
        try {
            Statement stmt = connectionDatabase.createStatement();
            stmt.execute("UPDATE webcontent SET title = '" + title + "' WHERE url = '" + url + "';");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateURLAlreadyIndexed(String url, boolean alreadyIndexed) {
        try {
            Statement stmt = connectionDatabase.createStatement();
            stmt.execute("UPDATE webcontent SET already_indexed = " + (alreadyIndexed ? "true" : "false") + " WHERE url = '" + url + "';");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int getIdFromURL(String url) {
        try {
            Statement stmt = connectionDatabase.createStatement();
            ResultSet result = stmt.executeQuery("SELECT id FROM webcontent WHERE url = '" + url + "' LIMIT 1;");

            result.next();
            return result.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteIndiciesForURL(String url) {
        int id = getIdFromURL(url);
        try {
            Statement stmt = connectionDatabase.createStatement();
            stmt.execute("DELETE FROM webindex WHERE contentID = " + id + ";");


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static String prepareIndex(String idx) {
        idx = idx.replaceAll("'", "");
        idx = idx.replaceAll("\"", "");
        return idx;
    }
    public void insertIndicies(String url, ArrayList<String> indicies) {
        int id = getIdFromURL(url);
        try {
            Statement stmt = connectionDatabase.createStatement();
            StringBuilder sql = new StringBuilder("INSERT INTO webindex VALUES ");
            for (int i = 0; i < indicies.size(); i++) {
                sql.append("(0, ").append(id).append(", '").append(prepareIndex(indicies.get(i))).append("')");
                if (i < indicies.size() - 1) {
                    sql.append(", ");
                }
            }
            stmt.execute(sql + ";");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static SearchResult processQuery(String query) {
        final String[] strs = query.split(" ");
        ArrayList<String> tokens = new ArrayList<>();
        for (String str : strs) {
            tokens.add(str);
        }
        return processTokens(tokens);
    }
    public static SearchResult processTokens(ArrayList<String> tokens) {
        SearchResult result = new SearchResult();
        Instant start = Instant.now();

        for (String token : tokens) {
            try {
                Statement stmt = connectionMain.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT url, title FROM webcontent, webindex WHERE webcontent.id = webindex.contentID AND idx = '" + token + "';");

                while (rs.next()) {
                    result.insert(rs.getString(1), rs.getString(2));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        Instant end = Instant.now();
        long millis = Duration.between(start, end).toMillis();
        result.elapsedTime = (millis/1000) + "." + (millis%1000);

        return result;
    }

    public static int getWebContentCount() {
        try {
            Statement stmt = connectionMain.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) FROM webcontent;");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getWebIndexCount() {
        try {
            Statement stmt = connectionMain.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) FROM webindex;");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getDepthFromURL(String url) {
        try {
            Statement stmt = connectionDatabase.createStatement();
            ResultSet result = stmt.executeQuery("SELECT depth FROM webcontent WHERE url = '" + url + "';");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

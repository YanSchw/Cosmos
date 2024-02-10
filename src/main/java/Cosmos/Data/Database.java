package Cosmos.Data;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    private final static String url = "jdbc:mysql://localhost:3306/";
    private final static String username = "root";
    private final static String password = "1234";
    private static Connection conn;
    public static void setup() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

        try {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE DATABASE IF NOT EXISTS cosmos;");
            stmt.execute("USE cosmos;");
            stmt.execute("CREATE TABLE IF NOT EXISTS webcontent(id INT PRIMARY KEY AUTO_INCREMENT, url VARCHAR(1024), date DATE);");
            stmt.execute("CREATE TABLE IF NOT EXISTS webindex(id INT PRIMARY KEY AUTO_INCREMENT, contentID INT, idx VARCHAR(1024), FOREIGN KEY (contentID) REFERENCES webcontent(id));");

            // Insert default data into Database if empty
            // Including Seed URLs
            ResultSet result = stmt.executeQuery("SELECT * FROM webcontent LIMIT 1;");
            if (!result.next()) {
                //stmt.execute("INSERT INTO webcontent VALUES (0, 'http://www.google.com', '2000-01-01');");
                stmt.execute("INSERT INTO webcontent VALUES (0, 'https://en.wikipedia.org/wiki/World_Wide_Web', '2000-01-01');");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOldestURL() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT url FROM webcontent ORDER BY date ASC LIMIT 1;");

            result.next();
            return result.getString(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void insertNewURL(String url) {
        if (url.contains("'") || url.contains("\"")) {
            return;
        }
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT url FROM webcontent WHERE url = '" + url + "' LIMIT 1;");

            if (!result.next()) {
                stmt.execute("INSERT INTO webcontent VALUES (0, '" + url + "', '1999-01-01');");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void updateURLDate(String url) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("UPDATE webcontent SET date = '2024-02-08' WHERE url = '" + url + "'");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getIdFromURL(String url) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT id FROM webcontent WHERE url = '" + url + "' LIMIT 1;");

            result.next();
            return result.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void deleteIndiciesForURL(String url) {
        int id = getIdFromURL(url);
        try {
            Statement stmt = conn.createStatement();
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
    public static void insertIndicies(String url, ArrayList<String> indicies) {
        int id = getIdFromURL(url);
        try {
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO webindex VALUES ";
            for (int i = 0; i < indicies.size(); i++) {
                sql += "(0, " +id+ ", '" + prepareIndex(indicies.get(i)) + "')";
                if (i < indicies.size() - 1) {
                    sql += ", ";
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

        for (String token : tokens) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT url FROM webcontent, webindex WHERE webcontent.id = webindex.contentID AND idx = '" + token + "';");

                while (rs.next()) {
                    result.insertURL(rs.getString(1));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    public static int getWebContentCount() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) FROM webcontent;");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getWebIndexCount() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) FROM webindex;");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

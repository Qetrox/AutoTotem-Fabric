package net.qlient.autototem.util;

import com.google.gson.*;
import net.qlient.autototem.client.autototem;

import java.io.IOException;

public class VersionChecker {

    private static final String version = autototem.VERSION;
    public static String latestversion = null;

    public static boolean isLatest() {
        if (latestversion == null) {
            return true;
        }

        int latestVersion1 = Integer.parseInt(latestversion.replace(".", ""));
        int version1 = Integer.parseInt(version.replace(".", ""));
        if(latestVersion1 > version1) {
            return false;
        }

        return true;
    }

    public static String getVersion() {
        return latestversion;
    }

    public static void getOnlineVersion() {
        try {
            String response = HttpsUtil.sendGETSSL("https://api.qlient.net/data/");
            Gson gson = new GsonBuilder().create();
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            JsonArray versionsArray = jsonObject.getAsJsonArray("versions");

            JsonObject modsObject = versionsArray.get(0).getAsJsonObject();
            JsonArray modsArray = modsObject.getAsJsonArray("mods");

            JsonObject qlientcapesObject = null;
            for (JsonElement modElement : modsArray) {
                JsonObject modObject = modElement.getAsJsonObject();
                if (modObject.has("autototem")) {
                    qlientcapesObject = modObject;
                    break;
                }
            }

            if (qlientcapesObject != null) {
                latestversion = qlientcapesObject.get("autototem").getAsString();
            } else {
                latestversion = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            latestversion = null;
        }
    }

}
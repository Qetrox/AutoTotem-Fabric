package net.qlient.autototem.util;

import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

public class HttpsUtil {

    private static final String USER_AGENT = "AutoTotem(2.0) Version Checker";

    public static String sendGETSSL(String URL) throws IOException {
        URL obj = new URL(URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }
        return "NONE";
    }
}
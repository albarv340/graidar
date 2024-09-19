package red.bread.graidar.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebRequest {
    public static String getData(String url) {
        try {
            StringBuilder result = new StringBuilder();
            URL urlObject = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void postData(String url, String data) {
        try {
            URL urlObject = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            int length = out.length;

            con.setFixedLengthStreamingMode(length);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.connect();
            try (OutputStream os = con.getOutputStream()) {
                os.write(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package de.exercises.pithonAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PiThonAPI {

    public static void main(String[] args) {
        try {
            String params = "?number=" + args[0] + "&amount=" + args[1] + "&index=0";
            URL url = new URL("http://localhost:5000/api" + params);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                in.close();
                System.out.println(response);
            } else {
                System.out.println(con.getResponseCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

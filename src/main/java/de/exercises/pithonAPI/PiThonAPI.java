package de.exercises.pithonAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PiThonAPI {
    public static final String PITHON_URL = "http://localhost:5000/api";

    public static void main(String[] args) {
        System.out.println(getDigits(args[0], Integer.parseInt(args[1])));
    }

    public static String getDigits(String number, int amount) {
        try {
            String params = "?number=" + number + "&amount=" + amount + "&index=0";
            URL url = new URL(PITHON_URL + params);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            con.setDoOutput(true);

            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                in.close();
                return response;
            } else {
                return "Error: received status code" + con.getResponseCode();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

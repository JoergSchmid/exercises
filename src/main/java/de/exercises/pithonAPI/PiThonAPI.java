package de.exercises.pithonAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PiThonAPI {
    public static final String PITHON_URL = "http://localhost:5000/api";

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args[0] == null) {
            System.out.println("argument(s) missing: pi|e|sqrt2 [amount]");
            return;
        }
        String number = args[0];
        if(args.length >= 3) {
            getDigitsWithBasicAuth(number, args[1], args[2]);
            return;
        }
        int amount;
        if(args[1] != null) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Unable to read amount (second argument)");
                return;
            }
        } else {
            amount = 10;
        }
        System.out.println(getDigits(number, amount));
    }

    private static void getDigitsWithBasicAuth(String number, String username, String password) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                })
                .build();

        // Request current progress
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(PITHON_URL + "?number=" + number))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        // Request next 10
        request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(PITHON_URL + "?number=pi&amount=10"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("+ " + response.body());
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
                return "Error: received status code " + con.getResponseCode();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

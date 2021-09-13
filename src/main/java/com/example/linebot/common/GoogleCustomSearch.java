package com.example.linebot.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.linebot.database.DatabaseConnection;
import com.example.linebot.database.GoogleKeyset;

public class GoogleCustomSearch {
    private String apiUrl = "https://www.googleapis.com/customsearch/v1/siterestrict?q=";
    private String apiKey = "AIzaSyCMD8B9JLNvN7Oie2pAwf8EuhAldMG8bUs";
    private String engineKey = "cd39043d76c75761e";
    private String optionParameters = "";

    public GoogleCustomSearch(final int startPage, final String... optionParams) {
        if (startPage > 1) {
            final int i = ((startPage - 1) * 9) + startPage;
            this.optionParameters = this.optionParameters + "&start=" + i;
        }
        for (String param : optionParams) {
            this.optionParameters = this.optionParameters + "&" + param;
        }
    }

    public GoogleCustomSearch youtube() {
        this.engineKey = "e6dadd7352386334c";
        return this;
    }

    public GoogleCustomSearch amazon() {
        this.engineKey = "38e147f2892adf77c";
        return this;
    }

    public GoogleCustomSearch wallpaper() {
        this.engineKey = "1154ed00919477dc3";
        return this;
    }

    public GoogleCustomSearch wiki() {
        this.engineKey = "ea360a520439ba624";
        return this;
    }

    public GoogleCustomSearch pixiv() {
        this.engineKey = "4714a949637a25aa2";
        return this;
    }

    public GoogleCustomSearch noLimitSearch(final String lineId) throws SQLException {
        GoogleKeyset keyset;
        if ((keyset = new DatabaseConnection().getGoogleKeyset(lineId)) != null) {
            this.apiUrl = "https://www.googleapis.com/customsearch/v1?q=";
            this.apiKey = keyset.getApiKey();
            this.engineKey = keyset.getEngineKey();
        }
        return this;
    }

    public ArrayList<String> getUrl(final String searchWord) throws IOException, URISyntaxException {

        URL searchUrl = new URL(apiUrl + searchWord + "&key=" + apiKey + "&cx="
                + engineKey + optionParameters);
        final HttpURLConnection urlConnection = (HttpURLConnection) searchUrl.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");

        final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

        return extractionUrl(br, urlConnection);

    }

    private static ArrayList<String> extractionUrl(BufferedReader br, HttpURLConnection connection)
            throws IOException, URISyntaxException {

        String output;
        String link = null;
        final ArrayList<String> linkList = new ArrayList<>();
        while ((output = br.readLine()) != null) {
            if (output.contains("\"link\": \"")) {
                link = output.substring(output.indexOf("\"link\": \"") + ("\"link\": \"").length(),
                        output.indexOf("\","));
                if (link.contains("http://")) {
                    continue;
                }
                System.out.println(link);
                linkList.add(link);
            }
        }
        connection.disconnect();
        return linkList;

    }

}

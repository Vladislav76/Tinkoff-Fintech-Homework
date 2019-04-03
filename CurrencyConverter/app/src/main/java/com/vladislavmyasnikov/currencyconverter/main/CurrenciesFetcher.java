package com.vladislavmyasnikov.currencyconverter.main;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CurrenciesFetcher {

    private static final String TAG = "CurrenciesFetcher";

    /**
     * Fetches from the server a list of available currencies
     * represented by a JSON string and parses it.
     *
     * @return list of available currencies represented by List
     */
    public List<String> fetchCurrenciesList() {
        List<String> items = new ArrayList<>();

        try {
            String jsonString = getUrlString("https://free.currencyconverterapi.com/api/v6/currencies?apiKey=098041bf9a8b3b1c3e35");
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        catch (NullPointerException e) {
            return null;
        }

        return items;
    }

    /**
     * Fetches from the server a list of currency rate
     * represented by a JSON string and parses it.
     *
     * @return list of currency rate represented by List
     */
    public List<Double> fetchConvertingData(String... queries) {
        List<Double> data = new ArrayList<>();

        try {
            String jsonString = getUrlString("https://free.currencyconverterapi.com/api/v6/convert?apiKey=098041bf9a8b3b1c3e35&compact=ultra&q=" +
                            combineQueries(queries));
            JSONObject jsonBody = new JSONObject(jsonString);
            parseData(data, jsonBody);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to fetch converting data", e);
        }
        catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON", e);
        }
        catch (NullPointerException e) {
            return null;
        }

        return data;
    }

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }
        catch (IOException e) {
            return null;
        }
        finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException, NullPointerException {
        return new String(getUrlBytes(urlSpec));
    }

    private void parseItems(List<String> items, JSONObject jsonBody) throws JSONException {
        JSONObject resultsJsonObject = jsonBody.getJSONObject("results");
        JSONArray currenciesJsonArray = resultsJsonObject.names();
        for (int i = 0; i < currenciesJsonArray.length(); i++) {
            String name = currenciesJsonArray.getString(i);
            items.add(name);
        }
    }

    private void parseData(List<Double> data, JSONObject jsonBody) throws JSONException {
        JSONArray currenciesJsonArray = jsonBody.names();
        for (int i = 0; i < currenciesJsonArray.length(); i++) {
            Double value = jsonBody.getDouble(currenciesJsonArray.getString(i));
            data.add(value);
        }
    }

    private String combineQueries(String... queries) {
        StringBuilder sb = new StringBuilder(queries[0]);
        for (int i = 1; i < queries.length; i++) {
            sb.append(",");
            sb.append(queries[i]);
        }
        return sb.toString();
    }
}

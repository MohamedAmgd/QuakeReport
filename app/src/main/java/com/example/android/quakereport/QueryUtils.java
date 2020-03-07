package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {

    private String Url ;
    public static final String LOG_TAG = "QueryUtils";

    public QueryUtils(String url) {
        Url = url;
    }

    public URL URLMaker(String input){
        if (input.isEmpty() || input == null){
            return null;
        }

        URL url = null;
        try {
            url = new URL(input);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public String makeHttpRequest(URL url){
        if(url == null){
            return null;
        }
        String JSONResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(35000);
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JSONResponse = JSONResponseMaker(inputStream);
            }else {
                Log.e(LOG_TAG, "the response code is " + urlConnection.getResponseCode());
            }

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
             //   inputStream.close();
            }
        }
        return JSONResponse;
    }

    public String JSONResponseMaker(InputStream inputStream){
        if(inputStream == null){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            line = bufferedReader.readLine();
            while (line != null && !line.isEmpty() ) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }catch (IOException e){

        }

        return stringBuilder.toString();
    }
    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public ArrayList<Earthquake> extractEarthquakes() {
        String JSONInput = makeHttpRequest(URLMaker(Url));
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject root =  new JSONObject(JSONInput);

            JSONArray features = root.getJSONArray("features");

            for (int i = 0 ; i<features.length() ; i++){
                JSONObject currentFeature = features.getJSONObject(i);

                JSONObject properties = currentFeature.getJSONObject("properties");

                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                Long time = properties.getLong("time");
                String url = properties.getString("url");

                earthquakes.add(new Earthquake(mag,place,time,url));

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        Log.i(LOG_TAG,"extractEarthquakes");

 /*       try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 */
        // Return the list of earthquakes
        return earthquakes;
    }



}
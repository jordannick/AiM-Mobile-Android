package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by jordan_n on 8/13/2014.
 */


//New HTTPURLConnection implementation supposedly better, HTTPClient was deprecated. Doesn't really work yet.
public class NetworkGetJSON_TEST {
    private final String TAG = "NetworkGetJSON";

    static JSONArray jArray = null;

    private CurrentUser sCurrentUser; //TODO: clear current user if different user successfully logs in


    public NetworkGetJSON_TEST() {
    }

    // http://developer.android.com/training/basics/network-ops/connecting.html
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    public ResponsePair downloadUrl(String inputUrl, boolean isArray, ResponsePair responsePair) throws IOException {
        InputStream inputStream = null;

        try {

            URL url = new URL(inputUrl);
            Log.d(TAG, "Attempting GET from: " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000 /* milliseconds */);
            //connection.setConnectTimeout(15000 /* milliseconds */);

            //connection.setDoInput(true);
            // Starts the query
            connection.connect();
            int statusCode = connection.getResponseCode();
            Log.d(TAG, "The response is: " + statusCode);

            //TODO: why does it code 302
            //Endless redirects....
            if (statusCode == 302){
                return downloadUrl(connection.getHeaderField("Location"), isArray, responsePair);
            }




            if (statusCode == 200){

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line+"\n");
                }
                bufferedReader.close();

                responsePair = convertResponseString(isArray, stringBuilder.toString(), responsePair);

            } else if (statusCode == 401){
                responsePair.setStatus(ResponsePair.Status.AUTH_FAIL);
            } /*else {
                responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            }
*/
            return responsePair;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    private ResponsePair convertResponseString(boolean isArray, String responseString, ResponsePair responsePair){
            try {
                if (isArray) {
                    jArray = new JSONArray(responseString); // Parse String to JSON object
                    responsePair.setJarray(jArray);
                } else {
                    responsePair.setReturnedString(responseString); //Parse String only (for lastUpdated)
                }
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing JSON data " + e.toString());
                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
            }
        return responsePair;
    }



}
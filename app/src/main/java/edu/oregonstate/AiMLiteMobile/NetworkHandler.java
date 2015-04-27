package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jordan_n on 4/22/2015.
 */
public class NetworkHandler {
    private final String TAG = "NetworkHandler";

    private static NetworkHandler sNetworkHandler;
    private static CurrentUser sCurrentUser;
    private static Context sContext;


    public NetworkHandler(Context c){
        sCurrentUser = CurrentUser.get(c);
    }

    public static NetworkHandler get(Context c){
        if(sNetworkHandler == null){
            sNetworkHandler = new NetworkHandler(c);
            sContext = c;
        }
        return sNetworkHandler;
    }


    //GET request on api-test.facilities.oregonstate.edu will go through two redirects. Cookie for session is given on first request.
    public ResponsePair downloadUrl(String inputUrl, boolean isArray, ResponsePair responsePair, HttpURLConnection connection) throws IOException {

        try {
            URL url = new URL(inputUrl);
            Log.d(TAG, "Attempting GET from: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);

            //Save the cookies which include session variable. Session variable from server lasts ~8 days.
            //TODO: check if saved session within cookie has expired?
            if (sCurrentUser.getCookies() == null) {
                String cookies = connection.getHeaderField("Set-Cookie");
                Log.d(TAG, "The cookie is: " + cookies);
                sCurrentUser.setCookies(cookies);
            }else {
                connection.setRequestProperty("Cookie", sCurrentUser.getCookies());
            }

            //Ready to connect
            connection.connect();
            int statusCode = connection.getResponseCode();
            Log.d(TAG, "The response is: " + statusCode + " " + connection.getResponseMessage());

            switch (statusCode){
                case HttpURLConnection.HTTP_OK:             // 200 OK

                    //Read the data response line by line, turn it into a string
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    responsePair = convertResponseString(isArray, stringBuilder.toString(), responsePair);

                case HttpURLConnection.HTTP_MOVED_TEMP:     // 302 Moved Temporarily OR 302 Found

                    //Redirect detected, try again with supplied location to redirect to. Keep same connection.
                    downloadUrl(connection.getHeaderField("Location"),isArray, responsePair, connection);

                case HttpURLConnection.HTTP_UNAUTHORIZED:   // 401

                    responsePair.setStatus(ResponsePair.Status.AUTH_FAIL);

                default:                                    // Any other status code

                    responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            }

            return responsePair;
        } catch (ConnectException e){
            responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            return responsePair;
        }
        finally { //Clean up
            connection.disconnect();
        }
    }

    //Usage: pass in a List<NameValuePair> created with values based on the method we want to call
    public ResponsePair postURL(String inputUrl, String username/*, String date, int hours, String workOrderPhaseID, String actiontaken, String note, String newStatus, String sectionValue, String timestamp*/){

        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        HttpURLConnection connection = null;

        try {
            URL url = new URL(inputUrl);
            Log.d(TAG, "Attempting GET from: " + url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(false);
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setDoOutput(true); //allow us to write to output stream for POST request

            /*
            if (sCurrentUser.getCookies() == null) {
                String cookies = connection.getHeaderField("Set-Cookie");
                Log.d(TAG, "The cookie is: " + cookies);
                sCurrentUser.setCookies(cookies);
            }else {
                connection.setRequestProperty("Cookie", sCurrentUser.getCookies());
            }
            */

            //Start sending the data

            String urlParameters = "param1=a&param2=b&param3=c";

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            // String urlParameters = "username="+username; //etc
            writer.write(urlParameters);
            writer.flush();

            //

            int statusCode = connection.getResponseCode();
            Log.d(TAG, "The response is: " + statusCode + " " + connection.getResponseMessage());

            switch (statusCode){
                case HttpURLConnection.HTTP_OK:             // 200 OK


                case HttpURLConnection.HTTP_MOVED_TEMP:     // 302 Moved Temporarily OR 302 Found


                case HttpURLConnection.HTTP_UNAUTHORIZED:   // 401

                    responsePair.setStatus(ResponsePair.Status.AUTH_FAIL);

                default:                                    // Any other status code

                    responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            }

            return responsePair;
        } catch (IOException e){
            responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            return responsePair;
        }
        finally { //Clean up
            connection.disconnect();
        }
    }

    private ResponsePair convertResponseString(boolean isArray, String responseString, ResponsePair responsePair){
        JSONArray jArray;
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
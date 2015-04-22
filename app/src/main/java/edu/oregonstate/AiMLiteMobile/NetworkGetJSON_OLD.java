package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by jordan_n on 8/13/2014.
 */



public class NetworkGetJSON_OLD {
    private final String TAG = "NetworkGetJSON_OLD";

    static JSONArray jArray = null;

    private CurrentUser sCurrentUser;


    public NetworkGetJSON_OLD() {
    }

    public ResponsePair getJSONFromUrl(String url, boolean isArray) {
        Log.i(TAG, "Attempting HTTP GET from: " + url);

        StringBuilder builder = new StringBuilder();

        sCurrentUser = CurrentUser.get(null);

        HttpClient client = new DefaultHttpClient();

        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        HttpGet httpGet = new HttpGet(url);


        try {
            //HttpResponse response = client.execute(httpPost);
            HttpResponse response = client.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                Log.i(TAG, "HTTP GET Success");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("==>", "Failed to download file");

                if (statusCode == 401){
                    responsePair.setStatus(ResponsePair.Status.AUTH_FAIL);
                    return responsePair;
                }

                responsePair.setStatus(ResponsePair.Status.NET_FAIL);
                return responsePair;


            }
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
            responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            return responsePair;
        }
        catch (IOException e) {
            e.printStackTrace();
            responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            return responsePair;
        }

        if(isArray){
            // Parse String to JSON object
            try {
                jArray = new JSONArray( builder.toString());
                responsePair.setJarray(jArray);
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing JSON data " + e.toString());

                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                return responsePair;

            }
        }else{
            //Parse String only -- for lastUpdated
            try {
                responsePair.setReturnedString(builder.toString());
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (Exception e){
                Log.e("JSON Parser", "Error parsing String data " + e.toString());
                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                return responsePair;
            }
        }

        return responsePair;
    }



}
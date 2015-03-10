package edu.oregonstate.AiMLiteMobile;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

public class JSONParser {
    private final String TAG = "JSONParser";

    static InputStream iStream = null;
    static JSONArray jarray = null;
    static String json = "";
    private CurrentUser sCurrentUser;


    public JSONParser() {
    }

    public ResponsePair getJSONFromUrl(String url, boolean isArray) {
        Log.d(TAG, "Attemping http to: " + url);

        StringBuilder builder = new StringBuilder();

        sCurrentUser = CurrentUser.get(null);

        HttpClient client = new DefaultHttpClient();

        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        HttpPost httpPost = new HttpPost(url);
       // httpPost.setHeader("Connection","Keep-Alive");

        try {
            HttpResponse response = client.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                Log.e("==>", "Success");
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                //TODO: check for different received status codes? 403, 404...
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
                jarray = new JSONArray( builder.toString());
                responsePair.setJarray(jarray);
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());

                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                return responsePair;

            }
        }else{
            //Parse String
            responsePair.setReturnedString(builder.toString());
            responsePair.setStatus(ResponsePair.Status.SUCCESS);
        }

        return responsePair;
    }



}
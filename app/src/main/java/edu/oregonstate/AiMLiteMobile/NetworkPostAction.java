package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jordan_n on 3/12/2015.
 */
public class NetworkPostAction {
    private final String TAG = "NetworkPostAction";

    private static CurrentUser sCurrentUser;

    public NetworkPostAction(Context c){
        sCurrentUser = CurrentUser.get(c);
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

}

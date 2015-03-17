package edu.oregonstate.AiMLiteMobile;

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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jordan_n on 3/12/2015.
 */
public class SubmitChange {
    private CurrentUser sCurrentUser;

    public SubmitChange(){
    }

    //Usage: pass in a List<NameValuePair> created with values based on the method we want to call
    public ResponsePair postToURL(String url, List<NameValuePair> nameValuePairs/*, String username, String date, int hours, String workOrderPhaseID, String actiontaken, String note, String newStatus, String sectionValue, String timestamp*/){
        sCurrentUser = CurrentUser.get(null);
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);


        //TODO 3/12/2015 - POST variables in body or appended in url? Does it matter?
        //Attach the variables to the message
       /* List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //Variables common to all the methods
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("timestamp", timestamp));
        nameValuePairs.add(new BasicNameValuePair("workOrderPhaseId", workOrderPhaseID));

        //Unique variables
        if (hours != 0) nameValuePairs.add(new BasicNameValuePair("hours", String.valueOf(hours)));
        else if (wo)
*/

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        try {
            HttpResponse response = client.execute(httpPost);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                Log.e("==>", "Success");
            } else {
                //TODO: check for different received status codes? 403, 404...
                Log.e("==>", "Failed to POST");

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

    return responsePair;
    }

}


/*

http://api-test.facilities.oregonstate.edu/1.0/WorkOrder/<method>

o	addTime($username, $date, $hours, $workOrderPhaseId, [$timeType], $timeStamp)
	Adds hours to the specified work order / phase for the date.
	Uses default time type, unless otherwise specified
	Timestamp should give the time at which this update took place.
	Example: coming soon.

o	addActionTaken($username, $workOrderPhaseId, $actionTaken, $timeStamp)
	Adds an Action Taken to the specified work order / phase
	Timestamp should give the time at which this update took place.

o	addNote($username, $workOrderPhaseId, $note, $timeStamp)
	Adds a note to the specified Work Order / Phase
	Timestamp should give the time at which this update took place.

o	updateStatus($username, $workOrderPhaseId, $newStatus, $timeStamp)
	Updates the status of the specified Work Order / Phase to the new status listed.
	Timestamp should give the time at which this update took place.

o	updateSection($usename, $workOrderPhaseId, $value, $timeStamp)
	Allowed values include ‘Backlog’ and ‘Daily Assignment’
	Timestamp should give the time at which this update took place.

 */
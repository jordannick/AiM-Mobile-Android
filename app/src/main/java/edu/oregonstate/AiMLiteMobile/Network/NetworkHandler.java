package edu.oregonstate.AiMLiteMobile.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Models.Note;
import edu.oregonstate.AiMLiteMobile.Helpers.ResponsePair;

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

    private class ConnectionResponse {
        int statusCode;
        String redirectUrl;
    }

    //GET request on api-test.facilities.oregonstate.edu will go through two redirects. Cookie for session is given on first request.
    public ResponsePair downloadUrl(String inputUrl, boolean isArray, ResponsePair responsePair, HttpURLConnection connection) throws IOException {

        try {
            URL url = new URL(inputUrl);        Log.d(TAG, "Attempting GET from: " + url);
            connection = (HttpURLConnection) url.openConnection();
            //GET and Timeout
            connection.setRequestMethod("GET"); connection.setReadTimeout(10000); connection.setConnectTimeout(15000);

            //Cookies just used through redirects?
            if (sCurrentUser.getCookies() == null) {
                String cookies = connection.getHeaderField("Set-Cookie");
                //Log.d(TAG, "The cookie is: " + cookies);
                sCurrentUser.setCookies(cookies);
            }else {
                connection.setRequestProperty("Cookie", sCurrentUser.getCookies());
            }

            //Ready to connect
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
                    break;
                case HttpURLConnection.HTTP_MOVED_TEMP:     // 302 Moved Temporarily OR 302 Found
                    Log.d(TAG, "Redirect URL. Location: " + connection.getHeaderField("Location"));
                    //Redirect detected, try again with supplied location to redirect to. Keep same connection.
                    downloadUrl(connection.getHeaderField("Location"), isArray, responsePair, connection);
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:   // 401
                    responsePair.setStatus(ResponsePair.Status.AUTH_FAIL);
                    break;
                default: // Any other status code
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


    public void postUnsyncedActions() {
        ResponsePair responsePair;
        ArrayList<Action> unsyncedActions = sCurrentUser.getUnsyncedActions();
        //TODO: how are actions "synced"
        for (int i = 0; i < unsyncedActions.size(); i++) {
            Action unsyncedAction = unsyncedActions.get(i);
            Log.d(TAG, " ------------ Syncing... ------------ ");
            Log.d(TAG, " ------------ " + unsyncedAction.getWorkOrder().getProposalPhase() + " ------------ ");
            responsePair = postAction(unsyncedAction);
            if (responsePair.getStatus() == ResponsePair.Status.SUCCESS) {
                Log.d(TAG, " ------------ Action synced ------------ ");
                unsyncedAction.setSynced(true);
            } else {
                Log.d(TAG, "------------ Synced failed ------------ Status: " + responsePair.getStatus());
            }

        }
    }

    // FUNCTION
    //      Separates the various POSTS that need to be made for a new Action.
    //      Calls postToURL() for each necessary POST.
    //      Returns the result of checkResponsePairs() which returns FAIL if any POST fails, or SUCCESS if they all pass
    public ResponsePair postAction(Action action){
        ResponsePair returnResponsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        //Build URL
        ArrayList<ResponsePair> responsePairs = new ArrayList<>();
        ResponsePair addTimeResponsePair = null, addActionTakenResponsePair = null;

        String encodedParams = null;
        URL addTimeURL = null, addActionTakenURL = null, addNoteURL = null, updateStatusURL = null;
        String baseURL = sCurrentUser.getBaseURL();
        String encodingCharSet = "UTF-8";

        try {
            // ---- Add Time ----
            if(action.getHours() != -1) { // -1 is DEFAULT value if none is entered...
                Log.d(TAG, action.getHours() + " hour(s) found.");
                //Build URL and call postToURL
                addTimeURL = new URL(baseURL + "/addTime");
                encodedParams = buildAddTimeParams(action, encodingCharSet);
                addTimeResponsePair = postToURL(addTimeURL, encodedParams);
                responsePairs.add(addTimeResponsePair);
            }else{
                Log.d(TAG, "No hours found.");
            }

            // ---- AddActionTaken ----
            if(action.getActionTaken() != Action.ActionTaken.NO_ACTION) { //If an ACTION was inputted. NO_ACTION is Default
                Log.d(TAG, action.getActionTaken().toString() + " found.");
                //Build URL and call postToURL
                addActionTakenURL = new URL(baseURL + "/addActionTaken");
                encodedParams = buildAddActionTakenParams(action, encodingCharSet);
                addActionTakenResponsePair = postToURL(addActionTakenURL, encodedParams);
                responsePairs.add(addActionTakenResponsePair);
            }else{
                Log.d(TAG, "No action found.");
            }

            // ---- AddNote ----
            if(action.getNotes().size() > 0) {  //If there are ANY notes
                Log.d(TAG, action.getNotes().size() + " note(s) found.");
                //Build URL and call postToURL
                addNoteURL = new URL(baseURL + "/addNote");
                String[] encodedParamsArray = buildAddNoteParams(action, encodingCharSet);

                for (int i = 0; i < encodedParamsArray.length; i++) {
                    ResponsePair responsePair = postToURL(addNoteURL, encodedParamsArray[i]);
                    responsePairs.add(responsePair);
                }
            }else{
                Log.d(TAG, "No note(s) found.");
            }

            // ---- UpdateStatus ----
            if(action.getUpdatedStatus() != null){
                Log.d(TAG, "Updated status to " + action.getUpdatedStatus());
                //Build URL and call postToURL
                updateStatusURL = new URL(baseURL + "/updateStatus");
                encodedParams = buildUpdateStatusParams(action, encodingCharSet);
                responsePairs.add(postToURL(updateStatusURL, encodedParams));
            }else{
                Log.d(TAG, "No update to status.");
            }

            returnResponsePair = checkResponsePairs(responsePairs);
        } catch (Exception e){
            Log.e(TAG, "Exception e: " + e);
            returnResponsePair.setStatus(ResponsePair.Status.FAIL);
        }

        // ------------ LOG THAT DISPLAYS RESPONSE AND STATUS CODE ------------
        /*Log.d(TAG, ".\n\n ------------ POST Series Action ------------ \n\n Post #1: AddTime :::\n\t status: " + addTimeResponsePair.getStatusInt() +
                "\n\t response: " + addTimeResponsePair.getReturnedString() + "\n\n Post #2: AddActionTaken :::\n\t status: " +
                addActionTakenResponsePair.getStatusInt() + "\n\t response: " + addActionTakenResponsePair.getReturnedString());*/
        return returnResponsePair;
    }

    private ResponsePair checkResponsePairs(ArrayList<ResponsePair> pairs){
        ResponsePair returnPair = new ResponsePair(ResponsePair.Status.NONE, null);
        String returnedStrings = "";

        boolean statusOk = true;
        for (int i = 0; i < pairs.size(); i++) {
            ResponsePair rp = pairs.get(i);
            if(rp.getStatusInt() != 200){
                statusOk = false;
            }
            returnedStrings += rp.getReturnedString() + " ||| ";
            Log.d(TAG, "responsePair #" + i + " : status= " + rp.getStatus() + ", statusInt= " + rp.getStatusInt() + ", response= " + rp.getReturnedString());

        }
        if(!statusOk){
            returnPair.setStatus(ResponsePair.Status.FAIL);
        }else returnPair.setStatus(ResponsePair.Status.SUCCESS);
        returnPair.setReturnedString(returnedStrings);

        return returnPair;
    }


     /*  FUNCTION
            Builds encodedString of all necessary parameters for the AddActionTaken POST.
     */
    private String buildAddActionTakenParams(Action action, String encoding){
        //AddActionTaken ===== addActionTaken($username, $workOrderPhaseId, $actionTaken, $timeStamp)
        String userName = sCurrentUser.getUsername();
        String workOrderPhaseIdString = action.getWorkOrder().getProposalPhase();
        String actionTaken = action.getActionTakenString();
        String timeStampString = String.valueOf(System.currentTimeMillis());

        //Encode and create parameter String
        String[] stringArgs = {"username", userName, "workOrderPhaseId", workOrderPhaseIdString, "actionTaken", actionTaken, "timeStamp",  timeStampString};

        return buildEncodedString(stringArgs, encoding);
    }

    private String[] buildAddNoteParams(Action action, String encoding){
        //AddNote ===== addNote($username, $workOrderPhaseId, $note, $timeStamp)
        ArrayList<Note> notes = action.getNotes();
        String[] encodedStrings = new String[notes.size()]; //String array to hold encodedParamString for EACH note.

        String userName = sCurrentUser.getUsername();
        String workOrderPhaseIdString = action.getWorkOrder().getProposalPhase();
        String timeStampString = String.valueOf(System.currentTimeMillis());
        //Iterate through each note, building an encodedParamString and adding it to the returnArray
        for (int i = 0; i < notes.size(); i++) {
            String noteString = notes.get(i).toString();
            String[] stringArgs = {"username", userName, "workOrderPhaseId", workOrderPhaseIdString, "note", noteString, "timeStamp",  timeStampString};
            encodedStrings[i] = buildEncodedString(stringArgs, encoding);
        }

        return encodedStrings;
    }


    /*  FUNCTION
           Builds encodedString of all necessary parameters for the AddTime POST.
    */
    private String buildAddTimeParams(Action action, String encoding){
        //AddTime ===== addTime($username, $date, $hours, $workOrderPhaseId, [$timeType], $timeStamp)
        String userName = sCurrentUser.getUsername();
        String dateString = action.getDateStamp().toString();
        String hoursString = String.valueOf(action.getHours());
        String workOrderPhaseIdString = action.getWorkOrder().getProposalPhase();
        String timeTypeString = action.getTimeType().toString();
        String timeStampString = String.valueOf(System.currentTimeMillis());

        //Encode and create parameter String
        String[] stringArgs = {"username", userName, "date", dateString, "hours", hoursString,
                "workOrderPhaseId", workOrderPhaseIdString, "timeType", timeTypeString, "timeStamp",  timeStampString/*, "token", sCurrentUser.getToken()*/};

        return buildEncodedString(stringArgs, encoding);
    }

    private String buildUpdateStatusParams(Action action, String encoding){
        //UpdateStatus ===== updateStatus($username, $workOrderPhaseId, $newStatus, $timeStamp)
        String userName = sCurrentUser.getUsername();
        String workOrderPhaseIdString = action.getWorkOrder().getProposalPhase();
        String newStatus = action.getUpdatedStatus();
        String timeStampString = String.valueOf(System.currentTimeMillis());

        String[] stringArgs = {"username", userName, "workOrderPhaseId", workOrderPhaseIdString, "newStatus", newStatus, "timeStamp",  timeStampString};

        return  buildEncodedString(stringArgs, encoding);
    }


    public ResponsePair postToURL(URL url, String encodedParams){
        try {
            Log.d(TAG, "------------------------------------Starting POST to URL: " + url.toString());
            //Open connection
            ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
            HttpURLConnection c = null;
            c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST"); c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            c.setRequestProperty("Content-Language", "en-US"); c.setRequestProperty("Content-Length", "" + Integer.toString(encodedParams.getBytes().length));
            c.setUseCaches(false);  c.setDoInput(true);  c.setDoOutput(true);

            //Send Request
            DataOutputStream wr = null;
            wr = new DataOutputStream(c.getOutputStream());
            wr.writeBytes(encodedParams);
            wr.flush();
            wr.close();

            //Get Response
            InputStream is = c.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {    //Building response...
                response.append(line);
                response.append("\r");
            }
            rd.close();

            //Build and return responsePair
            responsePair.setReturnedString(response.toString());
            responsePair.setStatusInt(c.getResponseCode());
            return  responsePair;
        }catch (Exception e){
            Log.e(TAG, "Exception e: " + e);
        }
        return null;
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


    /*
    FUNCTION
        PARAMS:  args = Array of Strings with pattern NAME, VALUE, NAME, VALUE, ... etc
                 encodingCharSet = Encoding for returned string
        Builds encoded String and returns it.
     */
    public String buildEncodedString(String[] args, String encodingCharSet){
        String returnString = "";
        try {
            for (int i = 0; i < args.length; i++) {
                String argument = args[i];
                if (((i + 2) % 2) == 0) { // i is ODD, meaning it's the Param NAME and not the VALUE
                    if (i != 0) returnString += "&";
                    returnString += argument + "=";
                } else { // i is EVEN, so it's the Param VALUE
                    returnString += URLEncoder.encode(argument, encodingCharSet);
                }
            }
        }catch (Exception e){
            Log.e(TAG, "Exception e: " + e);
        }
        Log.d(TAG, "------------------------------------Encoded string: " + returnString);
        return returnString;
    }

    public boolean isNetworkOnline(Context c) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;
    }

}
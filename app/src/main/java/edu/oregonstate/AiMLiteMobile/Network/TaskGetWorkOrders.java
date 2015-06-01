package edu.oregonstate.AiMLiteMobile.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Helpers.ResponsePair;

/*
 * Created by jordan_n on 8/14/2014.
 */

public class TaskGetWorkOrders extends AsyncTask<String, Void, ResponsePair> {
    private static final String TAG = "TaskGetWorkOrders";

    private static NetworkHandler sNetworkHandler;
    private static CurrentUser sCurrentUser;
    private Context mContext;
    private ArrayList<WorkOrder> mWorkOrders;

    private Date retrievedDate;
    private boolean forceRefresh;


    //Callbacks
    private OnTaskCompleted listener;
    public interface OnTaskCompleted{
        void onTaskSuccess();
        void onNetworkFail();
        void onAuthenticateFail();
    }

    public TaskGetWorkOrders(OnTaskCompleted l, CurrentUser currentUser, Context context, boolean forceRefresh) {
        listener = l;
        sCurrentUser = currentUser;
        this.mContext = context;
        this.forceRefresh = forceRefresh;
        this.mWorkOrders = new ArrayList<WorkOrder>();
        retrievedDate = new Date();
        sNetworkHandler = NetworkHandler.get(mContext);
    }

    @Override
    protected void onPostExecute(final ResponsePair responsePair) {

        switch (responsePair.getStatus()) {
            case SUCCESS:
                Log.i(TAG, "Task Success");
                listener.onTaskSuccess();
                break;
            case AUTH_FAIL:
                Log.i(TAG, "Auth Fail");
                listener.onAuthenticateFail();
                break;
            case NET_FAIL:
                Log.i(TAG, "Net Fail");
                  listener.onNetworkFail();
                break;
            case JSON_FAIL:
                Log.i(TAG, "JSON Fail");
                listener.onNetworkFail();//TODO: custom json failure handler
                break;
            case NO_DATA:
                Log.i(TAG, "No data");
                listener.onNetworkFail();//TODO: no network no data, should tell user to get network access
                break;
            default:
                break;
        }
    }

    protected ResponsePair doInBackground(final String... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        JSONArray json;

        //Network available
        if (sNetworkHandler.isNetworkOnline(mContext)) {
            sCurrentUser.setLastUpdated(new Date(System.currentTimeMillis()).toString());
            boolean needRefresh = isRefreshNeeded();
            Log.i(TAG, "forceRefresh: " + forceRefresh + ", normalRefresh: "+ needRefresh);
            if (needRefresh || forceRefresh) {
                try {
                    responsePair = sNetworkHandler.downloadUrl(sCurrentUser.getURLGetAll(), true, responsePair);
                } catch (IOException e){
                    Log.e(TAG, e.toString());
                }
                if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                    Log.e(TAG, "Connection error!");
                    return responsePair;
                }
                json = responsePair.getJarray();
            }
            else{
                //Have network but don't need refresh. Assume data exists locally. Is this even needed? Maybe from login?
                try {
                    Log.i(TAG, "Retrieving stored data");
                    json = new JSONArray(sCurrentUser.getPrefs().getString("jsondata", "[]"));
                    responsePair.setStatus(ResponsePair.Status.SUCCESS);
                }
                catch (JSONException e){
                    Log.e(TAG, "Failed to get stored data");
                    responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                    return responsePair;
                }
            }
        }

        //No network, try getting the stored data
        else if (sCurrentUser.getPrefs().contains("jsondata")) {

            try {
                Log.i(TAG, "Trying to get stored data");
                json = new JSONArray(sCurrentUser.getPrefs().getString("jsondata", "[]"));
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (JSONException e){
                Log.e(TAG, "Failed to get stored data");
                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                return responsePair;
            }
        }

        //No network, no stored data. Bye!
        else {
            Log.e(TAG, "No network, no stored data");
            responsePair.setStatus(ResponsePair.Status.NO_DATA);
            return responsePair;
        }

        /*
        Only reached here if:
            1) there is network and refresh is needed
            2) there is network and refresh is not needed, and stored data exists
            3) no network, and stored data exists

        Flow of first time run to get data:
            There is network, refresh needed, fills data, saves local jsondata in prefs
        */

        if (json == null) {
            Log.e(TAG, "Json is null");
            //return false;
        }


        for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject mJsonObj = json.getJSONObject(i);
                    WorkOrder wo = new WorkOrder();
                    wo.setBeginDate(mJsonObj.getString("beg_dt"));
                    wo.setEndDate(mJsonObj.getString("end_dt"));
                    wo.setCraftCode(mJsonObj.getString("craft_code"));
                    wo.setShop(mJsonObj.getString("shop"));
                    wo.setBuilding(mJsonObj.getString("building"));
                    wo.setDescription(mJsonObj.getString("description"));
                    wo.setCategory(mJsonObj.getString("category"));
                    wo.setPriority(mJsonObj.getString("pri_code"));
                    wo.setDateElements(mJsonObj.getString("ent_date"));
                    wo.setStatus(mJsonObj.getString("status_code"));
                    wo.setContactName(mJsonObj.getString("contact"));
                    wo.setDepartment(mJsonObj.getString("department"));
                    wo.setProposalPhase(String.format("%s-%s", mJsonObj.getString("proposal"), mJsonObj.getString("sort_code")));

                    Random rand = new Random();
                    if(rand.nextInt(100)%2 == 0){
                        wo.setSection("Daily");
                    }else if(rand.nextInt(100)%2 == 0){
                        wo.setSection("Backlog");
                    }else{
                        wo.setSection("Admin");
                    }

/*                    // %%%% TEST BLOCK - marks first 5 as daily
                    if (i < 5){
                        wo.setSection("Daily");
                    } else {
                        wo.setSection("Backlog");
                    }
                    // %%%%*/

                    mWorkOrders.add(wo);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                    return responsePair;
                }
        }

        //Save the raw json array for offline use
        String jsonStr = json.toString();
        sCurrentUser.getPrefsEditor().putString("jsondata", jsonStr);

        //Save time in last updated
        Log.i(TAG, "Saving new last_updated: " + retrievedDate);
        sCurrentUser.getPrefsEditor().putLong("last_updated", retrievedDate.getTime());
        sCurrentUser.getPrefsEditor().apply();


        //Save the work orders array into current user
        sCurrentUser.setWorkOrders(mWorkOrders);

        return responsePair;
    }


    //Gets the last updated time from url, compares with stored time.
    private boolean isRefreshNeeded(){
        String lastUpdated;
        Date storedDate;

        //Get last_updated from stored prefs
        Long storedTime = sCurrentUser.getPrefs().getLong("last_updated", 0L);

        if (storedTime == 0L){
            //No stored time, need refresh
            return true;
        }
        storedDate = new Date(storedTime); Log.i(TAG, "Stored last_updated: " + storedDate);

        //Retrieve last_updated from updateUrl for user

        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        try {
            responsePair = sNetworkHandler.downloadUrl(sCurrentUser.getURLGetLastUpdated(), false, responsePair);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        if (responsePair.getStatus() == ResponsePair.Status.SUCCESS){
            lastUpdated = responsePair.getReturnedString();
            retrievedDate = convertToDate(lastUpdated);
            Log.i(TAG, "Online last_updated: "+retrievedDate);
        }else{
            Log.e(TAG, "Connection error!");
            return true;
        }

        if (retrievedDate.after(storedDate)){
            //Retrieved time is new than stored time, need refresh
            Log.i(TAG, "Online date NEWER than stored date");
            return true;
        } else{
            //Retrieved time is not newer than stored time, no refresh
            Log.i(TAG, "Online date NOT newer than stored date");
            return false;
        }
    }


    private Date convertToDate(String dateString){
        dateString = dateString.replace("\"", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
        Date convertedDate = new Date();
        try{
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e){
            e.printStackTrace();
            Log.e(TAG, "Unable to parse dateString: " + dateString);
        }
        return convertedDate;
    }
}






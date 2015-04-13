package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * Created by jordan_n on 8/14/2014.
 */

public class GetWorkOrdersTask extends AsyncTask<String, Void, ResponsePair> {

    private static final String TAG = "GetWorkOrdersTask";
    private OnTaskCompleted listener;
    private String url;
    private String updateUrl;
    private boolean forceRefresh;
    private ArrayList<WorkOrder> mWorkOrders;
    private JSONArray json;
    private CurrentUser sCurrentUser;
    private Context mContext;
    private Date retrievedDate;


    public interface OnTaskCompleted{
        void onTaskSuccess();
        void onNetworkFail();
        void onAuthenticateFail();
    }

    public GetWorkOrdersTask(OnTaskCompleted listener, CurrentUser currentUser, Context context, boolean forceRefresh) {
        this.listener = listener;
        this.sCurrentUser = currentUser;
        this.mContext = context;
        this.forceRefresh = forceRefresh;

        this.url = currentUser.getURLGetAll();
        this.updateUrl = currentUser.getURLGetLastUpdated();
        this.mWorkOrders = new ArrayList<WorkOrder>();

        retrievedDate = new Date();
    }

    @Override
    protected void onPostExecute(final ResponsePair responsePair) {

        Log.d(TAG, "onPostExecute - Start responsePair:");
        switch(responsePair.getStatus()){
            case SUCCESS:
                Log.d(TAG, "Success");
                listener.onTaskSuccess();
                break;
            case AUTH_FAIL:
                Log.d(TAG, "Auth Fail");
                listener.onAuthenticateFail();
                break;
            case NET_FAIL:
                Log.d(TAG, "Net Fail");
                listener.onNetworkFail();
                break;
            case JSON_FAIL:
                Log.d(TAG, "JSON Fail");
                listener.onNetworkFail();//TODO: custom json failure handler
                break;
            case NO_DATA:
                Log.d(TAG, "No data");
                listener.onNetworkFail();//TODO: no network no data, should tell user to get network access
                break;
            default:
                break;
        }
        Log.d(TAG, "onPostExecute - End responsePair");
    }

    protected ResponsePair doInBackground(final String... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        //Network available
        if (isNetworkOnline()) {

            Log.d(TAG, "Network Available");
            JSONParser jParser = new JSONParser();

            Log.d(TAG, "Calling isRefreshNeeded");
            boolean needRefresh = isRefreshNeeded();
            Log.d(TAG, "needRefresh : " + needRefresh);

            if (needRefresh || forceRefresh) {

                responsePair = jParser.getJSONFromUrl(url, true);

                if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                    return responsePair;
                }

                json = responsePair.getJarray();
            }
            else{
                //Have network but don't need refresh. Assume data exists locally. Is this even needed? Maybe from login?
                try {
                    Log.d(TAG, "Trying to get stored data");
                    json = new JSONArray(sCurrentUser.getPrefs().getString("jsondata", "[]"));
                    responsePair.setStatus(ResponsePair.Status.SUCCESS);
                }
                catch (JSONException e){
                    Log.d(TAG, "Failed to get stored data");
                    responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                    return responsePair;
                }
            }

        }

        //No network, try getting the stored data
        else if (sCurrentUser.getPrefs().contains("jsondata")) {

            try {
                Log.d(TAG, "Trying to get stored data");
                json = new JSONArray(sCurrentUser.getPrefs().getString("jsondata", "[]"));
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
            }
            catch (JSONException e){
                Log.d(TAG, "Failed to get stored data");
                responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                return responsePair;
            }
        }

        //No network, no stored data. Bye!
        else {
            Log.d(TAG, "No network, no stored data");
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
            Log.d(TAG, "Json is null");
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
                    wo.setProposalPhase(String.format("%s-%s", mJsonObj.getString("proposal"), mJsonObj.getString("sort_code")));

                    // %%%% DEBUG
                    if (i < 5){
                        wo.setSection("Daily");
                    } else {
                        wo.setSection("Backlog");
                    }
                    // %%%%

                    mWorkOrders.add(wo);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    responsePair.setStatus(ResponsePair.Status.JSON_FAIL);
                    return responsePair;
                }
        }


        Log.d(TAG, "username is: "+sCurrentUser.getUsername());

        //Save the raw json array for offline use

        String jsonStr = json.toString();
        sCurrentUser.getPrefsEditor().putString("jsondata", jsonStr);

        //Save time in last updated
        Log.d(TAG, "Saving time to last_updated: "+retrievedDate.getTime()+" = "+ retrievedDate);
        sCurrentUser.getPrefsEditor().putLong("last_updated", retrievedDate.getTime());
        //Log.d(TAG, "Saving time to last_updated: "+System.currentTimeMillis()+" = "+ new Date(System.currentTimeMillis()));
        //sCurrentUser.getPrefsEditor().putLong("last_updated", System.currentTimeMillis());

        sCurrentUser.getPrefsEditor().apply();

        //Save the work orders array into current user
        sCurrentUser.setWorkOrders(mWorkOrders);

        return responsePair;
    }

    //Gets the last updated time from url, compares with stored time.
    private boolean isRefreshNeeded(){
        String lastUpdated = "";

        Date storedDate;

        //Get last_updated from stored prefs
        Long storedTime = sCurrentUser.getPrefs().getLong("last_updated", 0L);

        if (storedTime == 0L){
            //No stored time, need refresh
            Log.d(TAG, "storedTime = 0");
            return true;
        }
        storedDate = new Date(storedTime);
        Log.d(TAG, "isRefreshNeeded() Stored Date: "+storedTime+" = "+storedDate);

        //Retrieve last_updated from updateUrl for user
        JSONParser jParser = new JSONParser();
        ResponsePair responsePair = jParser.getJSONFromUrl(sCurrentUser.getURLGetLastUpdated(), false);

        if (responsePair.getStatus() == ResponsePair.Status.SUCCESS){
            lastUpdated = responsePair.getReturnedString();
            retrievedDate = convertToDate(lastUpdated);
            Log.d(TAG, "Online last_updated retrieved is: "+retrievedDate.getTime()+" = "+retrievedDate);
        }else{
            //TODO 3/12/2015 - below probably indicates http error, maybe just display error and don't try refresh
            //No retrieved time, need refresh
            return true;
        }

        if (retrievedDate.after(storedDate)){
            //Retrieved time is new than stored time, need refresh
            Log.d(TAG, "retrievedDate newer than storedDate");
            return true;
        } else{
            //Retrieved time is not newer than stored time, no refresh
            Log.d(TAG, "retrievedDate NOT newer than storedDate");
            return false;
        }


    }


    public boolean isNetworkOnline() {
        boolean status=false;
        try{
            ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                status= true;
            }else {
                netInfo = cm.getNetworkInfo(1);
                if(netInfo!=null && netInfo.getState()==NetworkInfo.State.CONNECTED)
                    status= true;
            }
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return status;

    }

    private Date convertToDate(String dateString){
        dateString = dateString.replace("\"", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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






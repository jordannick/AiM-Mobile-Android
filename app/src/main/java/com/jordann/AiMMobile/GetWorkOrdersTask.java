package com.jordann.AiMMobile;

import android.content.Context;
import android.content.SharedPreferences;
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
    private ArrayList<WorkOrder> mWorkOrders;
    private JSONArray json;
    private CurrentUser sCurrentUser;
    private Context mContext;

    public interface OnTaskCompleted{
        void onTaskSuccess();
        void onNetworkFail();
        void onAuthenticateFail();

    }


    public GetWorkOrdersTask(OnTaskCompleted listener,/* String url, String updateUrl,*/ CurrentUser currentUser, Context context) {
        this.listener = listener;
       // this.url = url;
       // this.updateUrl = updateUrl;
        this.url = currentUser.getURLGetAll();
        this.updateUrl = currentUser.getURLGetLastUpdated();

        this.sCurrentUser = currentUser;
        this.mContext = context;

        mWorkOrders = new ArrayList<WorkOrder>();
    }


    @Override
    protected void onPostExecute(final ResponsePair responsePair) {

        Log.d(TAG, "onPostExecute - BEGIN SWITCH --------------");

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

        Log.d(TAG, "onPostExecute - END SWITCH --------------");

    }


    protected ResponsePair doInBackground(final String... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        //Network available
        if (isNetworkOnline()) {
            Log.d(TAG, "Network Available");
            JSONParser jParser = new JSONParser();

            Log.d(TAG, "Calling isRefreshNeeded");
            boolean needRefresh = isRefreshNeeded(updateUrl);
            Log.d(TAG, "needRefresh : " + needRefresh);

           // if(needRefresh){ //Pull from online
                responsePair = jParser.getJSONFromUrl(url, true);
                if (responsePair.getStatus() != ResponsePair.Status.SUCCESS){
                    return responsePair;
                }

                //Assume success at this point
                json = responsePair.getJarray();
           // }

          //  else{ //Pull from file

          //  }
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

        sCurrentUser.getPrefsEditor().apply();

        //Save the work orders array into current user
        sCurrentUser.setWorkOrders(mWorkOrders);

        return responsePair;
    }

    private boolean isRefreshNeeded(String updateUrl){
        //Check 'last_updated' for user
        Long stored_lastUpdated = sCurrentUser.getPrefs().getLong("last_updated", 0);
        Date now = new Date(System.currentTimeMillis()); //TESTING
        stored_lastUpdated = now.getTime();
        Date stored_lastUpdatedDate = new Date(stored_lastUpdated);

        if(stored_lastUpdated != 0){ //If date is stored
            //Convert date string to date obj
            JSONParser jParser = new JSONParser();
            ResponsePair responsePair = jParser.getJSONFromUrl(updateUrl, false);
            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS){
                //Http fail
                Log.d(TAG, "isRefreshNeeded Fail");
                return false;
            }else{
                //Http success
                Log.d(TAG, "isRefreshNeeded Success");
                String lastUpdated = responsePair.getReturnedString();
                Log.d(TAG, "ds: "+lastUpdated);
                lastUpdated = lastUpdated.replace("\"", "");
                Log.d(TAG, "ds2: "+lastUpdated);

                Date lastUpdatedDate = convertToDate(lastUpdated);
                if(stored_lastUpdatedDate.before(lastUpdatedDate)){
                    //Needs Refresh
                    return true;
                }else{
                    return false;
                }
            }
        }else{ //No date stored. Refresh workOrders
            return true;
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






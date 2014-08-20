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

import java.util.ArrayList;

/*
 * Created by jordan_n on 8/14/2014.
 */

public class GetWorkOrdersTask extends AsyncTask<String, Void, ResponsePair> {

    private static final String TAG = "GetWorkOrdersTask";
    private OnTaskCompleted listener;
    private String url;
    private ArrayList<WorkOrder> mWorkOrders;
    private JSONArray json;
    private CurrentUser sCurrentUser;
    private Context mContext;

    public interface OnTaskCompleted{
        void onTaskSuccess();
        void onNetworkFail();
        void onAuthenticateFail();

    }


    public GetWorkOrdersTask(OnTaskCompleted listener, String url, CurrentUser currentUser, Context context) {
        this.listener = listener;
        this.url = url;

        this.sCurrentUser = currentUser;
        this.mContext = context;

        mWorkOrders = new ArrayList<WorkOrder>();
    }


    @Override
    protected void onPostExecute(final ResponsePair responsePair) {

        Log.d(TAG, "BEGIN SWITCH --------------");

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

        Log.d(TAG, "END SWITCH --------------");

    }


    protected ResponsePair doInBackground(final String... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        //Network available
        if (isNetworkOnline()) {
            Log.d(TAG, "Network Available");
            JSONParser jParser = new JSONParser();

            responsePair = jParser.getJSONFromUrl(url);

            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS){
                return responsePair;
            }

            //Assume success at this point
            json = responsePair.getJarray();

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
        if (json == null) {
            Log.d(TAG, "Json is null");
            //return false;
        }
        */

        for (int i = 0; i < json.length(); i++) {
                try {
                    JSONObject mJsonObj = json.getJSONObject(i);
                    WorkOrder wo = new WorkOrder();
                    wo.setBeginDate(mJsonObj.getString("beg_dt"));
                    wo.setEndDate(mJsonObj.getString("end_dt"));
                    wo.setCraftCode(mJsonObj.getString("craft_code"));
                    wo.setBuilding(mJsonObj.getString("building"));
                    wo.setDescription(mJsonObj.getString("description"));
                    wo.setCategory(mJsonObj.getString("category"));
                    wo.setPriority(mJsonObj.getString("pri_code"));
                    wo.setDateElements(mJsonObj.getString("ent_date"));
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




}






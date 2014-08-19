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

public class GetWorkOrdersTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "GetWorkOrdersTask";
    private OnTaskCompleted listener;
    private String url;
    private ArrayList<WorkOrder> mWorkOrders;

    private JSONArray json;


    private CurrentUser sCurrentUser;
    private Context mContext;


    public interface OnTaskCompleted{
        // void onTaskCompleted(WorkOrderAdapter adapter);
        void onTaskSuccess();
        void onTaskFail();

    }


    public GetWorkOrdersTask(OnTaskCompleted listener, String url, CurrentUser currentUser, Context context) {
        this.listener = listener;
        this.url = url;

        this.sCurrentUser = currentUser;
        this.mContext = context;

        mWorkOrders = new ArrayList<WorkOrder>();
    }


    //TODO: change success arg to handle different kinds of failures
    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            Log.d(TAG, "post success");
            listener.onTaskSuccess();
        }
        else {

            Log.d(TAG, "post fail");
            listener.onTaskFail();
        }
    }


    protected Boolean doInBackground(final String... args) {

        //Network available
        if (isNetworkOnline()) {
            Log.d(TAG, "Network Available");
            JSONParser jParser = new JSONParser();
            json = ((ResponsePair)jParser.getJSONFromUrl(url)).getJarray();
        }

        //No network, try getting the stored data
        else if (sCurrentUser.getPrefs().contains("jsondata")) {

            try {
                Log.d(TAG, "Trying to get stored data");
                json = new JSONArray(sCurrentUser.getPrefs().getString("jsondata", "[]"));
            }
            catch (JSONException e){
                Log.d(TAG, "Failed to get stored data");
                return false;
            }
        }

        //No network, no stored data. Bye!
        else {
            Log.d(TAG, "No network, no stored data");
            return false;
        }

        if (json == null) {
            Log.d(TAG, "Json is null");
            return false;
        }

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
                    return false;
                }
        }


        Log.d(TAG, "username is: "+sCurrentUser.getUsername());

        //Save the raw json array for offline use

        //SharedPreferences prefs = sCurrentUser.getPrefs();
        //SharedPreferences.Editor editor = prefs.edit();



        String jsonStr = json.toString();
        //editor.putString("jsondata", jsonStr);
        sCurrentUser.getPrefsEditor().putString("jsondata", jsonStr);


        //editor.commit();
        sCurrentUser.getPrefsEditor().apply();


        //sCurrentUser.setPrefs(prefs);

        //Save the work orders array into current user
        sCurrentUser.setWorkOrders(mWorkOrders);

        return true;
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






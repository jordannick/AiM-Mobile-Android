package com.jordann.practice1;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jordan_n on 8/14/2014.
 */
public class GetWorkOrdersTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "GetWorkOrdersTask";
    private OnTaskCompleted listener;
    private String url;
    private ArrayList<WorkOrder> mWorkOrders;


    public interface OnTaskCompleted{
        // void onTaskCompleted(WorkOrderAdapter adapter);
        void onTaskSuccess(ArrayList<WorkOrder> workOrders);
        void onTaskFail();

    }


    public GetWorkOrdersTask(OnTaskCompleted listener, String url) {
        this.listener = listener;
        this.url = url;
        mWorkOrders = new ArrayList<WorkOrder>();
    }


    @Override
    protected void onPostExecute(final Boolean success) {

        if (success) {
            Log.d(TAG, "post success");
            listener.onTaskSuccess(mWorkOrders);
        }
        else {
            Log.d(TAG, "post fail");
            listener.onTaskFail();
        }
    }


    protected Boolean doInBackground(final String... args) {
        JSONParser jParser = new JSONParser();
        // get JSON data from URL

        JSONArray json = jParser.getJSONFromUrl(url);

        if (json == null)
            return false;

        for (int i = 0; i < json.length(); i++) {
            try {
                JSONObject c = json.getJSONObject(i);
                WorkOrder wo = new WorkOrder();
                wo.setBeginDate(c.getString("beg_dt"));
                wo.setEndDate(c.getString("end_dt"));
                wo.setCraftCode(c.getString("craft_code"));
                wo.setBuilding(c.getString("building"));
                wo.setDescription(c.getString("description"));
                wo.setCategory(c.getString("category"));
                wo.setPriority(c.getString("pri_code"));
                wo.setDateElements(c.getString("ent_date"));
                wo.setProposalPhase(String.format("%s-%s", c.getString("proposal"), c.getString("sort_code")));
                //wo.setProposal(c.getString("proposal"));
                mWorkOrders.add(wo);
            }
            catch (JSONException e) {
                Log.d(TAG,"before stacktrace");
                e.printStackTrace();
                Log.d(TAG,"after stacktrace");
                return false;
            }
        }

        return true;
    }


}






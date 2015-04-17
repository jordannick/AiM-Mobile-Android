package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;


public class PostWorkOrdersTask extends AsyncTask<String, Void, ResponsePair> {

    private static final String TAG = "PostWorkOrdersTask";
    private OnTaskCompleted listener;
    private List<NameValuePair> nameValuePairs;
    private Context mContext;

    public interface OnTaskCompleted{

    }

    public PostWorkOrdersTask(List<NameValuePair> nameValuePairs, String url, Context context) {
        //this.listener = listener;
        this.nameValuePairs = nameValuePairs;
        this.mContext = context;
    }


    @Override
    protected void onPostExecute(final ResponsePair responsePair) {

        switch(responsePair.getStatus()){
            case SUCCESS:
                Log.i(TAG, "Task Success");
                //listener.onTaskSuccess();
                break;
            case AUTH_FAIL:
                Log.i(TAG, "Auth Fail");
                //listener.onAuthenticateFail();
                break;
            case NET_FAIL:
                Log.i(TAG, "Net Fail");
              //  listener.onNetworkFail();
                break;
            case JSON_FAIL:
                Log.i(TAG, "JSON Fail");
              //  listener.onNetworkFail();//TODO: custom json failure handler
                break;
            case NO_DATA:
                Log.i(TAG, "No data");
              //  listener.onNetworkFail();//TODO: no network no data, should tell user to get network access
                break;
            default:
                break;
        }

    }

    protected ResponsePair doInBackground(final String... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);

        //Network available
        if (isNetworkOnline()) {
            Log.d(TAG, "Network Available");

            SubmitChange submitChange = new SubmitChange();

            responsePair = submitChange.postToURL("SomeURLGoesHere", nameValuePairs);

            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                return responsePair;
            }

        }


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

/*
URL Schema
/[api version]/[object]/[method]/[param1]/[param2]/[param3] …

    o	addTime($username, $date, $hours, $workOrderPhaseId, [$timeType], $timeStamp)
        ?	Adds hours to the specified work order / phase for the date.
        ?	Uses default time type, unless otherwise specified
        ?	Timestamp should give the time at which this update took place.
        ?	Example: coming soon.

    o	addActionTaken($username, $workOrderPhaseId, $actionTaken, $timeStamp)
        ?	Adds an Action Taken to the specified work order / phase
        ?	Timestamp should give the time at which this update took place.

    o	addNote($username, $workOrderPhaseId, $note, $timeStamp)
        ?	Adds a note to the specified Work Order / Phase
        ?	Timestamp should give the time at which this update took place.

    o	updateStatus($username, $workOrderPhaseId, $newStatus, $timeStamp)
        ?	Updates the status of the specified Work Order / Phase to the new status listed.
        ?	Timestamp should give the time at which this update took place.

    o	updateSection($usename, $workOrderPhaseId, $value, $timeStamp)
        ?	Allowed values include ‘Backlog’ and ‘Daily Assignment’
        ?	Timestamp should give the time at which this update took place.
*/






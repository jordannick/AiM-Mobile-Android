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

        Log.d(TAG, "onPostExecute - Start responsePair:");
        switch(responsePair.getStatus()){
            case SUCCESS:
                Log.d(TAG, "Success");
                //listener.onTaskSuccess();
                break;
            case AUTH_FAIL:
                Log.d(TAG, "Auth Fail");
                //listener.onAuthenticateFail();
                break;
            case NET_FAIL:
                Log.d(TAG, "Net Fail");
              //  listener.onNetworkFail();
                break;
            case JSON_FAIL:
                Log.d(TAG, "JSON Fail");
              //  listener.onNetworkFail();//TODO: custom json failure handler
                break;
            case NO_DATA:
                Log.d(TAG, "No data");
              //  listener.onNetworkFail();//TODO: no network no data, should tell user to get network access
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






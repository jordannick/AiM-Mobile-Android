package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;


public class TaskPostAction extends AsyncTask<String, Void, ResponsePair> {

    private static final String TAG = "TaskPostAction";
    private OnTaskCompleted listener;
    private List<NameValuePair> nameValuePairs;
    private Context mContext;
    private static CurrentUser sCurrentUser;

    public interface OnTaskCompleted{

    }

    public TaskPostAction(List<NameValuePair> nameValuePairs, String url, Context context) {
        //this.listener = listener;
        this.nameValuePairs = nameValuePairs;
        this.mContext = context;
        this.sCurrentUser = CurrentUser.get(context);
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

            responsePair = new NetworkPostAction(mContext).postURL("SomeURLGoesHere", sCurrentUser.getUsername());

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




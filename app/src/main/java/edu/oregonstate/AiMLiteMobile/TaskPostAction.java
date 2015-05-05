package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;

import java.util.List;


public class TaskPostAction extends AsyncTask<Action, Void, ResponsePair> {

    private static final String TAG = "TaskPostAction";

    private static NetworkHandler sNetworkHandler;
    private OnTaskCompleted listener;
    private List<NameValuePair> nameValuePairs;
    private Context mContext;
    private static CurrentUser sCurrentUser;

    public interface OnTaskCompleted{
        void onTaskSuccess();
        void onNetworkFail();
        void onAuthenticateFail();
    }

    public TaskPostAction(OnTaskCompleted listener, String url, Context context) {
        this.listener = listener;
        this.mContext = context;
        this.sCurrentUser = CurrentUser.get(context);
        sNetworkHandler = NetworkHandler.get(context);
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


    protected ResponsePair doInBackground(final Action... args) {
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        //Network available
        if (sNetworkHandler.isNetworkOnline(mContext)) {
            Log.d(TAG, "Network Available");

            //Post newly made Action
            //responsePair = sNetworkHandler.postUnsyncedActions();
            sNetworkHandler.postUnsyncedActions();

            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                return responsePair;
            }
        }
        return responsePair;
    }

}




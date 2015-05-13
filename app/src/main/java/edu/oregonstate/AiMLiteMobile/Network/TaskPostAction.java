package edu.oregonstate.AiMLiteMobile.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Helpers.ResponsePair;


public class TaskPostAction extends AsyncTask<Action, Void, ResponsePair> {
    private static final String TAG = "TaskPostAction";


    private Context mContext;
    private static CurrentUser sCurrentUser;
    private static NetworkHandler sNetworkHandler;

    //Callback
    private OnTaskCompleted listener;
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
        if (sNetworkHandler.isNetworkOnline(mContext)) {
            Log.d(TAG, "Network Available");

            //Post newly made Action
            //TODO: currently doesn't use ResponsePair. Does multiple POSTs for all unsyncedActions
            sNetworkHandler.postUnsyncedActions();

            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                return responsePair;
            }
        }
        return responsePair;
    }
}




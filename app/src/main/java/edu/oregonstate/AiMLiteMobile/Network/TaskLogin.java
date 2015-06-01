package edu.oregonstate.AiMLiteMobile.Network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.Action;
import edu.oregonstate.AiMLiteMobile.Helpers.ResponsePair;

public class TaskLogin extends AsyncTask<Action, Void, ResponsePair> {
    private static final String TAG = "TaskPostAction";

    private static NetworkHandler sNetworkHandler;
    private Context mContext;
    private static CurrentUser sCurrentUser;

    private String url;

    private OnLoginCompleted listener;
    public interface OnLoginCompleted {
        void onLoginSuccess();
        void onLoginFail();
        void onNetworkFail();
    }

    public TaskLogin(OnLoginCompleted listener, String url, Context context) {
        this.listener = listener;
        this.mContext = context;
        this.sCurrentUser = CurrentUser.get(context);
        this.url = url;
        sNetworkHandler = NetworkHandler.get(context);
    }

    @Override
    protected void onPostExecute(final ResponsePair responsePair) {
        switch (responsePair.getStatus()) {
            case SUCCESS:
                Log.i(TAG, "Task Success");
                listener.onLoginSuccess();
                break;
            case AUTH_FAIL:
                Log.i(TAG, "Auth Fail");
                listener.onLoginFail();
                break;
            case NET_FAIL:
                Log.i(TAG, "Net Fail");
                listener.onNetworkFail();
                break;
            case JSON_FAIL:
                Log.i(TAG, "JSON Fail");
                listener.onNetworkFail();
                break;
            case NO_DATA:
                Log.i(TAG, "No data");
                listener.onNetworkFail();
                break;
            default:
                break;
        }
    }

    protected ResponsePair doInBackground(final Action... args) {
        Log.d(TAG, "in doinbackground");
        ResponsePair responsePair = new ResponsePair(ResponsePair.Status.NONE, null);
        try{
            if (sNetworkHandler.isNetworkOnline(mContext)) {
                //Perform request
                URL postLoginUrl = new URL(url);
                //TODO: use actual password
                String encodedParams = sNetworkHandler.buildEncodedString(new String[]{"password", "aaaa"}, "UTF-8");
                responsePair = sNetworkHandler.postToURL(postLoginUrl, encodedParams); //Log.d(TAG, "Login - Response code: " + responsePair.getStatusInt() + " ; Contents: " + responsePair.getReturnedString());

                //Check is response is OK, and string is returned
                if ((responsePair.getStatusInt() == 200) && responsePair.getReturnedString() != null) {
                    responsePair.setStatus(ResponsePair.Status.SUCCESS);
                    //Convert response to JSON and get the value of 'token'
                    String tokenString = new JSONObject(responsePair.getReturnedString()).getString("token"); //Log.d(TAG, "Obtained token: "+ tokenString);
                    sCurrentUser.setToken(tokenString);
                } else {
                    responsePair.setStatus(ResponsePair.Status.FAIL);
                }
            } else {
                responsePair.setStatus(ResponsePair.Status.NET_FAIL);
            }
        }catch (Exception e){
            Log.e(TAG, "Exception! e: " + e);
        }
        return responsePair;
    }


}




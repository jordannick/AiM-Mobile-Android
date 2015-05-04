package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

public class TaskLogin extends AsyncTask<Action, Void, ResponsePair> {

    private static final String TAG = "TaskPostAction";

    private static NetworkHandler sNetworkHandler;
    private OnLoginCompleted listener;

    private Context mContext;
    private static CurrentUser sCurrentUser;
    private String url;


    public interface OnLoginCompleted {
        void onLoginSuccess();

        void onLoginFail();
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
                //listener.onNetworkFail();
                listener.onLoginFail();
                break;
            case JSON_FAIL:
                Log.i(TAG, "JSON Fail");
                listener.onLoginFail();
                break;
            case NO_DATA:
                Log.i(TAG, "No data");
                listener.onLoginFail();
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
            URL postLoginUrl = null;
            String encodingCharSet = "UTF-8";
            String encodedParams = sNetworkHandler.buildEncodedString(new String[]{"password", "aaaa"}, encodingCharSet);
            // String params = "&password=password";
            // String encodedParams = "";

            try {
                //encodedParams =  URLEncoder.encode(params, encodingCharSet);
                postLoginUrl = new URL(url);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            responsePair = sNetworkHandler.postToURL(postLoginUrl, encodedParams);
            Log.d(TAG, "Login - Response code: " + responsePair.getStatusInt() + " ; Contents: " + responsePair.getReturnedString());

/*
            if (responsePair.getStatus() != ResponsePair.Status.SUCCESS) {
                return responsePair;
            }*/
            if ((responsePair.getStatusInt() == 200) && responsePair.getReturnedString() != null) {
                responsePair.setStatus(ResponsePair.Status.SUCCESS);
                try {
                    JSONObject tokenObject = new JSONObject(responsePair.getReturnedString());
                    String tokenString = tokenObject.getString("token");
                    Log.d(TAG, "Obtained token: "+tokenString);
                    sCurrentUser.setToken(tokenString);
                } catch (Exception e){
                    Log.e(TAG, "Failed to generate JSON token object");
                }
            } else {
                responsePair.setStatus(ResponsePair.Status.FAIL);
            }

        } else {
            responsePair.setStatus(ResponsePair.Status.NO_DATA);
        }
        return responsePair;
    }


}




package edu.oregonstate.AiMLiteMobile;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sellersk on 7/7/2015.
 */
public class RetrofitTest {
    private static final String TAG = "RetrofitTest";

    private CurrentUser currentUser;

    public RetrofitTest(Context c) {
        currentUser = CurrentUser.get(c);
    }

    public void performTest(int numRequests){
        for (int i = 0; i < numRequests; i++) {
            final int j = i;
            ApiManager.getService().getWorkOrders(currentUser.getUsername(), currentUser.getToken(), new Callback<ResponseWorkOrders>() {
                @Override
                public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                    Log.d(TAG, "Success on iteration: " + j);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.d(TAG, "Fail on iteration: " + j);
                }
            });
        }
    }

    private class Clock {



        public Clock() {
        }
    }


    /*       ApiManager.getService().getLastUpdated(currentUser.getUsername(), currentUser.getToken(), new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "requestLastUpdated SUCCESS");
                requestWorkOrders();
            }*/
}

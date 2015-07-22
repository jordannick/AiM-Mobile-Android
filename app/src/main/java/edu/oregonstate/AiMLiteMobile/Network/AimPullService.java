package edu.oregonstate.AiMLiteMobile.Network;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by sellersk on 7/20/2015.
 */
public class AimPullService extends IntentService {
    public static final String TAG = "AimPullService";

    public AimPullService() {
        super("AimPullService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent for AimService. intent : " + intent);

    }
}

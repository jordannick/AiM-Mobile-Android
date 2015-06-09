package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Fragment;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import edu.oregonstate.AiMLiteMobile.AimApi;
import edu.oregonstate.AiMLiteMobile.AndyApiObj;
import edu.oregonstate.AiMLiteMobile.CustomConverter;
import edu.oregonstate.AiMLiteMobile.Fragments.LoginFragment;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Network.NetworkHandler;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import retrofit.mime.TypedString;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginActivity extends SingleFragmentActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }


    public void retroTest(View v){
        Log.d(TAG, "Woo button clicked.");
        CurrentUser.get(this).buildLoginUrl("crosst");
        String baseUrl = CurrentUser.get(this).getURLLogin();
        Log.d(TAG, "Using baseUrl: " + baseUrl);

        baseUrl = "http://api-test.facilities.oregonstate.edu/1.0";
        CustomConverter customConverter = new CustomConverter();

        //baseUrl = "http://requestb.in";
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new CustomConverter())
                .build();

        AimApi userApi = restAdapter.create(AimApi.class);

        userApi.login("abc", new retrofit.Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Log.d(TAG, "Success! String " + s + ", Response " + response.getBody());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Error " + error);
            }
        });

    }
}

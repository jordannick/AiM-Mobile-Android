package com.jordann.AiMMobile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginFragment extends Fragment implements GetWorkOrdersTask.OnTaskCompleted {

    private LoginFragment myFragment = this;

    private static final String TAG = "LoginFragment";

    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private CheckBox mAutoLoginCheckbox;
    private ProgressBar mLoadCircle;

    private static CurrentUser sCurrentUser;

    private String mUrl = "";
    private String mBaseUrl = "http://apps-webdev.campusops.oregonstate.edu/robechar/portal/aim/api";
    private String mAPIVersion = "1.0.0";
    private String mMethod = "getWorkOrders";
    private String mUsername;
    private String mPassword;
   // private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create an instance of the user class
        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());


        //First time running
        if (!sCurrentUser.getPrefs().contains("autologin")){
            Log.d(TAG, "First time running");
            //prefs.edit().putBoolean("autologin", false);
            sCurrentUser.getPrefsEditor().putBoolean("autologin", false);
            sCurrentUser.getPrefsEditor().apply();

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_view, parent, false);
        getActivity().setTitle(R.string.login_title);

        mUsernameField = (EditText)v.findViewById(R.id.login_username);
        mPasswordField = (EditText)v.findViewById(R.id.login_password);
        mAutoLoginCheckbox = (CheckBox)v.findViewById(R.id.auto_login);
        mLoginButton = (Button)v.findViewById(R.id.login_button);
        mLoadCircle = (ProgressBar)v.findViewById(R.id.load_circle);
        mLoadCircle.setVisibility(View.INVISIBLE);

        //Check if autologin should occur
        if (sCurrentUser.getPrefs().getBoolean("autologin", false)){

            mUsername = sCurrentUser.getPrefs().getString("username", "");
            mPassword = sCurrentUser.getPrefs().getString("password", "");

            mUrl = mBaseUrl+'/'+mAPIVersion+'/'+mMethod+'/'+mUsername;//+'/'+mPassword

            //Bad urls for testing
           // mUrl = mBaseUrl+'/'+mAPIVersion+"/nonexistentmethod/"+mUsername;
           // mUrl = mBaseUrl+'/'+mAPIVersion+'/'+mMethod+'/'+"nonexistentusername";
           // mUrl = mBaseUrl+'/'+"nonexistentAPIVersion"+'/'+mMethod+'/'+mUsername; //this works for some reason...
             //mUrl = "http://apps-webdev.campusops.oregonstate.edu/NOPE/portal/aim/api/stuff/getWorkOrders/CROSST";

            executeTask();
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for empty fields
                if (mUsernameField.getText().toString().matches(""))
                    mUsernameField.setError("Username required");
                if (mPasswordField.getText().toString().matches(""))
                    mPasswordField.setError("Password required");

                //If both fields are not empty
                if (!mUsernameField.getText().toString().matches("") && !mPasswordField.getText().toString().matches("")){

                    // mUsername = mUsernameField.getText().toString();
                    mUsername = "CLARKEM";

                    //Check if this user has been previously stored
                    if (sCurrentUser.getPrefs().contains(mUsername)){
                        mPassword = sCurrentUser.getPrefs().getString(mUsername, null);

                    }

                    //Construct the URL
                    mUrl = mBaseUrl+'/'+mAPIVersion+'/'+mMethod+'/'+mUsername;
                    sCurrentUser.setUsername(mUsername);

                    //Save user info for future autologins
                    if (mAutoLoginCheckbox.isChecked()){
                        sCurrentUser.getPrefsEditor().putString("username", mUsername);
                        sCurrentUser.getPrefsEditor().putString("password", mPassword);
                        sCurrentUser.getPrefsEditor().putBoolean("autologin", true);
                        sCurrentUser.getPrefsEditor().apply();
                    }

                    executeTask();

                }
            }
        });
        return v;
    }


    public void executeTask(){

        //Disable all fields for now
        mUsernameField.setEnabled(false);
        mPasswordField.setEnabled(false);
        mAutoLoginCheckbox.setEnabled(false);
        mLoginButton.setEnabled(false);

        mLoadCircle.setVisibility(View.VISIBLE);
        showAutoLoginToast();
        //Start the asynchronous parsing controller
        GetWorkOrdersTask task = new GetWorkOrdersTask(myFragment, mUrl, sCurrentUser, getActivity());
        task.execute();
    }


    public void onTaskSuccess() {

        //Re-enable, in case we come back to login screen
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);
        mLoadCircle.setVisibility(View.INVISIBLE);

        sCurrentUser.setUsername(mUsername);

        //Move on to the next activity
        Intent i = new Intent(getActivity(), WorkOrderListActivity.class);
        startActivity(i);

        //Leave the login screen activity running
    }


    //TODO: handle network failures and authentication failures separately
    /*
    public void onTaskFail(){
        //mUsernameField.setError("Password and username didn't match");

        //Re-enable all fields so user can try again
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);

        mLoadCircle.setVisibility(View.INVISIBLE);
    }*/

    public void onNetworkFail(){
        //Re-enable all fields so user can try again
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);

        mLoadCircle.setVisibility(View.INVISIBLE);


        //Display error about network fail

    }

    public void onAuthenticateFail(){


        //Re-enable all fields so user can try again
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);

        mLoadCircle.setVisibility(View.INVISIBLE);


        //mUsernameField.setError("Password and username didn't match");
    }


    public void showAutoLoginToast(){
        Context context = getActivity().getApplicationContext();
        CharSequence text = "Logging in as: "+mUsername;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }




}



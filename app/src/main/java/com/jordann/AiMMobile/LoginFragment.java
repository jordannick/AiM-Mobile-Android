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

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginFragment extends Fragment implements GetWorkOrdersTask.OnTaskCompleted {

    //private GetWorkOrdersTask.OnTaskCompleted listener;

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
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create an instance of the user class
        sCurrentUser = CurrentUser.get(/*getActivity()*/);


        prefs = getActivity().getSharedPreferences("com.jordann.AiMMobile", Context.MODE_PRIVATE);


        /* SharedPreferences contents:
        autologin: bool
        username: string
        password: string
        jsondata: string
         */

        //First time running
        if (!prefs.contains("autologin")){
            prefs.edit().putBoolean("autologin", false);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_view, parent, false);
        getActivity().setTitle(R.string.login_title);

        mUsernameField = (EditText)v.findViewById(R.id.login_username);
        mPasswordField = (EditText)v.findViewById(R.id.login_password);
        mLoginButton = (Button)v.findViewById(R.id.login_button);
        mLoadCircle = (ProgressBar)v.findViewById(R.id.load_circle);
        mLoadCircle.setVisibility(View.INVISIBLE);



        sCurrentUser.setPrefs(prefs);

        //Check if autologin should occur
        if (prefs.getBoolean("autologin", false)){
            Log.d(TAG, "got to getboolean");
            mLoginButton.setEnabled(false);
            mUsername = prefs.getString("username", null);
            mUrl = mBaseUrl+'/'+mAPIVersion+'/'+mMethod+'/'+mUsername;

            //For visual purposes, show what we're logging in as
            mUsernameField.setText(prefs.getString("username", null));
            mPasswordField.setText(prefs.getString("password", null));
            //mPasswordField.
            //mUsernameField.setEnabled(false);
           // mPasswordField.setEnabled(false);




            executeTask();
        }



       // else if (mAutoLoginCheckbox.isChecked()){
            //add username+pw to prefs
        //}


        //TODO: check for empty/invalid chars
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mUsernameField.setEnabled(false);
                //mPasswordField.setEnabled(false);
                //mLoginButton.setEnabled(false);


                //mLoadCircle.setVisibility(View.VISIBLE);

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
                    if (prefs.contains(mUsername)){
                        mPassword = prefs.getString(mUsername, null);

                    }

                    //Construct the URL
                    mUrl = mBaseUrl+'/'+mAPIVersion+'/'+mMethod+'/'+mUsername;
                    sCurrentUser.setUsername(mUsername);

                    executeTask();


                }

            }
        });


        return v;
    }


    public void executeTask(){
        //Start the asynchronous parsing controller
        GetWorkOrdersTask task = new GetWorkOrdersTask(myFragment, mUrl, sCurrentUser, getActivity());
        task.execute();
    }


    public void onTaskSuccess() {



        Intent i = new Intent(getActivity(), WorkOrderListActivity.class);
        Log.d(TAG, "made the intent");
        startActivity(i);
        getActivity().finish();

    }


    public void onTaskFail(){
        Log.d(TAG, "failed the task");
        //mUsernameField.setError("Password and username didn't match");

        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mLoginButton.setEnabled(true);
        mLoadCircle.setVisibility(View.INVISIBLE);
    }







}



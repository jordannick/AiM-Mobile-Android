package edu.oregonstate.AiMLiteMobile;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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

    private String mUsername;
    private String mPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create an instance of the user class (singleton)
        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());

        //Put the autologin preference if it doesn't exist
        if (!sCurrentUser.getPrefs().contains("autologin")){
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

            //Retrieve saved info
            mUsername = sCurrentUser.getPrefs().getString("username", "");
            mPassword = sCurrentUser.getPrefs().getString("password", "");

            executeTask();
        }

        //TODO: 3/10/15: Handle password authentication/matching to username
        //Autologin did not occur, enter info manually
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

                    mUsername = mUsernameField.getText().toString();

                    //Check if this user has been previously stored
                    if (sCurrentUser.getPrefs().contains(mUsername)){
                        mPassword = sCurrentUser.getPrefs().getString(mUsername, null);
                    }

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

        sCurrentUser.setUsername(mUsername);
        sCurrentUser.buildUrlsWithUsername();

        //Disable all fields for now
        mUsernameField.setEnabled(false);
        mPasswordField.setEnabled(false);
        mAutoLoginCheckbox.setEnabled(false);
        mLoginButton.setEnabled(false);

        mLoadCircle.setVisibility(View.VISIBLE);
        showToast("Logging in as: "+mUsername, Toast.LENGTH_LONG);

        GetWorkOrdersTask task = new GetWorkOrdersTask(myFragment, sCurrentUser, getActivity());
        task.execute();
    }

    public void onTaskSuccess() {

        //Re-enable elements, in case user comes back to login screen later
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);
        mLoadCircle.setVisibility(View.INVISIBLE);

        sCurrentUser.getCurrentRefresh();

        //Move on to the next activity
        Intent i = new Intent(getActivity(), WorkOrderListActivity.class);
        startActivity(i);

        //Leave the login screen activity running
    }


    public void onNetworkFail(){
        //Re-enable all fields so user can try again
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);

        mLoadCircle.setVisibility(View.INVISIBLE);

        //Display error about network fail
        showToast("Network Fail", Toast.LENGTH_LONG);
    }

    public void onAuthenticateFail(){
        //Re-enable all fields so user can try again
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);

        mLoadCircle.setVisibility(View.INVISIBLE);

        //mUsernameField.setError("Password and username didn't match");
        showToast("Authenticate Fail", Toast.LENGTH_LONG);
    }

    public void showToast(String text, int duration){
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), text, duration);
        toast.show();
    }

}


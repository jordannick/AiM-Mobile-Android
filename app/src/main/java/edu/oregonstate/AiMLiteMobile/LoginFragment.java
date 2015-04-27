package edu.oregonstate.AiMLiteMobile;

import android.app.Fragment;
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
public class LoginFragment extends Fragment implements TaskGetWorkOrders.OnTaskCompleted {

    private static final String TAG = "LoginFragment";

    // %% DEBUG %%
    private static final boolean BYPASS_LOGIN_SCREEN = false;
    private static final String BYPASS_USER_NAME = "crosst";
    // %% DEBUG %%

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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_view, parent, false);
        getActivity().setTitle(R.string.login_title);

        mUsernameField = (EditText)v.findViewById(R.id.login_username);
        mPasswordField = (EditText)v.findViewById(R.id.login_password);
        mAutoLoginCheckbox = (CheckBox)v.findViewById(R.id.auto_login);
        mLoginButton = (Button)v.findViewById(R.id.login_button);
        mLoadCircle = (ProgressBar)v.findViewById(R.id.load_circle);
        mLoadCircle.setVisibility(View.INVISIBLE);

        if(BYPASS_LOGIN_SCREEN){
            mUsernameField.setText(BYPASS_USER_NAME);
            mPasswordField.setText("abc");
        }
        //%%%

        loginHandler();

        return v;
    }

    private void loginHandler(){

        //Check if autologin should occur
        if (sCurrentUser.getPrefs().getBoolean("autologin", false)){

            //Retrieve saved info
            mUsername = sCurrentUser.getPrefs().getString("username", "");
            mPassword = sCurrentUser.getPrefs().getString("password", "");

            executeTask();
        }

        //TODO: 3/10/15: Handle password authentication/matching to username
        //TODO: 3/19/15: Think about clearing stored JSON/prefs if it's tied to a different user.
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

                    //Done authenticating, set up for next activity
                    executeTask();
                }
            }
        });
        if(BYPASS_LOGIN_SCREEN) mLoginButton.callOnClick();
    }

    private void executeTask(){

        sCurrentUser.setUsername(mUsername);
        sCurrentUser.buildUrlsWithUsername();

        //Disable all fields for now
        mUsernameField.setEnabled(false);
        mPasswordField.setEnabled(false);
        mAutoLoginCheckbox.setEnabled(false);
        mLoginButton.setEnabled(false);

        mLoadCircle.setVisibility(View.VISIBLE);

        Log.i(TAG, "Logging in as: "+mUsername);
        showToast("Logging in as: "+mUsername, Toast.LENGTH_LONG);

        //Attempt the request, try force pulling new list since we're logging in. Callback to success or fail function in this class.
        boolean forceRefresh = true;
        TaskGetWorkOrders task = new TaskGetWorkOrders(this, sCurrentUser, getActivity(), forceRefresh);
        task.execute();
    }

    public void onTaskSuccess() {
        Log.d(TAG, "OnTaskSuccess in LoginFragment");


        //Re-enable elements, in case user comes back to login screen later
        mUsernameField.setEnabled(true);
        mPasswordField.setEnabled(true);
        mAutoLoginCheckbox.setEnabled(true);
        mLoginButton.setEnabled(true);
        mLoadCircle.setVisibility(View.INVISIBLE);

        //Move on to the next activity
        Intent i = new Intent(getActivity(), OverviewListActivity.class);
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



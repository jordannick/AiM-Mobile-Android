package edu.oregonstate.AiMLiteMobile.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.ArrayList;
import java.util.Date;

import edu.oregonstate.AiMLiteMobile.Activities.OverviewListActivity;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.R;
import edu.oregonstate.AiMLiteMobile.Network.ResponseLogin;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    //Layout variables
    private EditText mUsernameField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private CheckBox mAutoLoginCheckbox;
    private ProgressBar mLoadCircle;
    private TextView userIcon;
    private TextView passwordIcon;

    private static CurrentUser sCurrentUser;

    //Login values
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

        userIcon = (TextView)v.findViewById(R.id.login_username_icon);
        passwordIcon = (TextView)v.findViewById(R.id.login_password_icon);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        userIcon.setTypeface(tf); userIcon.setText(R.string.icon_user);
        passwordIcon.setTypeface(tf); passwordIcon.setText(R.string.icon_password);

        loginHandler();

        return v;
    }

    private void loginHandler(){
        //Check if autologin should occur
        if (sCurrentUser.getPrefs().getBoolean("autologin", false)){
            //Retrieve saved info
            mUsername = sCurrentUser.getPrefs().getString("username", "");
            mPassword = sCurrentUser.getPrefs().getString("password", "");
            if(mUsername != "" && mPassword != "") {
                attemptLogin();
            }
        }
        //TODO: 3/10/15: Handle password authentication/matching to username
        //TODO: 3/19/15: Think about clearing stored JSON/prefs if it's tied to a different user.
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for empty fields
                if (mUsernameField.getText().toString().matches("")) {
                    mUsernameField.setError("Username required");
                }
                if (mPasswordField.getText().toString().matches("")) {
                    mPasswordField.setError("Password required");
                }
                //If both fields are not empty
                if (!mUsernameField.getText().toString().matches("") && !mPasswordField.getText().toString().matches("")) {
                    mUsername = mUsernameField.getText().toString();
                    mPassword = mPasswordField.getText().toString();
                    attemptLogin();
                }
            }
        });
    }

    private void attemptLogin(){
        sCurrentUser.setUsername(mUsername);
        sCurrentUser.setPassword(mPassword);
        setEnableFormFields(false);
        SnackbarManager.show(Snackbar.with(getActivity()).text("Logging in as: " + mUsername).duration(Snackbar.SnackbarDuration.LENGTH_LONG));

        ApiManager.getService().loginUser(mUsername, mPassword, new Callback<ResponseLogin>() {
            @Override
            public void success(ResponseLogin responseLogin, Response response) {
                Log.d(TAG, "API MANAGER: loginUser :: OK :: Token :" + responseLogin.getToken());
                CurrentUser.setToken(responseLogin.getToken());
                onLoginSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "API MANAGER: loginUser :: ERROR :: " + error);
            }
        });
    }
    public void onLoginSuccess(){
        Log.d(TAG, "Login Success");
        //Save user info for future autologins
        if (mAutoLoginCheckbox.isChecked()) {
            sCurrentUser.getPrefsEditor().putString("username", mUsername);
            sCurrentUser.getPrefsEditor().putString("password", mPassword);
            sCurrentUser.getPrefsEditor().putBoolean("autologin", true);
            sCurrentUser.getPrefsEditor().apply();
        }

        ApiManager.getService().getWorkOrders(CurrentUser.getUsername(), CurrentUser.getToken(), new Callback<ResponseWorkOrders>() {
            @Override
            public void success(ResponseWorkOrders responseWorkOrders, Response response) {
                ArrayList<WorkOrder> workOrders = responseWorkOrders.getWorkOrders();
                String logStr = "API MANAGER: getWorkOrders :: OK :: Size :" + workOrders.size();
                for (int i = 0; i < workOrders.size(); i++) {
                    WorkOrder workOrder = workOrders.get(i);
                    logStr += "\nWO #" + workOrder.getProposalPhase() + " " + workOrder.getDescription();
                }
                Log.d(TAG, logStr);

                //Save raw JSON for offline use
                sCurrentUser.getPrefsEditor().putString("work_order_data", responseWorkOrders.getRawJson());

                //Save new lastUpdated
                Date retrievedDate = new Date(System.currentTimeMillis());
                Log.i(TAG, "Saving new last_updated: " + retrievedDate.toString());
                sCurrentUser.getPrefsEditor().putLong("last_updated", retrievedDate.getTime());
                sCurrentUser.getPrefsEditor().apply();

                //Save the workOrders
                sCurrentUser.setWorkOrders(workOrders);

                setEnableFormFields(true);
                startActivity(new Intent(getActivity(), OverviewListActivity.class));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "API MANAGER: getWorkOrders :: ERROR :: " + error);
            }
        });
    }

    /* Sets the enable property of known input elements.
       @param enable (required) boolean for new enable
    */
    private void setEnableFormFields(boolean enable){
        mUsernameField.setEnabled(enable);
        mPasswordField.setEnabled(enable);
        mAutoLoginCheckbox.setEnabled(enable);
        mLoginButton.setEnabled(enable);

        mLoadCircle.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }


    /* Shows new Snackbar with errorMessage
      @param errorMessage (required) string to display in snackbar alert.
    */
    private void showErrorSnackbar(String errorMessage){
        Snackbar.SnackbarDuration duration = Snackbar.SnackbarDuration.LENGTH_INDEFINITE;
        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text(errorMessage).duration(duration)
                        .actionLabel("DISMISS")
                        .actionColor(Color.RED));
    }
}



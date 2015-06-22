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
import edu.oregonstate.AiMLiteMobile.Models.Notice;
import edu.oregonstate.AiMLiteMobile.Models.WorkOrder;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseLogin;
import edu.oregonstate.AiMLiteMobile.Network.ResponseNotices;
import edu.oregonstate.AiMLiteMobile.Network.ResponseWorkOrders;
import edu.oregonstate.AiMLiteMobile.R;
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
    private TextView mSavedUserText;
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
        sCurrentUser = CurrentUser.get(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_view, parent, false);
        getActivity().setTitle(R.string.login_title);

        mUsernameField = (EditText)v.findViewById(R.id.login_username);
        mPasswordField = (EditText)v.findViewById(R.id.login_password);
        mAutoLoginCheckbox = (CheckBox)v.findViewById(R.id.auto_login);
        mSavedUserText = (TextView)v.findViewById(R.id.loginView_savedUser);
        mLoginButton = (Button)v.findViewById(R.id.login_button);
        mLoadCircle = (ProgressBar)v.findViewById(R.id.load_circle);
        mLoadCircle.setVisibility(View.INVISIBLE);

        userIcon = (TextView)v.findViewById(R.id.login_username_icon);
        passwordIcon = (TextView)v.findViewById(R.id.login_password_icon);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FontAwesome.otf");
        userIcon.setTypeface(tf); userIcon.setText(R.string.icon_user);
        passwordIcon.setTypeface(tf); passwordIcon.setText(R.string.icon_password);

        // Prevents autologin after user logout
        boolean autologin = getActivity().getIntent().getBooleanExtra("autologin", true);

        loginHandler(autologin);

        return v;
    }

    private void loginHandler(boolean autologin){
        //Check if autologin should occur
        if (sCurrentUser.getPreferences().getAutoLogin()){
            if (autologin) {
                mUsername = sCurrentUser.getPreferences().getUsername();
                mPassword = sCurrentUser.getPreferences().getPassword();
                if (mUsername != "" && mPassword != "") {
                    attemptLogin();
                }
            } else {
                mSavedUserText.setText(sCurrentUser.getPreferences().getUsername()+"\ncurrently saved");
            }
        }

        //TODO: 3/10/15: Handle password authentication/matching to username
        //TODO: 3/19/15: Think about clearing stored JSON/prefs if it's tied to a different user.
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = mUsernameField.getText().toString();
                mPassword = mPasswordField.getText().toString();
                //Check for empty fields
                if (!mUsername.matches("") && !mPassword.matches("")) {
                    //If both fields are not empty
                    attemptLogin();
                }else{
                    if (mUsername.matches("")) {
                        mUsernameField.setError("Username required");
                    }
                    if (mPassword.matches("")) {
                        mPasswordField.setError("Password required");
                    }
                }
            }
        });
    }

    private void attemptLogin(){
        setEnableFormFields(false);

        ApiManager.getService().loginUser(mUsername, mPassword, new Callback<ResponseLogin>() {
            @Override
            public void success(ResponseLogin responseLogin, Response response) {
                sCurrentUser.setUsername(mUsername);
               // sCurrentUser.setPassword(mPassword);
                sCurrentUser.setToken(responseLogin.getToken());
                setEnableFormFields(true);

                //Save user info for future autologins
                if (mAutoLoginCheckbox.isChecked()) {
                    sCurrentUser.getPreferences().saveAutoLogin(mUsername, mPassword);
                }

                //TODO: finish the activity
                getActivity().finish();
                startActivity(new Intent(getActivity(), OverviewListActivity.class));
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO: Display descriptive error message, such as wrong password.
                SnackbarManager.show(Snackbar.with(getActivity()).text("Login Failed").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                setEnableFormFields(true);
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

}



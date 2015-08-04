package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.oregonstate.AiMLiteMobile.Helpers.DialogUtils;
import edu.oregonstate.AiMLiteMobile.Helpers.InternalStorageWriter;
import edu.oregonstate.AiMLiteMobile.Models.CurrentUser;
import edu.oregonstate.AiMLiteMobile.Network.ApiManager;
import edu.oregonstate.AiMLiteMobile.Network.ResponseLogin;
import edu.oregonstate.AiMLiteMobile.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "AiM_LoginActivity";

    //Layout variables
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.login_username) EditText mUsernameField;
    @Bind(R.id.login_password) EditText mPasswordField;
    @Bind(R.id.login_button) Button mLoginButton;
    @Bind(R.id.auto_login) CheckBox mAutoLoginCheckbox;
    @Bind(R.id.loginView_savedUser) TextView mSavedUserText;
    @Bind(R.id.load_circle) ProgressBar mLoadCircle;
    @Bind(R.id.login_username_icon) TextView userIcon;
    @Bind(R.id.login_password_icon) TextView passwordIcon;
    @Bind(R.id.forget_button) Button forgetAutoLoginButton;
    @Bind(R.id.offline_button) Button offlineButton;

    private static CurrentUser sCurrentUser;

    //Login values
    private String mUsername;
    private String mPassword;

    public static final String EXTRA_LOGIN = "login";
    public static final String EXTRA_OFFLINE = "offline";

    private AppCompatActivity activity = this;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.login_title);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        sCurrentUser = CurrentUser.get(getApplicationContext());
        mLoadCircle.setVisibility(View.INVISIBLE);

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/FontAwesome.otf");
        userIcon.setTypeface(tf);
        userIcon.setText(R.string.icon_user);
        passwordIcon.setTypeface(tf);
        passwordIcon.setText(R.string.icon_password);

        // Prevents autologin after user logout
        boolean autologin = getIntent().getBooleanExtra("autologin", true);

        forgetAutoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavedUserText.setVisibility(View.GONE);
                forgetAutoLoginButton.setVisibility(View.GONE);

                //TODO remove autologin info for user
            }
        });

        loginHandler(autologin);

        InternalStorageWriter.logSavedFiles(this);
    }


    private void loginHandler(boolean autologin) {
        //Check if autologin should occur
        if (sCurrentUser.getPreferences().getAutoLogin()) {
            if (autologin) {
                mUsername = sCurrentUser.getPreferences().getUsername();
                mPassword = sCurrentUser.getPreferences().getPassword();
                if (!mUsername.equals("") && !mPassword.equals("")) {
                    attemptLogin();
                }
            } else {
                mSavedUserText.setText(sCurrentUser.getPreferences().getUsername() + " set to auto-login");
                mSavedUserText.setVisibility(View.VISIBLE);
                forgetAutoLoginButton.setVisibility(View.VISIBLE);

/*                //Autofill username and autologin checkbox. Still require password?
                mUsernameField.setText(sCurrentUser.getPreferences().getUsername());
                mAutoLoginCheckbox.setChecked(true);
                mPasswordField.requestFocus();*/
            }
        }

        offlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = "crosst";
                mPassword = "aaa";

                if (InternalStorageWriter.hasSavedData(activity, mUsername)/* && sCurrentUser.getPreferences().getUsername() != "" && sCurrentUser.getPreferences().getToken() != ""*/) {
                    Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            offlineLogin();

                        }
                    };
                    DialogUtils.createConfirmDialog(activity, "Load offline data for " + mUsername + "?", clickListener);
                }else {
                    SnackbarManager.show(Snackbar.with(activity).text("No Offline Data").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                }
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = mUsernameField.getText().toString();
                mPassword = mPasswordField.getText().toString();

                    //Check for empty fields
                    if (!mUsername.matches("") && !mPassword.matches("")) {
                        //If both fields are not empty
                        attemptLogin();
                    } else {
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

    private void offlineLogin(){
        sCurrentUser.setUsername(mUsername);
        sCurrentUser.setOfflineMode(true);
        setEnableFormFields(true);
        startOverviewActivity(true);
    }

    private void startOverviewActivity(boolean offline){
        this.finish();
        Intent intent = new Intent(this, OverviewListActivity.class);
        intent.putExtra(EXTRA_LOGIN, true);
        intent.putExtra(EXTRA_OFFLINE, offline);
        startActivity(intent);
    }

    private void attemptLogin() {
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

                startOverviewActivity(false);
            }

            @Override
            public void failure(RetrofitError error) {
                //TODO: Display descriptive error message, such as wrong password.
                SnackbarManager.show(Snackbar.with(activity).text("Login Failed").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                setEnableFormFields(true);


                // No network, will try checking stored work orders for offline view
                if (error.getKind() == RetrofitError.Kind.NETWORK){
                    if (InternalStorageWriter.hasSavedData(activity, mUsername)) {
                        Dialog.OnClickListener clickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                offlineLogin();
                            }
                        };
                        DialogUtils.createConfirmDialog(activity, "Load offline data for " + mUsername + "?", clickListener);
                    }else {
                        SnackbarManager.show(Snackbar.with(activity).text("No Offline Data").duration(Snackbar.SnackbarDuration.LENGTH_LONG));
                    }
                }
            }
        });
    }

    /* Sets the enable property of known input elements.
       @param enable (required) boolean for new enable
    */
    private void setEnableFormFields(boolean enable) {
        mUsernameField.setEnabled(enable);
        mPasswordField.setEnabled(enable);
        mAutoLoginCheckbox.setEnabled(enable);
        mLoginButton.setEnabled(enable);
        mLoadCircle.setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
    }

}

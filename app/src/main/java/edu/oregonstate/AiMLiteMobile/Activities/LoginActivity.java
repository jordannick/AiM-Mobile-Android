package edu.oregonstate.AiMLiteMobile.Activities;

import android.app.Fragment;
import edu.oregonstate.AiMLiteMobile.Fragments.LoginFragment;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginActivity extends SingleFragmentActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

}

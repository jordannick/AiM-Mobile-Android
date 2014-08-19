package com.jordann.AiMMobile;

import android.app.Fragment;

/**
 * Created by jordan_n on 8/15/2014.
 */
public class LoginActivity extends SingleFragmentActivity {

    private Fragment mFragment;

    @Override
    protected Fragment createFragment() {
        mFragment = new LoginFragment();
        return mFragment;
    }




}

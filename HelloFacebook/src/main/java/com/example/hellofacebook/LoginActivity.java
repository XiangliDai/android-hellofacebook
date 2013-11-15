package com.example.hellofacebook;

import android.support.v4.app.Fragment;

/**
 * Created by xdai on 11/14/13.
 */
public class LoginActivity extends SingleFrameActivity {

    @Override
    protected Fragment createFragment() {
        return new FacebookLoginFragment();
    }
}

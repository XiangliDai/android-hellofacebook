package com.example.hellofacebook;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Created by xdai on 11/14/13.
 */
public class LoginActivity extends SingleFrameActivity {

    @Override
    protected SherlockFragment createFragment() {
        return new FacebookLoginFragment();
    }
}

package com.example.hellofacebook;

import android.support.v4.app.Fragment;

public class FriendsActivity extends SingleFrameActivity {

    @Override
    protected Fragment createFragment(){
        return new FacebookFriendsFragment();
    }

}

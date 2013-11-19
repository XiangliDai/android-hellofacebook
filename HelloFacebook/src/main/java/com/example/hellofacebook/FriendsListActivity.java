package com.example.hellofacebook;

import android.support.v4.app.Fragment;

public class FriendsListActivity extends SingleFrameActivity {

    @Override
    protected Fragment createFragment(){
        String userId = (String)getIntent().getSerializableExtra(FacebookProfileFragment.EXTRA_USER_ID);
        return FacebookFriendsListFragment.newInstance(userId);
    }

}

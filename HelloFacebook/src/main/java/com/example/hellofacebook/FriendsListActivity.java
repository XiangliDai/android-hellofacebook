package com.example.hellofacebook;

import android.app.SearchManager;
import android.content.Intent;

import com.actionbarsherlock.app.SherlockListFragment;

public class FriendsListActivity extends SingleListFrameActivity {

    @Override
    protected SherlockListFragment createFragment(){
        String userId = (String)getIntent().getSerializableExtra(FacebookProfileFragment.EXTRA_USER_ID);
        return FacebookFriendsListFragment.newInstance(userId);
        //return new SherlockFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
       setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            FacebookFriendsListFragment fragment =(FacebookFriendsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if(fragment!=null)
                fragment.doQuery(query);
        }

    }

}

package com.example.hellofacebook;

/**
 * Created by xdai on 11/13/13.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

/**
 * A placeholder fragment containing a simple view.
 */
public  class FacebookProfileFragment extends SherlockFragment {
    private static final String TAG = "FacebookProfileFragment";
    private ProfilePictureView profilePictureView;
    private TextView userProfile;
    private Button viewFriendsButton;
    private GraphUser graphUser;
    private static final int REQUEST_PUBLISHER = 0;
    private static final String DIALOG_PUBLISHER = "Post";
    public static final String EXTRA_USER_ID = "user_id";
    private String userId;
    FacebookAPI<GraphUser> facebookAPI;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userId  = (String)getArguments().getSerializable(EXTRA_USER_ID);
        getFacebookProfile(userId);

    }

    public static FacebookProfileFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        FacebookProfileFragment facebookProfileFragment = new FacebookProfileFragment();
        facebookProfileFragment.setArguments(args);

        return facebookProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        userProfile = (TextView) rootView.findViewById(R.id.user_profile);
        profilePictureView = (ProfilePictureView) rootView.findViewById(R.id.profile_pic);
        profilePictureView.setCropped(true);
        profilePictureView.setVisibility(View.INVISIBLE);
        viewFriendsButton = (Button)rootView.findViewById(R.id.get_friends_button);
        viewFriendsButton.setVisibility(View.INVISIBLE);
        viewFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FriendsListActivity.class);
                intent.putExtra(EXTRA_USER_ID, userId);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_post:
                showPublisherDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == REQUEST_PUBLISHER){
            String message = data.getStringExtra(FacebookPublisherFragment.EXTRA_MESSAGE);
            Log.d(TAG, message);
            postToWall(message);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        graphUser = null;
    }

    private void getFacebookProfile(String userId){
        final Session session = Session.getActiveSession();
        if (session.isOpened()) {
                facebookAPI = new  FacebookAPI<GraphUser>();
                facebookAPI.setListener(new FacebookAPI.Listener<GraphUser>() {
                    @Override
                    public void onPayloadDownloaded(GraphUser user, Response response) {
                        graphUser = user;
                        updateUI();
                    }
                });
            if(userId == null)
                facebookAPI.getMyProfile(session);
            else
                facebookAPI.getProfileForUser(session,userId);

        }
    }

    private void showPublisherDialog(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        // Create and show the dialog.
        FacebookPublisherFragment publisherFragment = FacebookPublisherFragment.newInstance(DIALOG_PUBLISHER);
        publisherFragment.setTargetFragment(FacebookProfileFragment.this, REQUEST_PUBLISHER);
        publisherFragment.show(fm, DIALOG_PUBLISHER);
    }

    private void updateUI() {
        if (graphUser != null) {
            profilePictureView.setProfileId(graphUser.getId());
            profilePictureView.setVisibility(View.VISIBLE);
            Log.d(TAG, graphUser.getFirstName());
            viewFriendsButton.setVisibility(View.VISIBLE);
            userProfile.setText(new ProfileUtil().buildUserInfoDisplay(graphUser));
        }
    }

    public void postToWall(String message){
        Session session = Session.getActiveSession();
        if(session!=null && session.isOpened()){
        Request.newStatusUpdateRequest(session, message, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Log.e(TAG, error.toString());
                    showToast("Failed to post");
                } else {
                    showToast(response.toString());
                }
            }
        }).executeAsync();
        }
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}

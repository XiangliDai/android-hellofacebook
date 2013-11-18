package com.example.hellofacebook;

/**
 * Created by xdai on 11/13/13.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public  class FacebookMeFragment extends Fragment {
    private static final String TAG = "FacebookMeFragment";
    private ProfilePictureView profilePictureView;
    private TextView welcome;
    private Button viewFriendsButton;
    private GraphUser graphUser;
    private static final int REQUEST_PUBLISHER = 0;
    private static final String DIALOG_PUBLISHER="Post";

    private Facebook mFacebook;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getFacebookProfile();

    }

     private void getFacebookProfile(){
        Session session = Session.getActiveSession();
             if (session.isOpened()) {
         // make request to the /me API
         // Get current logged in user information

         Request meRequest = Request.newMeRequest(session, new Request.GraphUserCallback() {
             @Override
             public void onCompleted(GraphUser user, Response response) {
                 FacebookRequestError error = response.getError();
                 if (error != null) {
                     Log.e(TAG, error.toString());
                 }
                 else {
                     graphUser = user;
                     updateUI();
                     profilePictureView.setProfileId(graphUser.getId());

                 }
             }
         });
        meRequest.executeAsync();
     }

 }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        welcome = (TextView) rootView.findViewById(R.id.welcome_text);
        profilePictureView = (ProfilePictureView) rootView.findViewById(R.id.profile_pic);
        profilePictureView.setCropped(true);
        viewFriendsButton = (Button)rootView.findViewById(R.id.get_friends_button);
        viewFriendsButton.setVisibility(View.INVISIBLE);
        viewFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FriendsActivity.class);
                startActivity(intent);

            }
        });
        return rootView;
    }

    @Override //Add action bar
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
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


    private void showPublisherDialog(){
        FragmentManager fm = getActivity().getSupportFragmentManager();
        // Create and show the dialog.
        FacebookPublisherFragment publisherFragment = FacebookPublisherFragment.newInstance(DIALOG_PUBLISHER);
        publisherFragment.setTargetFragment(FacebookMeFragment.this, REQUEST_PUBLISHER);
        publisherFragment.show(fm, DIALOG_PUBLISHER);
    }

    private void updateUI() {
        if (graphUser != null) {
            Log.d(TAG, graphUser.getFirstName());
            viewFriendsButton.setVisibility(View.VISIBLE);
            welcome.setText(buildUserInfoDisplay(graphUser));
        }
    }

    //TODO: create a model of this
    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.getProperty("languages");
        if (languages.length() > 0) {
            ArrayList<String> languageNames = new ArrayList<String> ();
            for (int i=0; i < languages.length(); i++) {
                JSONObject language = languages.optJSONObject(i);
                // Add the language name to a list. Use JSON
                // methods to get access to the name field.
                languageNames.add(language.optString("name"));
            }
            userInfo.append(String.format("Languages: %s\n\n",
                    languageNames.toString()));
        }

        return userInfo.toString();
    }

    public void postToWall(String message){
        Session session = Session.getActiveSession();
        if(session!=null && session.isOpened()){
        Request postRequest = Request.newStatusUpdateRequest(session, message, new Request.Callback() {
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
        });
            postRequest.executeAsync();
        }
    }

    private void showToast(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


}

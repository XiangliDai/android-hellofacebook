package com.example.hellofacebook;

/**
 * Created by xdai on 11/13/13.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public  class FacebookMeFragment extends Fragment {
    private static final String TAG = "FacebookMeFragment";
    private ProfilePictureView profilePictureView;
    private TextView welcome;
    private Button viewFriendsButton;
    private GraphUser graphUser;
    private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
    private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
    private boolean pendingPublishReauthorization = false;
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
                //postToFacebook();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void postToFacebook() {

       Session session = Session.getActiveSession();

       if (session != null && session.isOpened()){

                // Check for publish permissions
        List<String> permissions = session.getPermissions();
        if (!isSubsetOf(PERMISSIONS, permissions)) {
            pendingPublishReauthorization = true;
            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS);
            session.requestNewPublishPermissions(newPermissionsRequest);
            return;
        }

        Bundle postParams = new Bundle();
        postParams.putString("name", "Facebook SDK for Android");
        postParams.putString("caption", "Build great social apps and get more installs.");
        postParams.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
        postParams.putString("link", "https://developers.facebook.com/android");
        postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

        Request.Callback callback= new Request.Callback() {
            public void onCompleted(Response response) {
                JSONObject graphResponse = response
                        .getGraphObject()
                        .getInnerJSONObject();
                String postId = null;
                try {
                    postId = graphResponse.getString("id");
                } catch (JSONException e) {
                    Log.i(TAG,
                            "JSON error "+ e.getMessage());
                }
                FacebookRequestError error = response.getError();
                if (error != null) {
                    Toast.makeText(getActivity()
                            .getApplicationContext(),
                            error.getErrorMessage(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity()
                            .getApplicationContext(),
                            postId,
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        Request request = new Request(session, "me/feed", postParams,HttpMethod.POST, callback);

        RequestAsyncTask task = new RequestAsyncTask(request);
        task.execute();
      }


    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    private void updateUI() {
        if (graphUser != null) {
            Log.d("TEST", graphUser.getFirstName());
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
}

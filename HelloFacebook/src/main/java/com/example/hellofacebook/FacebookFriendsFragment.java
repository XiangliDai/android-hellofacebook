package com.example.hellofacebook;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.util.List;

/**
 * Created by xdai on 11/13/13.
 */
public class FacebookFriendsFragment extends ListFragment {
    private static final String TAG = "FacebookFriendsFragment";
    /**
     * A placeholder fragment containing a simple view.
     */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = super.onCreateView(inflater, container, savedInstanceState);
            return rootView;
        }
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        makeFacebookRequst();
    }



    private void makeFacebookRequst(){
        // start Facebook Login
        final Session session = Session.getActiveSession();
        if (session != null ){//&& session.isOpened()) {

            // make request to the /me API
            // Get current logged in user information
            Request friendsRequestRequest = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {

                @Override
                public void onCompleted(List<GraphUser> users, Response response) {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Log.e(TAG, error.toString());
                        // handleError(error, true);
                    } else if (session == Session.getActiveSession()) {
                        // Set the currentFBUser attribute
                        // ((FriendSmashApplication)getApplication()).setCurrentFBUser(user);
                       // graphUser = user;
                        //updateUI();
                    }
                }
            });
            friendsRequestRequest.executeAsync();
        }
    }
}

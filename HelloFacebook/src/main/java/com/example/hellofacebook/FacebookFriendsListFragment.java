package com.example.hellofacebook;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdai on 11/13/13.
 */
public class FacebookFriendsListFragment extends ListFragment {
    private static final String TAG = "FacebookFriendsListFragment";
    private ArrayList<GraphUser> mUsers;
    private ProfilePictureView profilePictureView;
    private String userId;
    /**
     * A placeholder fragment containing a simple view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if(NavUtils.getParentActivityName(getActivity()) != null)
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        userId  = (String)getArguments().getSerializable(FacebookProfileFragment.EXTRA_USER_ID);
        if(userId == null)
            getFacebookFriends();
        else
            getFacebookFriendsForUser();
        mUsers = new ArrayList<GraphUser>();
        FriendsAdapter adapter = new FriendsAdapter(mUsers);
        setListAdapter(adapter);
    }

    public static FacebookFriendsListFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putSerializable(FacebookProfileFragment.EXTRA_USER_ID, userId);
        FacebookFriendsListFragment facebookFriendsListFragment = new FacebookFriendsListFragment();
        facebookFriendsListFragment.setArguments(args);

        return facebookFriendsListFragment;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        GraphUser user = ((FriendsAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), ProfileActivity.class);

        i.putExtra(FacebookFriendProfileFragment.EXTRA_USER_ID, user.getId());
        startActivity(i);
        Log.d(TAG, user.getId().toString() + " is clicked");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(getActivity()) != null)
                    NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
        mUsers = null;
    }

    private void getFacebookFriends(){
        // start Facebook Login
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // make request to the /friends API
            Request friendsRequestRequest = Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
                @Override
                public void onCompleted(List<GraphUser> users, Response response) {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Log.e(TAG, error.toString());
                    } else if (session == Session.getActiveSession()) {
                        mUsers.addAll (users);
                        FriendsAdapter adapter = (FriendsAdapter)getListAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            friendsRequestRequest.executeAsync();
        }
    }


    private void getFacebookFriendsForUser(){
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            /* make the API call*/
            new Request(
                    session,
                    "me/mutualfriends/"+ userId,
                    null,
                    HttpMethod.GET,
                    new Request.Callback() {
                        public void onCompleted(Response response) {
                            JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                            try {
                                JSONArray users = (JSONArray)graphResponse.get("data");
                                if (users.length() > 0) {
                                //    ArrayList<GraphUser> graphUsers = new ArrayList<GraphUser> ();
                                    for (int i=0; i < users.length(); i++) {
                                        JSONObject user = users.optJSONObject(i);
                                        // Add the language name to a list. Use JSON
                                        // methods to get access to the name field.
                                      // GraphUser graphUser =  new ProfileUtil().convertJSONObjectToGraphUser(user);
                                        mUsers.add(new ProfileUtil().convertJSONObjectToGraphUser(user));
                                    }
                                  //  mUsers = graphUsers;
                                    FriendsAdapter adapter = (FriendsAdapter)getListAdapter();
                                    adapter.notifyDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
    }

    private class FriendsAdapter extends ArrayAdapter<GraphUser>{

        public FriendsAdapter(ArrayList<GraphUser> users){
            super(getActivity(),0, users);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView ==  null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.friend_list_item, null);

            GraphUser user = getItem(position);
            profilePictureView = (ProfilePictureView) convertView.findViewById(R.id.friend_profile_pic);

            profilePictureView.setCropped(true);
            profilePictureView.setPresetSize(profilePictureView.SMALL);
            profilePictureView.setProfileId(user.getId());
            TextView nameText = (TextView)convertView.findViewById(R.id.friend_name);
            nameText.setText(user.getName());

            return convertView;
        }
    }

}

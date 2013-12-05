package com.example.hellofacebook;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdai on 11/13/13.
 */
public class FacebookFriendsListFragment extends SherlockListFragment {
    private static final String TAG = "FacebookFriendsListFragment";
    private ArrayList<GraphUser> mUsers;
    private ProfilePictureView profilePictureView;
    private String userId;
    FacebookAPI<List<GraphUser>> facebookAPI;

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
        Intent intent = getActivity().getIntent();
        if (!intent.ACTION_SEARCH.equals(intent.getAction())) {
            FriendsListManager.get(getActivity()).removeAllUsers();
            userId  = (String)getArguments().getSerializable(FacebookProfileFragment.EXTRA_USER_ID);
            getFacebookFriends(userId);
            mUsers = new ArrayList<GraphUser>();
            FriendsAdapter adapter = new FriendsAdapter(mUsers);
            setListAdapter(adapter);
        }
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
        i.putExtra(FacebookProfileFragment.EXTRA_USER_ID, user.getId());
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
            case R.id.action_search:
                getActivity().onSearchRequested();
                return true ;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            inflater.inflate(R.menu.friend_search, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (null != searchView ){
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                searchView.setIconifiedByDefault(false);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mUsers = null;
    }


    private void getFacebookFriends(String userId){
        final Session session = Session.getActiveSession();
        if (session.isOpened()) {
            facebookAPI = new  FacebookAPI<List<GraphUser>>();
            facebookAPI.setListener(new FacebookAPI.Listener<List<GraphUser>>() {
                @Override
                public void onPayloadDownloaded(List<GraphUser> users, Response response) {
                    mUsers.addAll(FriendsListManager.get(getActivity()).insertFriends(users));
                    notifyDataSetChanged();
                }
            });
            if(userId == null)
                facebookAPI.getMyFriends(session);
            else
                facebookAPI.getFriendsForUser(session,userId);

        }
    }

    public void doQuery(String query) {
        mUsers = new ArrayList<GraphUser>();
        FriendsAdapter adapter = new FriendsAdapter(mUsers);
        setListAdapter(adapter);
        try {
            if(query.length() == 0)
                mUsers.addAll(FriendsListManager.get(getActivity()).getAllUsers());
            else
                mUsers.addAll(FriendsListManager.get(getActivity()).getUsersBySearch(query));

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        notifyDataSetChanged();
    }

    private void notifyDataSetChanged(){
        FriendsAdapter adapter = (FriendsAdapter)getListAdapter();
        adapter.notifyDataSetChanged();
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

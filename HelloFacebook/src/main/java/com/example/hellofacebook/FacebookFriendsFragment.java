package com.example.hellofacebook;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xdai on 11/13/13.
 */
public class FacebookFriendsFragment extends ListFragment {
    private static final String TAG = "FacebookFriendsFragment";
    private ArrayList<GraphUser> mUsers;
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
        setRetainInstance(true);
        getFacebookFriends();
        mUsers = new ArrayList<GraphUser>();
        FriendsAdapter adapter = new FriendsAdapter(mUsers);
        setListAdapter(adapter);
    }

    private void getFacebookFriends(){
        // start Facebook Login
        final Session session = Session.getActiveSession();
        if (session != null && session.getState().isOpened()) {
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

    private class FriendsAdapter extends ArrayAdapter<GraphUser>{

        public FriendsAdapter(ArrayList<GraphUser> users){
            super(getActivity(),0, users);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView ==  null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.friend_list_item, null);

            GraphUser user = getItem(position);
            TextView nameText = (TextView)convertView.findViewById(R.id.text_friend_name);
            nameText.setText(user.getName());
            //try to get Zodiac for fun but API doens't return birthday!!
            //actually only id and name are returned
            if(user.getBirthday() != null){
                TextView zodiacText = (TextView)convertView.findViewById(R.id.text_friend_zodiac);
                zodiacText.setText(getZodiac(new Date(user.getBirthday())));
            }
            return convertView;
        }
    }

    private String getZodiac(Date date){
        HashMap<String, ArrayList<String>> dictionary = new HashMap<String, ArrayList<String>>();
        String[] signs = {"Aries", "Taurus","Gemini","Cancer", "Leo","Virgo","Libra","Scorpius",
               "Sagittarius", "Capricorn","Aquarius", "Pisces"};
        String[][] dates = {
                {"12 March", "18 April"},
                {"19 April","13 May"},
                {"14 May","21 June"},
                {"20 June", "20 July"},
                {"21 July","9 August"},
                {"10 August","15 September"},
                {"16 September", "30 October"},
                {"31 October","22 November"},
                {"23 November","17 December"},
                {"18 December", "18 January"},
                {"19 January","15 February"},
                {"16 February","11 March"},

        };
        for(int i=0; i< signs.length; i++){
            Date startDate = new Date(String.format("%s/%s", dates[i][0], "2000"));
            Date endDate = new Date(String.format("%s/%s", dates[i][1], "2000"));
            if(date.getMonth() >= startDate.getMonth()  &&
                    date.getMonth() <= endDate.getMonth()) return signs[i];

        }
        return "";
    }
}

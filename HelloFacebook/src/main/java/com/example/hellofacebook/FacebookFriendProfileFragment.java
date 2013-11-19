package com.example.hellofacebook;

/**
 * Created by xdai on 11/13/13.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public  class FacebookFriendProfileFragment extends Fragment {
    private static final String TAG = "FacebookFriendProfileFragment";
    private ProfilePictureView profilePictureView;
    private TextView profile;

    public static final String EXTRA_USER_ID="user_id";
    private String userId;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        userId  = (String)getArguments().getSerializable(EXTRA_USER_ID);
        getFacebookProfile();
    }

    public static FacebookFriendProfileFragment newInstance(String userId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_USER_ID, userId);
        FacebookFriendProfileFragment facebookFriendProfileFragment = new FacebookFriendProfileFragment();
        facebookFriendProfileFragment.setArguments(args);

        return facebookFriendProfileFragment;
    }

     private void getFacebookProfile(){
        Session session = Session.getActiveSession();
             if (session.isOpened()) {

               /* make the API call*/
                 new Request(
                         session,
                         "/"+ userId,
                         null,
                         HttpMethod.GET,
                         new Request.Callback() {
                                 public void onCompleted(Response response) {
                                     JSONObject graphResponse = response
                                             .getGraphObject()
                                             .getInnerJSONObject();

                                     updateUI(graphResponse);
                             }
                         }
                 ).executeAsync();

}
 }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.friend_profile_fragment, container, false);
        profilePictureView = (ProfilePictureView)rootView.findViewById(R.id.friend_profile_pic);
        profile = (TextView)rootView.findViewById(R.id.friend_profile_info);
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
       super.onActivityResult(requestCode, resultCode, data);


    }

    private void updateUI(JSONObject user) {
        if (user != null) {
            //Log.d(TAG, graphUser.getFirstName());
            profilePictureView.setProfileId(userId);

            profile.setText(buildUserInfoDisplay(user));
        }
    }

    //TODO: create a model of this
    private String buildUserInfoDisplay(JSONObject user) {
        StringBuilder userInfo = new StringBuilder("");
        try{
        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.get("name").toString()));
    userInfo.append(String.format("Gender: %s\n\n",
            user.get("gender").toString()));
    if( user.get("relationship_status")!= null){
    userInfo.append(String.format("Status: %s\n\n",
            user.get("relationship_status").toString()));}
    if(user.get("birthday") != null){
        String bday =user.get("birthday").toString();
           Date birthday =     new Date(String.format("%s/%s/%s", bday.split("/")[0], bday.split("/")[1], "2000"));
    userInfo.append(String.format("Zodiac: %s\n\n",
            getZodiac(birthday)));
    }
        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
    if(user.get("location") != null)
        userInfo.append(String.format("Location: %s\n\n",
                ((JSONObject)user.get("location")).get("name")));
    if(user.get("hometown") != null)
        userInfo.append(String.format("Hometown: %s\n\n",
                ((JSONObject)user.get("hometown")).get("name")));
    if(user.get("work") != null){
        JSONArray works = (JSONArray)user.get("work");
        JSONObject work = (JSONObject)works.optJSONObject(0);
        JSONObject employer = (JSONObject)work.get("employer");
        JSONObject position = (JSONObject)work.get("position");
        userInfo.append(String.format("Work: %s at %s\n\n",
                position.get("name") ,employer.get("name") ));
    }
        // Example: access via property name (locale)
        // - no special permissions required
        //userInfo.append(String.format("Locale: %s\n\n",
        //        user.getProperty("locale")));

        // Example: access via key for array (languages)
        // - requires user_likes permission
        JSONArray languages = (JSONArray)user.get("languages");
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
        }
        catch (JSONException e){
            Log.e("error", e.getMessage());
        }
        return userInfo.toString();
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

package com.example.hellofacebook;

import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdai on 11/21/13.
 */
public class FacebookAPI<Token> {
    private static final String TAG = "FacebookAPI";

    private Listener<Token> mListener;
    public interface Listener<Token>{
        void onPayloadDownloaded(Token token, Response response);
    }
    public void setListener(Listener<Token> listener){
        mListener = listener;
    }

    public void getMyProfile(final Session session){
        Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                mListener.onPayloadDownloaded((Token)user, response);
                            }
                        }
                        if (response.getError() != null) {
                            Log.e(TAG, response.getError().toString());
                        }
                    }
                }).executeAsync();
    }

    public void getProfileForUser(final Session session, String userId){
        new Request(
                session,
                "/"+ userId,
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                        mListener.onPayloadDownloaded((Token)new ProfileUtil().convertJSONObjectToGraphUser(graphResponse), response);
                    }
                }).executeAsync();
    }


    public void getMyFriends(final Session session){
        Request.newMyFriendsRequest(session,
            new Request.GraphUserListCallback() {
            @Override
                public void onCompleted(List<GraphUser> users, Response response) {
                    if (session == Session.getActiveSession()) {
                        if (users != null) {
                            mListener.onPayloadDownloaded((Token)users, response);
                        }
                    }
                    else if (response.getError() != null) {
                        Log.e(TAG, response.getError().getErrorMessage().toString());
                    }
                }
        }).executeAsync();
    }

    public void getFriendsForUser(final Session session, String userId){
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
                            ArrayList<GraphUser> userList = new ArrayList<GraphUser>();
                            if (users.length() > 0) {
                                for (int i=0; i < users.length(); i++) {
                                    JSONObject user = users.optJSONObject(i);
                                    userList.add(new ProfileUtil().convertJSONObjectToGraphUser(user));
                                }
                                mListener.onPayloadDownloaded((Token) userList, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
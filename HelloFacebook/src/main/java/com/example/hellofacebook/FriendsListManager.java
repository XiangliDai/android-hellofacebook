package com.example.hellofacebook;

import android.content.Context;

import com.facebook.model.GraphUser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdai on 11/20/13.
 */
public class FriendsListManager {
    private static final String TAG = "FriendsListManager";

    private static FriendsListManager mFriendsListManager;
    private Context mContext;
    private SqliteController mController;
    private FriendsListManager(Context context){
        mContext = context;
        mController = new SqliteController(context);
    }

    public static FriendsListManager get(Context context){
        if(mFriendsListManager == null){
            mFriendsListManager = new FriendsListManager(context);
        }
        return mFriendsListManager;
    }


    public GraphUser insertFriend(GraphUser user){
        mController.InsertFriend(user);
        return user;
    }

    public ArrayList<GraphUser> insertFriends(List<GraphUser> users){
        ArrayList<GraphUser> graphUsers = new ArrayList<GraphUser>(users);
        mController.InsertFriends(graphUsers);
        return graphUsers;
    }

    public ArrayList<GraphUser> getUsersBySearch(String query) throws JSONException {
        return mController.getFriendsSearch(query);
    }

    public ArrayList<GraphUser> getAllUsers() throws JSONException {
        return mController.getAllFriends();
    }

    public void removeAllUsers(){
        mController.removeFriends();
    }
}

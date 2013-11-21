package com.example.hellofacebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xdai on 11/19/13.
 */
public class SqliteController extends SQLiteOpenHelper {
    private static final String TAG = "SqliteController";
    public SqliteController(Context context) {
        super(context, "androidsqlite", null, 1);
        getWritableDatabase();
        Log.i(TAG, "database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE friends (id INTEGER PRIMARY KEY, name TEXT)";
        db.execSQL(query);
        Log.i(TAG, "table friends created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query = "DROP TABLE IF EXISTS friends";
        database.execSQL(query);
        onCreate(database);
        Log.i(TAG, "table friends dropped");
    }


    public void InsertFriend(GraphUser user)
    {
        SQLiteDatabase database = getWritableDatabase();
        try{
                ContentValues cv = new ContentValues();
                cv.put("id", user.getId());
                cv.put("name", user.getName());
                database.insertOrThrow("friends", null, cv);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    public void InsertFriends(List<GraphUser> users)
    {
        SQLiteDatabase database = getWritableDatabase();
        try{
            for(int i =0; i< users.size();i++){
                GraphUser user = users.get(i);
                ContentValues cv=new ContentValues();
                cv.put("id", user.getId());
                cv.put("name", user.getName());
                database.insertOrThrow("friends", null, cv);
            }
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
    }

    /**
     * Return all countries
     * @return
     */
    public ArrayList<GraphUser> getAllFriends() throws JSONException {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<GraphUser> friends = new ArrayList<GraphUser>();
        String selectQuery = "SELECT  * FROM friends";
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                JSONObject userObject = new JSONObject();
                userObject.put("id", cursor.getString(0));
                userObject.put("name", cursor.getString(1));

                GraphUser user = new ProfileUtil().convertJSONObjectToGraphUser(userObject);

                friends.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

    /**
     * Return all names based on a search string
     * we use the MATCH keyword to make use of the full text search
     * @return
     */
    public ArrayList<GraphUser>  getFriendsSearch(String query) throws JSONException {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<GraphUser> friends = new ArrayList<GraphUser>();

        String selectQuery = "SELECT * FROM friends WHERE name like  '%"+query+"%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject userObject = new JSONObject();
                userObject.put("id", cursor.getString(0));
                userObject.put("name", cursor.getString(1));

                GraphUser user = new ProfileUtil().convertJSONObjectToGraphUser(userObject);
                friends.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return friends;
    }

    /**
     * Return all names based on a search string
     * we use the MATCH keyword to make use of the full text search
     * @return
     */
    public void  removeFriends()  {
        SQLiteDatabase database = getWritableDatabase();
        try{
            database.delete("friends", null, null);
        }
        catch (Exception ex){
            Log.e(TAG, ex.getMessage());
        }
        finally {
            database.close();
        }
    }
}

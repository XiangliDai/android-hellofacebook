package com.example.hellofacebook;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.Facebook;

/**
 * Created by xdai on 11/17/13.
 */
public class CredentialManager {
    public static CredentialManager mCredentialManager;
    private static final String APP_ID = "363930458541";
    private static final String[] PERMISSIONS = new String[] {"publish_stream"};
    private Context mAppContext;
    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    private Facebook facebook ;
    private CredentialManager(Context appContext){
        this.mAppContext = appContext;
        facebook = new Facebook(APP_ID);
    }

    public static CredentialManager get(Context c){
        if (mCredentialManager == null)
            mCredentialManager = new CredentialManager(c);

        return mCredentialManager;

    }

    public Facebook getFacebook() {
        return facebook;
    }

    public boolean saveCredentials() {
        SharedPreferences.Editor editor = mAppContext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, facebook.getAccessToken());
        editor.putLong(EXPIRES, facebook.getAccessExpires());
        return editor.commit();
    }

    public boolean restoreCredentials() {
        SharedPreferences sharedPreferences = mAppContext.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
        facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
        return facebook.isSessionValid();
    }
}

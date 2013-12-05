package com.example.hellofacebook;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class ProfileActivity extends SingleFrameActivity {

    @Override
    protected SherlockFragment createFragment(){
        String userId = (String)getIntent().getSerializableExtra(FacebookProfileFragment.EXTRA_USER_ID);
        return FacebookProfileFragment.newInstance(userId);
    }

    protected void onResume(){
        super.onResume();
        getPackgeHash();
    }

    private void getPackgeHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.hellofacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}

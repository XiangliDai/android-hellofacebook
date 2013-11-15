package com.example.hellofacebook;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
public class MainActivity extends SingleFrameActivity {

    @Override
    protected Fragment createFragment(){
        return new FacebookMeFragment();
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

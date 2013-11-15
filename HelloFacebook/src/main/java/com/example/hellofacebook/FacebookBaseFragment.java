package com.example.hellofacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Created by xdai on 11/14/13.
 */
public abstract class FacebookBaseFragment extends Fragment {
    protected boolean isResumed = false;
    private UiLifecycleHelper uiHelper;
    private final static String TAG = "FacebookBaseFragment";
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        uiHelper.onStop();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    protected abstract void onSessionStateChange(Session session, SessionState state, Exception exception);

    private Session.StatusCallback callback =  new Session.StatusCallback() {
                @Override
                public void call(Session session, SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
        }
            };

}


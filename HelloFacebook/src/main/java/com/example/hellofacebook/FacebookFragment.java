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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

/**
 * A placeholder fragment containing a simple view.
 */
public  class FacebookFragment extends Fragment {
    private TextView welcome;
    private GraphUser graphUser;
    private UiLifecycleHelper uiHelper;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        makeFacebookRequst();
    }
   /* public void makeFacebookRequst() {
        Session session = Session.getActiveSession();
        Log.d("TEST", "session.getState() = " + session.getState());

        if (session.getState().isOpened()) {
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        graphUser = user;
                        updateUI();
                    }
                }
            });
        }

    }*/
 private void makeFacebookRequst(){
        // start Facebook Login
        Session.openActiveSession(getActivity(), true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {

                    // make request to the /me API
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {

                                welcome.setText("Hello " + user.getName() + "!");
                            }
                        }
                    });
                }
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        welcome = (TextView) rootView.findViewById(R.id.welcome_text);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
/*
        view.findViewById(R.id.getMeBtn).setOnClickListener(this);

        LoginButton loginBtn = (LoginButton) view.findViewById(R.id.loginBtn);
        loginBtn.setFragment(this);
        loginBtn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                graphUser = user;
            }
        });*/
    }


    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        updateUI();
    }

    private void updateUI() {
        if (graphUser != null) {
            Log.d("TEST", graphUser.getFirstName());
        }
    }


}

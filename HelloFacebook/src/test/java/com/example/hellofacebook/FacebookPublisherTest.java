package com.example.hellofacebook;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.internal.ActionBarSherlockCompat;
import com.actionbarsherlock.internal.ActionBarSherlockNative;

import android.app.Activity;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
/**
 * Created by xdai on 12/9/13.
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FacebookPublisherTest {

    @Test
    public void activity_notnull() throws Exception {
       // SingleFrameActivity fragment = Robolectric.build(
       //ActionBarSherlock.registerImplementation(ActionBarSherlock.class);
       // ActionBarSherlock.unregisterImplementation(ActionBarSherlockNative.class);
       // ActionBarSherlock.unregisterImplementation(ActionBarSherlockCompat.class);

        SherlockDialogFragment publishFragment = new FacebookPublisherFragment();

        SingleFrameActivity activity = Robolectric.buildActivity(SingleFrameActivity.class)
                .create()
                .start()
                .resume()
                .get();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(publishFragment, null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
       // View rootView = getActivity().getLayoutInflater().inflate(R.layout.facebook_publisher_fragment, null) ;
        EditText messageText = (EditText)activity.findViewById(R.id.publisher_message);

        messageText.setText("Testing Android Rocks!");

        String resultsText = messageText.getText().toString();
        assertEquals(resultsText,"Testing Android Rocks!");
    }
}

package com.example.hellofacebook;

import android.app.Activity;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;
import com.example.hellofacebook.RobolectricGradleTestRunner;
@RunWith(RobolectricGradleTestRunner.class)
public class ProfileActivityTest {

    @org.junit.Test
    public void testSomething() throws Exception {
        Activity activity = Robolectric.buildActivity(LoginActivity.class).attach().create().start().resume().get();
        assertTrue(activity != null);
    }
}

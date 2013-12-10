package com.example.hellofacebook;

import android.app.Activity;

import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
@RunWith(RobolectricGradleTestRunner.class)
public class DummyTest {

    @Test
    public void testSomething() throws Exception {

//Let's import Mockito statically so that the code looks clearer


//mock creation
        List mockedList = mock(List.class);

//using mock object
        mockedList.add("one");
        mockedList.clear();

//verification
        verify(mockedList).add("one");
        verify(mockedList).clear();

    }
}

package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EditUserTest {


    private String testAndroidId;
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        // Generate a unique test Android ID
        testAndroidId = "TEST_ANDROID_ID_" + System.currentTimeMillis();

        // Prepare an Intent with the test Android ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("TEST_ANDROID_ID", testAndroidId);

        // Launch MainActivity with the Intent
        scenario = ActivityScenario.launch(intent);

        Log.d("TestSetup", "Launched MainActivity with test Android ID: " + testAndroidId);
    }

    @Test
    public void testUserInitialization() throws InterruptedException {
        Thread.sleep(10000);
        onView(withId(R.id.entrant_switch)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        Thread.sleep(5000);
        onView(withId(R.id.edit_user_name)).perform(typeText("John Smith"));
        onView(withId(R.id.edit_user_email)).perform(typeText("jsmith@ualberta.ca"));
        onView(withId(R.id.edit_user_phone)).perform(typeText("123-456-789"));
        onView(withId(R.id.startup_button)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_user_profile)).check(matches(isDisplayed()));
        Thread.sleep(5000);

        // Go to profile page
        onView(withId(R.id.navigation_user_profile)).perform(click());
        Thread.sleep(5000);

        // Go to edit profile page
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_profile_button)).perform(click());
        Thread.sleep(5000);

        // Click photo options
        onView(withId(R.id.delete_profile_picture_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.generate_profile_picture_button)).perform(click());

        //Edit user name
        onView(withId(R.id.edit_user_name)).perform(replaceText(""));
        onView(withId(R.id.edit_user_name)).perform(typeText("Smith John"));

        // Edit user email
        onView(withId(R.id.edit_user_email)).perform(replaceText(""));
        onView(withId(R.id.edit_user_email)).perform(typeText("sjohn@ualberta.ca"));

        // Edit user phone number
        onView(withId(R.id.edit_user_phone)).perform(replaceText(""));
        onView(withId(R.id.edit_user_phone)).perform(typeText("987-654-321"));
        Thread.sleep(3000);

        // Save updated profile
        onView(withId(R.id.save_profile_edit_button)).perform(click());
        Thread.sleep(3000);

        // look at user notifications
        onView(withId(R.id.navigation_notifications)).perform(click());
        Thread.sleep(3000);

        // look at user events
        onView(withId(R.id.navigation_user_events)).perform(click());
        Thread.sleep(3000);

    }
}

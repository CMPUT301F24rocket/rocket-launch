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

import org.junit.Before;
import org.junit.Test;

public class EditOrganizerTest {

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

    /**
     * Test the initizialization and interactions of an organizer within th app
     * author: Griffin
     * - The test in the beginning will make a new user based on a created androidID. I did this
     * because I was having troubles on showing the test since the tests will always be related to the androidID
     * of the emulator (and since im already in the database I wont see the startup screen). Since the US states that
     * the androidID is used for logging in, there are times in the test cases that your androidID will take precedence
     * over the created androidID. You will be altering your actual data at some point during the tests.
     * - Will show the startup for making an organizer
     * - will then go to the profile and edit it
     * - then show all the navigation related to it
     * @throws InterruptedException if the sleep thread is interrupted
     */

    @Test
    public void testOrganizerInitialization() throws InterruptedException {
        // turn on organizer
        Thread.sleep(10000);
        onView(withId(R.id.organizer_switch)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());


        Thread.sleep(5000);
        onView(withId(R.id.edit_user_name)).perform(typeText("John Smith"));
        onView(withId(R.id.edit_user_email)).perform(typeText("jsmith@ualberta.ca"));
        onView(withId(R.id.edit_user_phone)).perform(typeText("123-456-789"));
        onView(withId(R.id.edit_user_facility)).perform(typeText("Van Vilet Centre"));
        onView(withId(R.id.edit_user_facility_address)).perform(typeText("8834 114 St NW"));
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
        onView(withId(R.id.generate_profile_picture_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.delete_profile_picture_button)).perform(click());


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
        onView(withId(R.id.navigation_create_events)).perform(click());
        Thread.sleep(3000);
    }
}

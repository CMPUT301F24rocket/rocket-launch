package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.anything;

import android.content.Intent;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

public class NewEventTest {

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
     * - Will show the startup for making an organizer and entrant
     * - will then go and make an event
     * - then show how to edit, view lists, show map, and generate QR code for the event
     * @throws InterruptedException if the sleep thread is interrupted
     */
    @Test
    public void testCreatingEvent() throws InterruptedException{

        // turn on organizer and entrant
        Thread.sleep(10000);
        onView(withId(R.id.entrant_switch)).perform(click());
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
        onView(withId(R.id.navigation_create_events)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_create_events)).perform(click());
        Thread.sleep(5000);

        // Add a new event
        onView(withId(R.id.add_new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_new_event_button)).perform(click());
        Thread.sleep(5000);

        // Add event name details (name, capacity, waitlist limit, geolocation, description)
        onView(withId(R.id.edit_event_name)).perform(typeText("Sample Event Name"));
        onView(withId(R.id.edit_event_capacity)).perform(typeText("10"));
        onView(withId(R.id.checkbox_waitlist_limit)).perform(click());
        onView(withId(R.id.edit_waitlist_limit_size)).perform(typeText("10"));
        onView(withId(R.id.checkbox_geolocation_requirement)).perform(click());
        onView(withId(R.id.edit_event_description)).perform(typeText("Sample Event Description"));
        onView(withId(R.id.create_event_button)).perform(click());
        Thread.sleep(5000);

        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.organizer_created_events_list))
                .atPosition(0)
                .perform(click());

        // Open update test event
        onView(withId(R.id.edit_event_page_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_page_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.cancel_edit_event_button)).perform(click());

        // Open entrant list
        onView(withId(R.id.view_entrant_lists_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_lists_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        // Open and show map
        onView(withId(R.id.view_entrant_map_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_map_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        // Open and show QR code
        onView(withId(R.id.view_event_qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_event_qr_code_button)).perform(click());
        onView(withId(R.id.generateCode)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

    }
}

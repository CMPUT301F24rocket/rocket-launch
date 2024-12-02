package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {

    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Test to navigate to Create Event screen and create a new event
     *
     * - Navigate to "Create Event" screen
     * - Click "Add New Event" button
     * - Enter event details: name, capacity, waitlist limit, geolocation requirement, description
     * - Submit new event by clicking "Create Event" button
     *
     * @throws InterruptedException if interrupted during test
     */

    @Test
    public void testNavigateToCreateEventFragment() throws InterruptedException {




        // Check if we can see the navigation and go to user settings
        Thread.sleep(5000);
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_user_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_user_profile)).perform(click());

        onView(withId(R.id.edit_profile_button)).perform(click());
        onView(withId(R.id.edit_user_role_button)).perform(click());

        onView(withId(R.id.entrant_switch)).check(matches(isChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());
        onView(withId(R.id.entrant_switch)).check(matches(isChecked()));

        onView(withId(R.id.save_profile_edit_button)).perform(click());
        Thread.sleep(3000);

        onView(withId(R.id.edit_user_role_button)).perform(click());
        Thread.sleep(5000);











        // Check if we can see the navigation and click create event
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
    }
}

package com.example.rocket_launch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class CreateEventTest {

    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigateToCreateEventFragment() throws InterruptedException {
        // Check if the bottom navigation is displayed
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));

        // Check if the "Create Event" button in the navigation is visible
        onView(withId(R.id.navigation_create_events)).check(matches(isDisplayed()));

        // Perform a click on "Create Event" navigation button
        onView(withId(R.id.navigation_create_events)).perform(click());

        // Introduce a 2-second delay
        Thread.sleep(5000);
        // Check if the "Add New Event" button is displayed
        onView(withId(R.id.add_new_event_button)).check(matches(isDisplayed()));

        onView(withId(R.id.add_new_event_button)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.edit_event_name)).perform(typeText("Sample Event Name"));
        onView(withId(R.id.edit_event_capacity)).perform(typeText("10"));
        onView(withId(R.id.checkbox_waitlist_limit)).perform(click());
        onView(withId(R.id.edit_waitlist_limit_size)).perform(typeText("10"));
        onView(withId(R.id.checkbox_geolocation_requirement)).perform(click());
        onView(withId(R.id.edit_event_description)).perform(typeText("Sample Event Description"));
        onView(withId(R.id.create_event_button)).perform(click());

    }
}

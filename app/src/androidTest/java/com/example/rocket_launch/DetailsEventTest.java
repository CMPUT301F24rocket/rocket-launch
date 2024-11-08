package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DetailsEventTest {

    //launch MainActivity
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Tests the navigation and functionality within the Event Details screen.
     *
     * Steps:
     * - Simulates selecting the first event in the list and entering the event details.
     * - Navigates to and interacts with:
     *     - Edit Event screen, verifying it opens and closes properly
     *     - Entrant Lists, simulating view and returning to details
     *     - Entrant Map, simulating view and returning to details
     *     - Event QR Code, displaying the code and returning to details
     *
     * @throws InterruptedException if interrupted during test
     */

    @Test
    public void testEventDetails() throws InterruptedException {

        // Click first event in event list
        Thread.sleep(3000);
        onData(anything())
                .inAdapterView(withId(R.id.organizer_created_events_list))
                .atPosition(0)
                .perform(click());

        // Open update test event (not implemented)
        onView(withId(R.id.edit_event_page_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_page_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.cancel_edit_event_button)).perform(click());

        // Open entrant list (not implemented)
        onView(withId(R.id.view_entrant_lists_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_lists_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        // Open and show map (not implemented)
        onView(withId(R.id.view_entrant_map_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_map_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        // Open and show QR code
        onView(withId(R.id.view_event_qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_event_qr_code_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

    }


}
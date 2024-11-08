package com.example.rocket_launch;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
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
public class DetailsEventTest {

    //launch MainActivity
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testEventDetails() throws InterruptedException {
        Thread.sleep(3000);

        // click first event
        onData(anything())
                .inAdapterView(withId(R.id.organizer_created_events_list))
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.edit_event_page_button)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_event_page_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.cancel_edit_event_button)).perform(click());

        onView(withId(R.id.view_entrant_lists_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_lists_button)).perform(click());
       // onView(withId(R.id.entrant_list_tab_layout)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        onView(withId(R.id.view_entrant_map_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_entrant_map_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

        onView(withId(R.id.view_event_qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_event_qr_code_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.back_button)).perform(click());

    }


}
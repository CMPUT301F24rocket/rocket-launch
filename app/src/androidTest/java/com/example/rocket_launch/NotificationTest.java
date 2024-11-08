package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationTest {

    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);


    /**
     * Test navigation to test notifcation settings
     * - Verifies navigation and opens notification settings
     * - Turns off entrant notifications, saves, and verifies settings
     * @throws InterruptedException if interrupted during test
     */

    @Test
    public void testNavigateToCreateEventFragment() throws InterruptedException {
        // Check if we can see the navigation and click notifications
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_notifications)).perform(click());
        Thread.sleep(5000);

        // Click notfications settings
        onView(withId(R.id.notification_settings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_settings_button)).perform(click());
        Thread.sleep(5000);

        // Switch off
        onView(withId(R.id.entrant_switch)).check(matches(isChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());
        onView(withId(R.id.entrant_switch)).check(matches(isNotChecked()));
        onView(withId(android.R.id.button1)).perform(click());

        // Check if saved
        onView(withId(R.id.notification_settings_button)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_settings_button)).perform(click());
        onView(withId(android.R.id.button2)).perform(click());

    }
}
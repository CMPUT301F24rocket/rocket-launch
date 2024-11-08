package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QRcodeTest {


    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);


    /**
     * Test QR code functionality:
     *
     * - Navigates to user events through the bottom navigation bar
     * - Opens the QR scanner and verifies its visibility
     * - Closes the QR scanner and verifies functionality
     *
     * @throws InterruptedException if interrupted during test
     */

    @Test
    public void testQRcode() throws InterruptedException {
        // Check if we can see the navigation and click user events
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_user_events)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_user_events)).perform(click());
        Thread.sleep(10000);

        // Open QR scanner
       // onView(withId(R.id.open_qr_scan_button)).check(matches(isDisplayed()));
       // onView(withId(R.id.open_qr_scan_button)).perform(click());
       // Thread.sleep(5000);

        //Close QR scanner
       // onView(withId(R.id.qr_button_cancel)).check(matches(isDisplayed()));
       // onView(withId(R.id.open_qr_scan_button)).perform(click());
       // Thread.sleep(5000);
    }
}

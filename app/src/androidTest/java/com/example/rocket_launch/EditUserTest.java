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
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EditUserTest {

    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testEditUser() throws InterruptedException {
        // Check if the bottom navigation is displayed
        onView(withId(R.id.bottom_nav_view)).check(matches(isDisplayed()));

        // Check if the "Create Event" button in the navigation is visible
        onView(withId(R.id.navigation_user_profile)).check(matches(isDisplayed()));

        Thread.sleep(5000);
        // Perform a click on "Create Event" navigation button
        onView(withId(R.id.navigation_user_profile)).perform(click());

        // Introduce a 2-second delay
        Thread.sleep(5000);
        // Check if the "Add New Event" button is displayed
        onView(withId(R.id.edit_profile_button)).check(matches(isDisplayed()));

        onView(withId(R.id.edit_profile_button)).perform(click());

        Thread.sleep(5000);

        onView(withId(R.id.edit_user_name)).perform(typeText("John Smith"));
        onView(withId(R.id.edit_user_email)).perform(typeText("jsmith@ualberta.ca"));
        onView(withId(R.id.edit_user_phone)).perform(typeText("123-456-789"));
        Thread.sleep(3000);

        onView(withId(R.id.edit_user_role_button)).perform(click());

        onView(withId(R.id.entrant_switch)).check(matches(isChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());
        onView(withId(R.id.entrant_switch)).check(matches(isNotChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());

        onView(withId(R.id.organizer_switch)).check(matches(isChecked()));
        onView(withId(R.id.organizer_switch)).perform(click());

    }
}

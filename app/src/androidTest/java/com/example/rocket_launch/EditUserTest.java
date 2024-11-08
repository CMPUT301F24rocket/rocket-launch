package com.example.rocket_launch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
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
public class EditUserTest {

    // Launch MainActivity for the test
    @Rule
    public ActivityScenarioRule<MainActivity> scenario =
            new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Simulates editing a user’s profile information:
     * updates name, email, phone number, facility, profile picture, and role,
     * then saves the changes
     *
     * - Navigate to edit profile
     * - Modify fields: name, email, phone number, and facility
     * - Change the profile picture
     * - Update the user role
     * - Save and verify updated info
     *
     * @throws InterruptedException if interrupted during test
     */

    @Test
    public void testEditUser() throws InterruptedException {
        // Check if the bottom navigation is displayed
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

        // Change photo in gallery (dont know backbutton)
        //onView(withId(R.id.change_profile_picture_button)).perform(click());
        //pressBack();

        // Click photo options
        onView(withId(R.id.generate_profile_picture_button)).perform(click());
        onView(withId(R.id.delete_profile_picture_button)).perform(click());

        //Edit user name 
        onView(withId(R.id.edit_user_name)).perform(replaceText(""));
        onView(withId(R.id.edit_user_name)).perform(typeText("John Smith"));

        // Edit user email
        onView(withId(R.id.edit_user_email)).perform(replaceText(""));
        onView(withId(R.id.edit_user_email)).perform(typeText("jsmith@ualberta.ca"));

        // Edit user phone number
        onView(withId(R.id.edit_user_phone)).perform(replaceText(""));
        onView(withId(R.id.edit_user_phone)).perform(typeText("123-456-789"));

        // Edit user facility
        onView(withId(R.id.edit_user_facility)).perform(replaceText(""));
        onView(withId(R.id.edit_user_facility)).perform(typeText("Yoga Studio"));
        Thread.sleep(3000);

        //Select different roles
        onView(withId(R.id.edit_user_role_button)).perform(click());
        onView(withId(R.id.entrant_switch)).check(matches(isChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());
        onView(withId(R.id.entrant_switch)).check(matches(isNotChecked()));
        onView(withId(R.id.entrant_switch)).perform(click());

        // Confirm entrant is switched
        onView(withId(R.id.organizer_switch)).check(matches(isChecked()));
        onView(withId(R.id.organizer_switch)).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // Save updated profile
        onView(withId(R.id.save_profile_edit_button)).perform(click());
        Thread.sleep(3000);
    }
}

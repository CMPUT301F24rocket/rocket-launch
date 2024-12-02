package com.example.rocket_launch.admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter used for displaying the different tabs in the admin interface.
 * Each tab corresponds to a specific admin management section.
 * Author: Pouyan
 */
public class AdminModePagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor for AdminModePagerAdapter.
     *
     * @param fragmentActivity The activity that hosts the admin tabs.
     * Author: Pouyan
     */
    public AdminModePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    /**
     * Creates and returns the appropriate fragment for the specified tab position.
     *
     * @param position The index of the selected tab.
     * @return The fragment corresponding to the tab.
     * Author: Pouyan
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Switch statement to decide which fragment to display
        switch (position) {
            case 0:
                return new AdminEventsFragment(); // Tab for managing events
            case 1:
                return new AdminProfilesFragment(); // Tab for managing profiles
            case 2:
                return new AdminImagesFragment(); // Tab for managing images
            case 3:
                return new AdminQRDataFragment(); // Tab for managing QR data
            case 4:
                return new AdminFacilitiesFragment(); // Tab for managing facilities
            default:
                return new AdminEventsFragment(); // Default to events tab if invalid position
        }
    }

    /**
     * Returns the total number of tabs available in the admin interface.
     *
     * @return The total number of admin tabs (5 in this case).
     * Author: Pouyan
     */
    @Override
    public int getItemCount() {
        return 5; // Total number of tabs (Events, Profiles, Images, QR Data, Facilities)
    }
}

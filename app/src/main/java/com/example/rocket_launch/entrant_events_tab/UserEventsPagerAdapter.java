package com.example.rocket_launch.entrant_events_tab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * adapter used for displaying waitlist and registered events tab
 */
public class UserEventsPagerAdapter extends FragmentStateAdapter {

    /**
     * constructor for UserEventsPagerAdapter
     * @param fragmentActivity
     *  activity that is holding the adapter
     */
    public UserEventsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0: return new EntrantViewWaitingListFragment();
            case 1: return  new EntrantViewRegisteredListFragment();
            default: return new EntrantViewWaitingListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

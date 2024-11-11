package com.example.rocket_launch.organizer_events_tab;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * adapter used for displaying entrant list tabs
 */
public class OrganizerEntrantListPagerAdapter extends FragmentStateAdapter {
    public OrganizerEntrantListPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new EntrantListViewWaitlistFragment();
            case 1: return new EntrantListViewInvitedFragment();
            case 2: return new EntrantListViewCancelledFragment();
            case 3: return new EntrantListViewFinalFragment();
            default: return new EntrantListViewWaitlistFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // total number of tabs
    }
}

package com.example.rocket_launch.organizer_event_details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class OrganizerEntrantListPagerAdapter extends FragmentStateAdapter {
    public OrganizerEntrantListPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new EntrantListViewAllFragment();
            case 1: return new EntrantListViewInvitedFragment();
            case 2: return new EntrantListViewCancelledFragment();
            case 3: return new EntrantListViewFinalFragment();
            default: return new EntrantListViewAllFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // total number of tabs
    }
}

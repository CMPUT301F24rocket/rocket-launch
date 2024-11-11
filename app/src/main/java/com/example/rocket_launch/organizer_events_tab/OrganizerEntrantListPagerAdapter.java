package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * adapter used for displaying entrant list tabs
 */
public class OrganizerEntrantListPagerAdapter extends FragmentStateAdapter {
    private Bundle bundle;
    public OrganizerEntrantListPagerAdapter(@NonNull FragmentActivity fragmentActivity, String eventId) {
        super(fragmentActivity);
        bundle = new Bundle();
        bundle.putString("eventId", eventId);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new EntrantListViewWaitlistFragment();
                break;
            case 1:
                fragment = new EntrantListViewInvitedFragment();
                break;
            case 2:
                fragment = new EntrantListViewCancelledFragment();
                break;
            case 3:
                fragment = new EntrantListViewFinalFragment();
                break;
            default:
                fragment = new EntrantListViewWaitlistFragment();
                break;
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4; // total number of tabs
    }
}

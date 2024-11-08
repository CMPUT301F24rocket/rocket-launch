package com.example.rocket_launch.organizer_event_details;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.rocket_launch.admin.AdminEventsFragment;
import com.example.rocket_launch.admin.AdminFacilitiesFragment;
import com.example.rocket_launch.admin.AdminImagesFragment;
import com.example.rocket_launch.admin.AdminProfilesFragment;
import com.example.rocket_launch.admin.AdminQRDataFragment;

public class OrganizerEntrantListPagerAdapter extends FragmentStateAdapter {
    public OrganizerEntrantListPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new EntrantListViewInvitedFragment();
            case 1: return new EntrantListViewCancelledFragment();
            case 2: return new EntrantListViewFinalFragment();
            default: return new EntrantListViewInvitedFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // total number of tabs
    }
}

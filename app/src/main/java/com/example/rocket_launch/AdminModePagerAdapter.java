package com.example.rocket_launch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.rocket_launch.AdminEventsFragment;

public class AdminModePagerAdapter extends FragmentStateAdapter {

    public AdminModePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AdminEventsFragment();
            case 1: return new AdminProfilesFragment();
            case 2: return new AdminImagesFragment();
            case 3: return new AdminQRDataFragment();
            case 4: return new AdminFacilitiesFragment();
            default: return new AdminEventsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5; // total number of tabs
    }
}
package com.example.rocket_launch.entrant;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserEventsPagerAdapter extends FragmentStateAdapter {


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

package com.example.rocket_launch.organizer_event_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.R;

/**
 * fragment shown when the organizer wants to show a list of entrants in the waitlist
 */
public class EntrantListViewWaitlistFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_list, container, false);
        return view;
    }
}
package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.R;

/**
 * fragment that is shown when the organizer views the list of entrants
 */
public class EntrantListViewCancelledFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_list, container, false);
        return view;
    }
}
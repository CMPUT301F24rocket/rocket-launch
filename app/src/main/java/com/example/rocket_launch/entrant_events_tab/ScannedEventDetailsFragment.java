package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.R;

/**
 * fragment to show details about a newly scanned event
 */
public class ScannedEventDetailsFragment extends Fragment {
    private Event event;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_event_details, container, false);
        return view;
    }

    /**
     * on button press navigate to the given fragment
     * @param fragment
     *  fragment to display
     */
    private void pressButton(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

}

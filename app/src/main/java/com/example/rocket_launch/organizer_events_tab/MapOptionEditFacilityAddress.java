package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.rocket_launch.R;

/**
 * Edit an Organizer Facility Address in the MapView options
 */
public class MapOptionEditFacilityAddress extends Fragment {

    //TODO: change facility address and update ViewModel with it

    public MapOptionEditFacilityAddress() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_map_option_edit_facility_address_fragment, container, false);

        ImageButton backButton = view.findViewById(R.id.edit_facility_location_back_button);
        backButton.setOnClickListener(v -> {
            closeFragment();
        });

        return view;
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }
}
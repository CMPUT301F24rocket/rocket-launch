package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.R;

public class OrganizerMapViewOptionsFragment extends Fragment {

    public OrganizerMapViewOptionsFragment() {
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
        View view = inflater.inflate(R.layout.organizer_map_view_options_fragment, container, false);

        //back button pressed
        ImageButton backButton = view.findViewById(R.id.map_options_back_button);
        backButton.setOnClickListener(v -> closeFragment());

        //edit location button
        Button editLocationButton = view.findViewById(R.id.map_options_edit_facility_location_button);
        editLocationButton.setOnClickListener(v -> );

        //collapsable options for radius menu
        LinearLayout defineRadiusOptions = view.findViewById(R.id.radius_options_header);
        LinearLayout radiusOptionsContent = view.findViewById(R.id.radius_content_layout);
        ImageView radiusOptionsDownArrow = view.findViewById(R.id.radius_options_down_arrow);

        //collapsable options for lists
        LinearLayout entrantInRangeHeader = view.findViewById(R.id.entrants_in_range_header);
        ImageView entrantInRangeDownArrow = view.findViewById(R.id.entrants_in_range_down_arrow);
        ListView entrantInRangeList = view.findViewById(R.id.list_entrants_in_defined_radius);
        LinearLayout entrantOutOfRangeHeader = view.findViewById(R.id.entrants_out_of_range_header);
        ImageView entrantOutOfRangeDownArrow = view.findViewById(R.id.entrants_out_of_range_down_arrow);
        ListView entrantOutOfRangeList = view.findViewById(R.id.list_entrants_outside_defined_radius);

        //controls for collapsable menu options
        radiusOptionControls(defineRadiusOptions, radiusOptionsContent, radiusOptionsDownArrow);
        entrantsInRangeListControls(entrantInRangeHeader, entrantInRangeList, entrantInRangeDownArrow);
        entrantsOutOfRangeListControls(entrantOutOfRangeHeader, entrantOutOfRangeList, entrantOutOfRangeDownArrow);

        return view;
    }

    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }

    private void radiusOptionControls(LinearLayout defineRadiusOptions,LinearLayout radiusOptionsContent,ImageView radiusOptionsDownArrow){
        //make radius content visible if clicked
        defineRadiusOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radiusOptionsContent.getVisibility() == View.GONE){
                    radiusOptionsContent.setVisibility(View.VISIBLE);
                    radiusOptionsDownArrow.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    radiusOptionsContent.setVisibility(View.GONE);
                    radiusOptionsDownArrow.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });
    }

    private void entrantsInRangeListControls(LinearLayout entrantInRangeHeader,ListView entrantInRangeList,ImageView entrantInRangeDownArrow){
        entrantInRangeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entrantInRangeList.getVisibility() == View.GONE){
                    entrantInRangeList.setVisibility(View.VISIBLE);
                    entrantInRangeDownArrow.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    entrantInRangeList.setVisibility(View.GONE);
                    entrantInRangeDownArrow.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });
    }

    private void entrantsOutOfRangeListControls(LinearLayout entrantOutOfRangeHeader,ListView entrantOutOfRangeList,ImageView entrantOutOfRangeDownArrow){
        entrantOutOfRangeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entrantOutOfRangeList.getVisibility() == View.GONE){
                    entrantOutOfRangeList.setVisibility(View.VISIBLE);
                    entrantOutOfRangeDownArrow.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    entrantOutOfRangeList.setVisibility(View.GONE);
                    entrantOutOfRangeDownArrow.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });
    }
}
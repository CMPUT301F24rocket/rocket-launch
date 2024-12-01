package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.rocket_launch.MapOptionsViewModel;
import com.example.rocket_launch.R;

import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fragment used for setting up additional organizer map options for the map view fragment.
 * Author: Rachel
 */

public class OrganizerMapViewOptionsFragment extends Fragment {
    private MapOptionsViewModel mapOptionsViewModel;
    private EditText radiusEditText;
    private Button enterRadiusButton;
    ListView entrantInRangeListView;
    ListView entrantOutOfRangeListView;


    public OrganizerMapViewOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapOptionsViewModel = new ViewModelProvider(requireActivity()).get(MapOptionsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_map_view_options_fragment, container, false);


        //have view model listen for any changes in facility name and update the TextView for facility name
        mapOptionsViewModel.getFacilityName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.i("Map View Options", "Facility name: " + mapOptionsViewModel.getFacilityName().getValue());

                TextView facilityNameTextView = view.findViewById(R.id.map_options_facility_name);
                facilityNameTextView.setText(mapOptionsViewModel.getFacilityName().getValue());
            }
        });


        //have view model listen for any changes in the facility address and update the TextView for facility address
        mapOptionsViewModel.getFacilityAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {

                Log.i("Map View Options", "Facility Address: " + mapOptionsViewModel.getFacilityAddress().getValue());

                TextView facilityAddressTextView = view.findViewById(R.id.map_options_facility_address);
                facilityAddressTextView.setText(mapOptionsViewModel.getFacilityAddress().getValue());
            }
        });

        //if the in range or out of range lists change, update the content and ListViews
        mapOptionsViewModel.getEntrantsInRangeList().observe(getViewLifecycleOwner(), new Observer<List<Marker>>() {
            @Override
            public void onChanged(List<Marker> markers) {
                setListContent();
                setListViews();
            }
        });

        mapOptionsViewModel.getEntrantsOutOfRangeList().observe(getViewLifecycleOwner(), new Observer<List<Marker>>() {
            @Override
            public void onChanged(List<Marker> markers) {
                setListContent();
                setListViews();
            }
        });

        //Get input for radius
        radiusEditText = view.findViewById(R.id.edit_radius);
        enterRadiusButton = view.findViewById(R.id.map_options_enter_radius_button);
        enterRadiusButton.setOnClickListener(v -> {
            defineRadius();
            setListContent();
            setListViews(); //run this whenever the defined range changes
        });

        //back button pressed
        ImageButton backButton = view.findViewById(R.id.map_options_back_button);
        backButton.setOnClickListener(v -> closeFragment());

        //edit location button
        Button editLocationButton = view.findViewById(R.id.map_options_edit_facility_location_button);
        editLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapOptionEditFacilityAddress mapOptions = new MapOptionEditFacilityAddress();
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.map_options_edit_address_container, mapOptions)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //collapsable options for radius menu
        LinearLayout defineRadiusOptions = view.findViewById(R.id.radius_options_header);
        LinearLayout radiusOptionsContent = view.findViewById(R.id.radius_content_layout);
        ImageView radiusOptionsDownArrow = view.findViewById(R.id.radius_options_down_arrow);

        //collapsable options for lists
        LinearLayout entrantInRangeHeader = view.findViewById(R.id.entrants_in_range_header);
        ImageView entrantInRangeDownArrow = view.findViewById(R.id.entrants_in_range_down_arrow);
        entrantInRangeListView = view.findViewById(R.id.list_entrants_in_defined_radius);

        LinearLayout entrantOutOfRangeHeader = view.findViewById(R.id.entrants_out_of_range_header);
        ImageView entrantOutOfRangeDownArrow = view.findViewById(R.id.entrants_out_of_range_down_arrow);
        entrantOutOfRangeListView = view.findViewById(R.id.list_entrants_outside_defined_radius);

        //controls for collapsable menu options
        radiusOptionControls(defineRadiusOptions, radiusOptionsContent, radiusOptionsDownArrow);
        entrantsInRangeListControls(entrantInRangeHeader, entrantInRangeListView, entrantInRangeDownArrow);
        entrantsOutOfRangeListControls(entrantOutOfRangeHeader, entrantOutOfRangeListView, entrantOutOfRangeDownArrow);

        return view;
    }

    /**
     * Set a list of entrants who are in and out of a user defined range
     * Run this to update the list whenever either of the range lists change content
     */
    private void setListContent(){
        List<String> entrantInRangeNames = new ArrayList<>();
        List<String> entrantOutOfRangeNames = new ArrayList<>();
        List<Marker> markerInRange = mapOptionsViewModel.getEntrantsInRangeList().getValue();
        List<Marker> markerOutOfRange = mapOptionsViewModel.getEntrantsOutOfRangeList().getValue();

        assert markerInRange!= null;
        //get names of entrants in range
        for (Marker marker: markerInRange){
            assert marker != null;
            String name = marker.getTitle();

            if (name != null){
                entrantInRangeNames.add(name);
            }
        } mapOptionsViewModel.setInRangeNames(entrantInRangeNames);

        //get names of entrants out of range
        assert markerOutOfRange != null;
        for (Marker marker: markerOutOfRange){
            String name = marker.getTitle();
            if (name != null){
                entrantOutOfRangeNames.add(name);
            }
        } mapOptionsViewModel.setOutRangeNames(entrantOutOfRangeNames);

        Log.i("SetListContent", "setListContent: in range names: " + mapOptionsViewModel.getInRangeNames().getValue());
        Log.i("SetListContent", "setListContent: out range names: " + mapOptionsViewModel.getOutRangeNames().getValue());
    }

    /**
     * Set up the list view layouts with info from the range lists
     */
    private void setListViews(){
        List<String> inRangeNames = mapOptionsViewModel.getInRangeNames().getValue();
        List<String> outRangeNames = mapOptionsViewModel.getOutRangeNames().getValue();

        ArrayAdapter<String> inRangeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, inRangeNames);
        entrantInRangeListView.setAdapter(inRangeAdapter);
        inRangeAdapter.notifyDataSetChanged();

        ArrayAdapter<String> OutOfRangeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, outRangeNames);
        entrantOutOfRangeListView.setAdapter(OutOfRangeAdapter);
        OutOfRangeAdapter.notifyDataSetChanged();

        setListViewHeightBasedOnContent(entrantInRangeListView);
        setListViewHeightBasedOnContent(entrantOutOfRangeListView);
    }

    /**
     * Take the user input for a radius as a double, to later draw a polygon representing a range from their facility
     * update mapOptionsViewModel with the radius for synchronous implementation
     */
    private void defineRadius(){
        //check if radiusEditText is not null
        String radiusText = radiusEditText.getText().toString().trim();

        if (radiusText.isEmpty()){
            //set default value to 0 and if radius is 0, don't do anything
            mapOptionsViewModel.setRadius(null);
        } else {
            try {
                Double radiusInt = Double.parseDouble(radiusText); // verifies input is int
                //make sure its bigger than 1 (at least 1km radius) and smaller than 20km (based on Edmonton & surrounding area)
                if (radiusInt >= 0.0 && radiusInt <= 20.0){ //set radius in map view model to entered radius
                    mapOptionsViewModel.setRadius(radiusInt);
                    Log.i("Define Radius", "Radius: " + mapOptionsViewModel.getRadius().getValue());
                } else {
                    radiusEditText.setError("Max 20km");
                    Log.i("Define Radius", "Radius out of range " + radiusInt);
                }
                radiusEditText.clearFocus();

            } catch (NumberFormatException e){
                radiusEditText.setError("Enter a valid Integer");
            }
        }
    }

    /**
     * CLose the Fragment
     */
    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }

    /**
     * Dropdown menu controls for radius options
     * @param defineRadiusOptions Header for the radius options
     * @param radiusOptionsContent Content for both the define radius option and range lists
     * @param radiusOptionsDownArrow Image arrow for the drop down menu
     */
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

    /**
     * Dropdown menu controls for entrantsInRange ListView
     * @param entrantInRangeHeader layout for header
     * @param entrantInRangeList layout for ListView
     * @param entrantInRangeDownArrow Image arrow for the dropdown menu
     */
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

    /**
     * Dropdown menu controls for entrantOutOfRange ListView
     * @param entrantOutOfRangeHeader layout for header
     * @param entrantOutOfRangeList layout for ListView
     * @param entrantOutOfRangeDownArrow Image arrow for the dropdown menu
     */
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

    /**
     * set list view height based on the number of content, otherwise the scroll view interferes with it
     * @param listView ListView to set height
     */
    private static void setListViewHeightBasedOnContent(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        //base layout height of a listview based on the content inside it
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
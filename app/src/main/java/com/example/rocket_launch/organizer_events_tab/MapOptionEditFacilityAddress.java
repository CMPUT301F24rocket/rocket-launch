package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.rocket_launch.MapOptionsViewModel;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Edit an Organizer Facility Address in the MapView options
 * update the database and also send info back through the view model to live update mapView
 * Author: Rachel
 */
public class MapOptionEditFacilityAddress extends Fragment {

    private MapOptionsViewModel mapOptionsViewModel;
    private UsersDB usersDB;
    private User user;
    private String androidID;
    private EditText facilityAddressEditText;

    public MapOptionEditFacilityAddress() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        usersDB = new UsersDB();
        mapOptionsViewModel = new ViewModelProvider(requireActivity()).get(MapOptionsViewModel.class);
        androidID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_map_option_edit_facility_address_fragment, container, false);

        facilityAddressEditText = view.findViewById(R.id.map_options_edit_facility_address);
        Button changeLocationButton = view.findViewById(R.id.change_facility_location_button);

        loadUserDetails();
        changeLocationButton.setOnClickListener(v -> {
            mapOptionsViewModel.setFacilityAddress(facilityAddressEditText.getText().toString());
            saveNewAddress();
        });


        //for back button
        ImageButton backButton = view.findViewById(R.id.edit_facility_location_back_button);
        backButton.setOnClickListener(v -> {
            closeFragment();
        });

        return view;
    }

    /**
     * Load user details of organizer from Firebase
     */
    private void loadUserDetails() {
        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User userData) {
                user = userData;
            }
        }, e -> Log.w("Firebase", "Error loading user", e));
    }

    /**
     * Save the new address from the edit text field and update the facilty address in the database
     */
    private void saveNewAddress(){
        user.setUserFacilityAddress(facilityAddressEditText.getText().toString());
        usersDB.updateUser(androidID,user,
                success -> {
                    Log.d("Map Option Edit Facility Address", "saveNewAddressToDB: user address updated"); closeFragment();},
                error -> Log.e("Map Option Edit Facility Address", "saveNewAddressToDB: failed to update user address", error));
    }

    /**
     * Close the fragment
     */
    private void closeFragment() {
        getParentFragmentManager().popBackStack();
    }
}
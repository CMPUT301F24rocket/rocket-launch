package com.example.rocket_launch.entrant_events_tab;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.rocket_launch.EntrantLocationData;
import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

/**
 * fragment used to show details of an event
 * Author: Kaiden
 */
public class ScannedEventDetailsFragment extends Fragment {

    String androidId;
    String eventId;
    Event event;
    EventsDB eventsdb;
    UsersDB usersDB;
    TextView eventNameView;
    TextView eventWaitlistCapacityView;
    CheckBox eventGeolocationRequired;
    TextView eventDescription;
    LinearLayout eventCapacityLayout;
    ImageButton add_event_poster_button;

    //For Location Data
    private ActivityResultLauncher<String> locationPermissionLauncher;
    EntrantLocationData entrantLocationData;
    private FusedLocationProviderClient locationProviderClient;
    boolean geolocationDataRequired;

    Button joinWaitlistButton;

    /**
     * default constructor
     */
    public ScannedEventDetailsFragment() {
        // needs to be empty
    }

    /**
     * constructor for passing an eventID
     * @param eventId   //
     *  id in database for an event
     */
    public ScannedEventDetailsFragment(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*For Location Permissions if Location Required
            Author: Rachel
            from: https://developer.android.com/training/permissions/requesting#java, accessed 2024-11-26 */
        locationPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission is granted get entrant location and add to database event location data list
                        getEntrantLocation();

                        //Join event waitlist add it to users joined waitlisted events
                        checkAndAddUser();
                        closeFragment();

                    } else {
                        //cannot join event without location enabled
                        Toast.makeText(requireContext(), "Cannot Join Waitlist: This event requires location from the entrants", Toast.LENGTH_LONG).show();
                        openLocationSettingsDialogue();
                    }
                });

        eventsdb = new EventsDB();
        usersDB = new UsersDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_event_details, container, false);

        eventNameView = view.findViewById(R.id.view_event_name);
        eventWaitlistCapacityView = view.findViewById(R.id.view_waitlist_capacity);
        eventGeolocationRequired = view.findViewById(R.id.view_checkbox_geolocation_requirement);
        eventDescription = view.findViewById(R.id.view_event_description);
        eventCapacityLayout = view.findViewById(R.id.waitlist_capacity_layout);
        add_event_poster_button = view.findViewById(R.id.add_event_poster_button);
        add_event_poster_button.setVisibility(View.GONE);


        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        joinWaitlistButton = view.findViewById(R.id.join_waitlist_button);
        view.findViewById(R.id.cancel_button).setOnClickListener(l -> {
            closeFragment();
        });
        getEvent();

        return view;
    }

    /**
     * loads an event with eventId
     */
    private void getEvent() {
        eventsdb.loadEvent(eventId, new OnSuccessListener<Event>() {
            @Override
            public void onSuccess(Event loadedEvent) {
                if (loadedEvent != null) {
                    event = loadedEvent;
                    if (event.getMaxWaitlistSize() != -1) {
                        eventCapacityLayout.setVisibility(View.VISIBLE);
                        eventWaitlistCapacityView.setText(String.format(
                                Locale.CANADA, "%d / %d", event.getWaitingList().size(),
                                event.getMaxWaitlistSize()));
                    }
                    eventNameView.setText(event.getName());
                    eventGeolocationRequired.setChecked(event.getGeolocationRequired());
                    eventDescription.setText(event.getDescription());
                    geolocationDataRequired = event.getGeolocationRequired();
                    // in get event so we cant press before we have event
                    joinWaitlistButton.setOnClickListener(l -> {
                        joinWaitlist();
                    });
                }
                else {
                    Log.d("Scan event", "event does not exist");
                    Toast.makeText(requireContext(), "Event does not exist", Toast.LENGTH_SHORT).show();
                    closeFragment();
                }
            }
        });
    }

    /**
     * function to join waiting list and update databse accordingly
     */
    private void joinWaitlist() {
        // -1 specifies that it the waitlist is unlimited
        if (event.getWaitingList().size() < event.getMaxWaitlistSize() || event.getMaxWaitlistSize() == -1) {

            if (geolocationDataRequired == true){ //Need Location Data --> Run Permissions

                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                Log.i("JOIN WAITLIST TEST", "joinWaitlist REQUIRES GEOLOCATION DATA");

            } else { //Don't need Location Data --> don't run permissions

                //check if user in waitlist already
                checkAndAddUser();
                closeFragment();
            }
        }
        else {
            Toast.makeText(requireContext(), "Waitlist Full", Toast.LENGTH_LONG).show();
            Log.d("joinWaitlist", "Waitlist is Full");
        }
    }



    /**
     * closes fragment
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    /**
     * For an event requiring geolocation from entrants, get the lat-long coordinates of the entrant at their current position upon sign-up
     * Referenced: https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient.html, Accessed 2024-11-26
     * Author: Rachel
     */
    private void getEntrantLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            entrantLocationData = new EntrantLocationData(androidId, latitude, longitude);
                            eventsdb.addToEntrantLocationDataList(eventId, entrantLocationData);

                            // Display the coordinates
                            if (isAdded()){Toast.makeText(getContext(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_LONG).show();}
                            Log.i("GET ENTRANT LOCATION", "getEntrantLocation: Latitude, Longitude " + latitude + ", " + longitude);
                            Log.i("Get Entrant Location", "entrantLocationData" + entrantLocationData);
                        } else {
                            if (isAdded()){Toast.makeText(getContext(), "Unable to fetch location.", Toast.LENGTH_SHORT).show();}
                            Log.e("Get Entrant Location", "Unable to get entrant location");
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()){Toast.makeText(getContext(), "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();}
                        Log.e("ERROR GETTING ENTRANT LOCATION", "Failed to get entrant location", e);
                    });
        }
    }

    /**
     * alert dialogue to go to android location settings if location permissions are off
     * Author: Rachel
     */
    private void openLocationSettingsDialogue(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Location Permission Required")
                .setMessage("This event requires location data enabled to join. Please enable location permissions in app settings to join event.")
                .setPositiveButton("Go to Location Settings", (dialog, i) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss())
                .show();
    }

    private void checkAndAddUser() {
        //check if user in waitlist already
        if (event.getWaitingList().contains(androidId)) {
            Toast.makeText(requireContext(), "Already in Waitlist", Toast.LENGTH_LONG).show();
            Log.d("joinWaitlist", "user already in waitlist");
        }
        else if (event.getInvitedEntrants().contains(androidId)) {
            Toast.makeText(requireContext(), "Check Notifications to Register!", Toast.LENGTH_LONG).show();
            Log.d("joinWaitlist", "user already in invited list");
        }
        else if (event.getregisteredEntrants().contains(androidId)) {
            Toast.makeText(requireContext(), "Already Registered", Toast.LENGTH_LONG).show();
            Log.d("joinWaitlist", "user already in registered list");
        }
        else if (event.getCancelledEntrants().contains(androidId)) {
            Toast.makeText(requireContext(), "Cannot join you have been cancelled", Toast.LENGTH_LONG).show();
            Log.d("joinWaitlist", "user already in cancelled list");
        }
        else {
            // add to waitlist of event
            eventsdb.addUserToWaitingList(eventId, androidId);
            // add to user's joined events
            usersDB.addWaitlistedEvent(androidId, eventId);
        }
    }
}
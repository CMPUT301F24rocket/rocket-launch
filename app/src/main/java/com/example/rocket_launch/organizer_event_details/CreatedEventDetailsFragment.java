package com.example.rocket_launch.organizer_event_details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class CreatedEventDetailsFragment extends Fragment {
    private String androidId;
    private String eventId;
    private FirebaseFirestore db;

    public CreatedEventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_created_event_details, container, false);

        androidId = Settings.Secure.getString((requireContext()).getContentResolver(), Settings.Secure.ANDROID_ID);
        eventId = getArguments().getString("eventID");
        loadEventFromID(androidId, eventId);

        //back button
        ImageButton backButton = view.findViewById(R.id.organizer_event_details_back_button);
        backButton.setOnClickListener(v -> closeFragment());

        //Button Options
        Button editEventButton = view.findViewById(R.id.edit_event_page_button);
        Button viewEntrantListsButton = view.findViewById(R.id.view_entrant_lists_button);
        Button viewEntrantMapButton = view.findViewById(R.id.view_entrant_map_button);
        Button viewEventQrCodeButton = view.findViewById(R.id.view_event_qr_code_button);

        //edit button
        editEventButton.setOnClickListener(v -> {
            OrganizerEditEventFragment organizerEditEventFragment = new OrganizerEditEventFragment();
            pressButton(organizerEditEventFragment);
        });

        //View entrant list button
        viewEntrantListsButton.setOnClickListener(v -> {
            OrganizerViewEntrantListsFragment organizerViewEntrantListsFragment = new OrganizerViewEntrantListsFragment();
            pressButton(organizerViewEntrantListsFragment);
        });

        //View Entrant Map button
        viewEntrantMapButton.setOnClickListener(v -> {
            OrganizerViewMapFragment organizerViewMapFragment = new OrganizerViewMapFragment();
            pressButton(organizerViewMapFragment);
        });

        //View QR Code Button
        viewEventQrCodeButton.setOnClickListener(v -> {
            OrganizerViewQrCodeFragment organizerViewQrCodeFragment = new OrganizerViewQrCodeFragment();
            pressButton(organizerViewQrCodeFragment);
        });

        return view;
    }


    public static CreatedEventDetailsFragment newInstance(String param1, String param2) {
        CreatedEventDetailsFragment fragment = new CreatedEventDetailsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    //Loading Event based on a organizer's event by eventID
    //use this code in following fragments to display specific information
    private void loadEventFromID(String androidId, String eventId){
        db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("organizer_events")
                .document(androidId)
                .collection("events")
                .document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                Event event = documentSnapshot.toObject(Event.class);

                if (event != null){
                    //display event name in menu
                    TextView eventName = getView().findViewById(R.id.organizer_event_details_name);
                    eventName.setText(event.getName());
                }
            } else {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Error loading event", Toast.LENGTH_SHORT).show();
        });
    }

    private void pressButton(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Close the fragment and return to the Created Activities view
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}
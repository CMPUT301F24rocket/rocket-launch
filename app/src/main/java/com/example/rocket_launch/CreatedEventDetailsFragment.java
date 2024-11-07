package com.example.rocket_launch;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

    // Close the fragment and return to the Created Activities view
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}
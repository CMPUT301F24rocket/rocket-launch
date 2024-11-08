package com.example.rocket_launch.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventArrayAdapter;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.organizer_event_details.CreatedEventDetailsFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EntrantViewRegisteredListFragment extends Fragment {
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private ListView listView;
    private EventArrayAdapter adapter;
    private ArrayList<Event> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_list, container, false);
        listView = view.findViewById(R.id.view_list_listview);
        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        events = new ArrayList<>();
        adapter = new EventArrayAdapter(requireContext(), events);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            Event clickedEvent = events.get(position);

            CreatedEventDetailsFragment clickedEventDetailsFragment = new CreatedEventDetailsFragment(clickedEvent);

            openClickedEvent(clickedEventDetailsFragment);
        });

        fetchEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchEvents();
    }

    private void openClickedEvent(CreatedEventDetailsFragment clickedEventDetailsFragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, clickedEventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void fetchEvents() {
        String androidID = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        usersDB.getUser(androidID, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                List<String> events = user.getEventsRegistered();
                if (events != null) {
                    eventsDB.getAllEventsInList(events, new OnSuccessListener<List<Event>>() {
                        @Override
                        public void onSuccess(List<Event> events) {
                            //update list adapter data with fetched events
                            EntrantViewRegisteredListFragment.this.events.clear();
                            EntrantViewRegisteredListFragment.this.events.addAll(events);
                            adapter.notifyDataSetChanged();
                        }
                    }, new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed to load events into list", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    user.setEventsCreated(new ArrayList<String>());
                }
            }
        }, e -> Log.w("Firebase", "Error getting user", e));
    }
}

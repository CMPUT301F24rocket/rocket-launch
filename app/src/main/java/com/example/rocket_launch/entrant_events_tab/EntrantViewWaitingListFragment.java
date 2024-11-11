package com.example.rocket_launch.entrant_events_tab;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventArrayAdapter;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;

import java.util.ArrayList;

/**
 * fragment used to view an event's waiting list
 */
public class EntrantViewWaitingListFragment extends Fragment {
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private ListView listView;
    private EventArrayAdapter adapter;
    private ArrayList<Event> events;
    private String androidID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidID = Settings.Secure
                .getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

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

            WaitlistedEventDetailsFragment clickedEventDetailsFragment = new WaitlistedEventDetailsFragment(clickedEvent);

            openClickedEvent(clickedEventDetailsFragment);
        });

        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(this::fetchEvents);

        fetchEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchEvents();
    }

    /**
     * displays event details fragment. For use when event is clicked
     * @param clickedEventDetailsFragment
     *  fragment to go display
     */
    private void openClickedEvent(WaitlistedEventDetailsFragment clickedEventDetailsFragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, clickedEventDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * function that fetches all events created by an organizer and loads them
     */
    private void fetchEvents(){
        // get created events and on success, get events from eventsDB
        usersDB.getWaitlistedEventTitles(androidID, eventTitleList ->
                        eventsDB.getAllEventsInList(eventTitleList, events -> {
                            EntrantViewWaitingListFragment.this.events.clear();
                            EntrantViewWaitingListFragment.this.events.addAll(events);
                            adapter.notifyDataSetChanged();
                        }, e -> {
                            Log.w("Firebase", "Error getting user", e);
                            Toast.makeText(requireContext(), "Failed to load events", Toast.LENGTH_SHORT).show();}),
                e -> Log.w("Firebase", "Error getting events title list", e));
    }
}

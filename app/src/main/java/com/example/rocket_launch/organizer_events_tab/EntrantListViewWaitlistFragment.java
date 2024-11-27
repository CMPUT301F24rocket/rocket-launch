package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UserDetailsFragment;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.entrant_events_tab.EntrantViewWaitingListFragment;
import com.example.rocket_launch.entrant_events_tab.WaitlistedEventDetailsFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * fragment shown when the organizer wants to show a list of entrants in the waitlist
 */
public class EntrantListViewWaitlistFragment extends Fragment {
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private ListView listView;
    private Event.UserArrayAdapter adapter;
    private ArrayList<User> users;
    private String eventId;
    private Button sampleButton;
    private TextView spotsAvailable;
    private Event event;
    private int availableSpots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_waitlist_layout, container, false);

        assert getArguments() != null;
        eventId = getArguments().getString("eventId");

        listView = view.findViewById(R.id.view_list_listview);
        spotsAvailable = view.findViewById(R.id.spotsAvailable);
        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        users = new ArrayList<>();
        adapter = new Event.UserArrayAdapter(requireContext(), users);

        sampleButton = view.findViewById(R.id.sampleWaitlistButton);

        listView.setAdapter(adapter);
//        listView.setOnItemClickListener((parent, itemView, position, id) -> {
//            // TODO - maybe?
//            User clickedUser = users.get(position);
//        });

        eventsDB.loadEvent(eventId, loadedEvent -> {
            if (loadedEvent!= null) {
                event = loadedEvent;

                availableSpots = event.getCapacity() -
                        (event.getInvitedEntrants().size() + event.getFinalEntrants().size());
                spotsAvailable.setText(String.format(Locale.CANADA, "%d available spots",
                        availableSpots));

                // add listener only if we successfully loaded waitlist
                sampleButton.setOnClickListener(l -> {
                    sampleWaitlist();
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsers(); // make sure we reload list in case items updated
        eventsDB.loadEvent(eventId, loadedEvent -> {
            if (loadedEvent != null) {
                event = loadedEvent;

                availableSpots = event.getCapacity() -
                        (event.getInvitedEntrants().size() + event.getFinalEntrants().size());
                spotsAvailable.setText(String.format(Locale.CANADA, "%d available spots",
                        availableSpots));
            }
        }); // reload event aswell as to show proper values
    }

    /**
     * displays event details fragment. For use when event is clicked
     * @param userDetailsFragment
     *  fragment to go display
     */
    private void openClickedEvent(UserDetailsFragment userDetailsFragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_frame, userDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * function that fetches all events created by an organizer and loads them
     */
    private void fetchUsers(){
        // get waitlist from event and on success, get users from the resulting list
        eventsDB.getWaitlistedUserIds(eventId, userIdList ->
                        usersDB.getAllUsersInList(userIdList, users -> {
                            EntrantListViewWaitlistFragment.this.users.clear();
                            EntrantListViewWaitlistFragment.this.users.addAll(users);
                            adapter.notifyDataSetChanged();

                        }, e -> {
                            Log.w("Firebase", "Error getting users", e);
                            Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                        }),
                e -> Log.w("Firebase", "Error getting events", e));
    }

    void sampleWaitlist() {
        if (availableSpots > 0) {
            List<String> sampledUsers = event.sampleWaitlist(users.size());
            eventsDB.updateEvent(eventId, event, l -> {
                Log.d("Firebase", "sample success");
                Toast.makeText(requireContext(), String.format(Locale.CANADA, "invited %d entrant(s)", users.size()), Toast.LENGTH_SHORT).show();

                // send notificatin to all sampledUsers

                fetchUsers();
            }, l -> {
                Log.d("Firebase", "sample fail :(");
            });
            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(requireContext(), "no spots available", Toast.LENGTH_SHORT).show();
        }
    }
}

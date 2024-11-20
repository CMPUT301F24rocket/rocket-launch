package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.util.ArrayList;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_list, container, false);

        assert getArguments() != null;
        eventId = getArguments().getString("eventId");

        listView = view.findViewById(R.id.view_list_listview);
        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        users = new ArrayList<>();
        adapter = new Event.UserArrayAdapter(requireContext(), users);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            // TODO - maybe?
            User clickedUser = users.get(position);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsers();
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
}

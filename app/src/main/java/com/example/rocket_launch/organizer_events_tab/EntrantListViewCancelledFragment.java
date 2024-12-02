package com.example.rocket_launch.organizer_events_tab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.NotificationCreator;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UserDetailsFragment;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.entrant_events_tab.WaitlistedEventDetailsFragment;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment that is shown when the organizer views the list of entrants
 */
public class EntrantListViewCancelledFragment extends Fragment {
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private ListView listView;
    private UserListArrayAdapter adapter;
    private ArrayList<User> users;
    private String eventId;
    private Button notifyButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_list_cancelled, container, false);

        assert getArguments() != null;
        eventId = getArguments().getString("eventId");

        listView = view.findViewById(R.id.view_list_listview);
        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        users = new ArrayList<>();
        adapter = new UserListArrayAdapter(requireContext(), users);
        listView.setAdapter(adapter);
        notifyButton = view.findViewById(R.id.sendNotification);
        notifyButton.setOnClickListener(l -> {
            // open NotificationCreator
            NotificationCreator notificationCreator = new NotificationCreator(users);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_frame, notificationCreator)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUsers();
    }


    /**
     * function that fetches all users in an event's cancelled list
     */
    private void fetchUsers(){
        // get waitlist from event and on success, get users from the resulting list
        eventsDB.getCancelledUserIds(eventId, userIdList ->
                        usersDB.getAllUsersInList(userIdList, users -> {
                            EntrantListViewCancelledFragment.this.users.clear();
                            EntrantListViewCancelledFragment.this.users.addAll(users);
                            adapter.notifyDataSetChanged();

                            if (!users.isEmpty()) {
                                notifyButton.setVisibility(View.VISIBLE);
                            }
                            }, e -> {
                            Log.w("Firebase", "Error getting users", e);
                            Toast.makeText(requireContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
                        }),
                e -> Log.w("Firebase", "Error getting events", e));
    }
}

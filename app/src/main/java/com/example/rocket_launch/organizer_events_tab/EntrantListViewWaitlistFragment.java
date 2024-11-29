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
import com.example.rocket_launch.Notification;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UserDetailsFragment;
import com.example.rocket_launch.UsersDB;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * fragment shown when the organizer wants to show a list of entrants in the waitlist
 * Authors: kaiden
 */
public class EntrantListViewWaitlistFragment extends Fragment {
    private EventsDB eventsDB;
    private UsersDB usersDB;
    private Event event;
    private Event.UserArrayAdapter adapter;
    private ArrayList<User> users;
    private String eventId;
    private int availableSpots;
    private int sampleAmount;
    private int replaceAmount;

    // UI
    private ListView listView;
    private TextView spotsAvailable;
    private Button notifyButton;
    private Button sampleButton;
    private Button replaceButton;

    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organizer_view_waitlist_layout, container, false);

        assert getArguments() != null;
        eventId = getArguments().getString("eventId");

        eventsDB = new EventsDB();
        usersDB = new UsersDB();
        users = new ArrayList<>();
        
        listView = view.findViewById(R.id.view_list_listview); // Users list
        adapter = new Event.UserArrayAdapter(requireContext(), users);
        listView.setAdapter(adapter);

        spotsAvailable = view.findViewById(R.id.spotsAvailable); // text above list
        
        // buttons
        sampleButton = view.findViewById(R.id.sampleWaitlistButton);
        replaceButton = view.findViewById(R.id.replaceButton);
        notifyButton = view.findViewById(R.id.sendNotification);

        notifyButton.setOnClickListener(l -> {
            // TODO - send notification to all
        });
        sampleButton.setOnClickListener(l -> {
            sampleWaitlist(sampleAmount, sampledUsers -> {
                // send notifications to all sampledUsers saying they were chosen
                Notification inviteNotification = new Notification();
                String inviteTitle = String.format(Locale.CANADA, "You are Invited to Join %s", event.getName());
                inviteNotification.createInvite(java.util.UUID.randomUUID().toString(), inviteTitle, eventId);
                for (String userId : sampledUsers) {
                    usersDB.addNotification(userId, inviteNotification);
                }

                // send notifications to all in waitlist saying they were not chosen
                String title = String.format(Locale.CANADA, "You were not chosen for %s", event.getName());
                String message = "Keep an eye out for any redraws";
                Notification declineNotification = new Notification(java.util.UUID.randomUUID().toString(), title, message);
                for (String userId : event.getWaitingList()) {
                    usersDB.addNotification(userId, declineNotification);
                }
            });
        });
        replaceButton.setOnClickListener(l -> {
            sampleWaitlist(replaceAmount, sampledUsers -> {
                // send notifications to all sampledUsers saying they were chosen in a redraw
                Notification inviteNotification = new Notification();
                String title = String.format(Locale.CANADA, "You are invited to join %s in a redraw!", event.getName());
                inviteNotification.createInvite(java.util.UUID.randomUUID().toString(), title, eventId);
            });
        });


        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            // TODO - do something on clicking a user
            User clickedUser = users.get(position);
        });


        eventsDB.loadEvent(eventId, loadedEvent -> {
            if (loadedEvent!= null) {
                event = loadedEvent;

                updateUI();

            } else {
                Log.e("WaitingList fragment", "invalid event");
            }
        });


        return view;
    }

    void updateUI() {
        if (event.getWaitingList().isEmpty()) {
            notifyButton.setVisibility(View.GONE);
        }
        // amount of space available total
        availableSpots = event.getCapacity() -
                (event.getInvitedEntrants().size() + event.getFinalEntrants().size());
        // set text for amount of spots available in total
        spotsAvailable.setText(String.format(Locale.CANADA, "%d available spots", availableSpots));

        // takes into account if waitlist does not have that many
        int realAvailableSpots = Math.min(event.getWaitingList().size(), availableSpots);

        if (realAvailableSpots < event.getCancelledEntrants().size()) {
            replaceAmount = realAvailableSpots; // replacement amount is all the spots
            sampleAmount = 0; // implies sampleAmount = 0
        }
        else {
            replaceAmount = event.getCancelledEntrants().size(); //
            sampleAmount = realAvailableSpots - replaceAmount;
        }
        if (sampleAmount > 0) {
            // set text on button
            sampleButton.setText(String.format(Locale.CANADA, "Sample %d Users", sampleAmount));
        } else {
            sampleButton.setVisibility(View.GONE);
        }

        if (replaceAmount > 0) {
            // set text on button
            replaceButton.setText(String.format(Locale.CANADA, "Replace %d Users", replaceAmount));
        } else {
            replaceButton.setVisibility(View.GONE);
        }
    }

    /**
     * each time we resume or load this fragment we want to use fresh data
     */
    @Override
    public void onResume() {
        super.onResume();
        fetchUsers(); // make sure we reload list in case items updated
        eventsDB.loadEvent(eventId, loadedEvent -> {
            event = loadedEvent;
            if (event != null) {
                availableSpots = event.getCapacity() -
                        (event.getInvitedEntrants().size() + event.getFinalEntrants().size());
                spotsAvailable.setText(String.format(Locale.CANADA, "%d available spots in Event",
                        availableSpots));
            }
        }); // reload event as well as to show proper values
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

        /**
     * samples a certain amount of spots form the waitlist
     * @param spots
     *  amount of people to sponsor
     * @param onSuccess
     *  used mainly for notifying users which are passed to this function in a list
     */
    void sampleWaitlist(int spots, OnSuccessListener<List<String>> onSuccess) {
        if (spots > 0) {
            List<String> sampledUsers = event.sampleWaitlist(spots);
            eventsDB.updateEvent(eventId, event, l -> {
                Toast.makeText(requireContext(), String.format(Locale.CANADA, "invited %d entrant(s)", spots), Toast.LENGTH_SHORT).show();

                onSuccess.onSuccess(sampledUsers);
                fetchUsers();
                updateUI();

            }, l -> Log.d("Firebase", "sample fail :("));
            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(requireContext(), "no spots available", Toast.LENGTH_SHORT).show();
        }
    }
}

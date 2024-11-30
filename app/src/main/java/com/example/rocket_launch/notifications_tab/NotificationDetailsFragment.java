package com.example.rocket_launch.notifications_tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.Notification;
import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;

public class NotificationDetailsFragment extends Fragment {
    public Notification notification;

    public NotificationDetailsFragment() {}
    public NotificationDetailsFragment(Notification notification) {
        this.notification = notification;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        // get arguments
        Bundle args = getArguments();
        // String from = args != null ? args.getString("from") : "Unknown Sender"; // no sender data rn
        String title = args != null ? args.getString("title") : "No Title";
        String message = args != null ? args.getString("message") : "No Message";
        boolean isInvitation = args != null && args.getBoolean("isInvitation", false);
        String eventID = args != null ? args.getString("eventID") : null;
        String androidID = args != null ? args.getString("androidID") : null;

        // get UI elements
        TextView titleTextView = view.findViewById(R.id.title_content);
        TextView messageTextView = view.findViewById(R.id.message_content);
        LinearLayout confirmationButtons = view.findViewById(R.id.confirmation_buttons);
        Button acceptButton = view.findViewById(R.id.button_accept);
        Button declineButton = view.findViewById(R.id.button_decline);
        ImageView backButton = view.findViewById(R.id.back_button);

        // populate UI elements
        titleTextView.setText(title);
        messageTextView.setText(message);

        // back button to notification fragment
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // show buttons if invite type notification
        if (isInvitation) {
            confirmationButtons.setVisibility(View.VISIBLE);

            //accept invitation
            acceptButton.setOnClickListener(v -> {
                AcceptInvitation(eventID, androidID);
                requireActivity().getSupportFragmentManager().popBackStack();
            });
            // decline invitation
            declineButton.setOnClickListener(v -> {
                DeclineInvitation(eventID, androidID);
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        } else { // regular notification type
            confirmationButtons.setVisibility(View.GONE);
        }

        return view;
    }


    private void AcceptInvitation(String eventID, String androidID) {
        //remove from invite and add to registered event
        UsersDB usersDB = new UsersDB();
        EventsDB eventsDB = new EventsDB();
        // add to a user's registered events
        usersDB.addRegisteredEvent(androidID, eventID);

        // remove from event invite list and add to final
        eventsDB.removeUserFromInvitedList(eventID, androidID);
        eventsDB.addUserToRegisteredList(eventID, androidID);

        // remove notification from user
        usersDB.removeNotification(androidID, notification);
    }


    private void DeclineInvitation(String eventID, String androidID){
        EventsDB eventsDB = new EventsDB();
        UsersDB usersDB = new UsersDB();

        // remove from event invite list and add to cancelled
        eventsDB.removeUserFromInvitedList(eventID, androidID);
        eventsDB.addUserToCancelledList(eventID, androidID);

        // remove notification from user
        usersDB.removeNotification(androidID, notification);
    }
}




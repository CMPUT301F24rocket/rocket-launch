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

import com.example.rocket_launch.R;
import com.example.rocket_launch.UsersDB;

public class NotificationDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        // get arguments
        Bundle args = getArguments();
        String from = args != null ? args.getString("from") : "Unknown Sender";
        String message = args != null ? args.getString("message") : "No Message";
        boolean isInvitation = args != null && args.getBoolean("isInvitation", false);
        String eventID = args != null ? args.getString("eventID") : null;
        String androidID = args != null ? args.getString("androidID") : null;

        // get UI elements
        TextView fromTextView = view.findViewById(R.id.from_event_name);
        TextView messageTextView = view.findViewById(R.id.message_content);
        LinearLayout confirmationButtons = view.findViewById(R.id.confirmation_buttons);
        Button acceptButton = view.findViewById(R.id.button_accept);
        Button declineButton = view.findViewById(R.id.button_decline);
        ImageView backButton = view.findViewById(R.id.back_button);

        // populate UI elements
        fromTextView.setText(from);
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
        //remove from waitlist and add to registered event
        UsersDB usersDB = new UsersDB();
        usersDB.removeWaitlistedEvent(androidID, eventID);
        usersDB.addRegisteredEvent(androidID, eventID);
    }


    private void DeclineInvitation(String eventID, String androidID){
        //remove from waitlist and add to cancelled list
        UsersDB usersDB = new UsersDB();
        usersDB.removeWaitlistedEvent(androidID, eventID);
        usersDB.addCancelledEvent(androidID, eventID);
    }
}




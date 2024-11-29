package com.example.rocket_launch.notifications_tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.R;

public class NotificationDetailsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances){
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        // get arguments
        Bundle args = getArguments();
        String from = args != null ? args.getString("from") : "Unknown Sender";
        String message = args!= null ? args.getString("message") : "No Message";
        boolean isInvitation = args!= null && args.getBoolean("isInvitation", false);

        



        // get UI elements
        TextView fromTextView = view.findViewById(R.id.from_event_name);
        TextView messageTextView = view.findViewById(R.id.message_content);
        LinearLayout confirmationButtons = view.findViewById(R.id.confirmation_buttons);
        Button acceptButton = view.findViewById(R.id.button_accept);
        Button declineButton = view.findViewById(R.id.button_decline);
        Button backButton = view.findViewById(R.id.back_button);


        // populate UI elements
        fromTextView.setText(from);
        messageTextView.setText(message);

        confirmationButtons.setVisibility(isInvitation ? View.VISIBLE : View.GONE);

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;


    }

}

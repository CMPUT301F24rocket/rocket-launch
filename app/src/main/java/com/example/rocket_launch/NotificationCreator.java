package com.example.rocket_launch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Locale;

/**
 * fragment to allow an organizer to create and edit contents of notification
 * Author: kaiden
 */
public class NotificationCreator extends Fragment {
    private EditText title;
    private EditText message;
    private Button sendButton;
    private Button cancelButton;
    private List<User> users;

    /**
     * empty constructor for fragment requirement
     */
    public NotificationCreator() {}

    /**
     * constructor used to pass a list of users
     * Author: Kaidenf
     * @param users
     *  list of users to send a notification to
     */
    public NotificationCreator(List<User> users) {
        this.users = users;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_create, container, false);

        // load UI
        title = view.findViewById(R.id.editTitle);
        message = view.findViewById(R.id.editMessage);
        sendButton = view.findViewById(R.id.sendButton);
        cancelButton = view.findViewById(R.id.cancelButton);

        sendButton.setOnClickListener(l -> {
            sendNotification();
        });

        cancelButton.setOnClickListener(l -> {
            closeFragment();
        });

        return view;
    }

    /**
     * verifies data and send a notification to all users in provided list
     * Author: Kaiden
     */
    private void sendNotification() {
        String titleText = title.getText().toString();
        String messageText = message.getText().toString();

        boolean validEntry = true;

        // make sure title is not empty
        if (titleText.isEmpty()) {
            title.setError("title cannot be empty");
            validEntry = false;
        }
        // make sure message is not empty
        if (messageText.isEmpty()) {
            message.setError("message cannot be empty");
            validEntry = false;
        }
        if (validEntry) {
            // create Notification
            Notification notification = new Notification(
                    java.util.UUID.randomUUID().toString(), titleText, messageText);

            // try to send to all users in list
            int notificationCounter = 0; // to display how many were sent
            for (User user : users) {
                // if they want to receive notifications, send notification
                if (user.getNotificationPreferences()) {
                    NotificationHelper.sendPrefabNotification(user.getAndroidId(), notification);
                    notificationCounter += 1;
                }
            }
            Toast.makeText(requireContext(), String.format(Locale.CANADA, "Sent notification to %d users", notificationCounter), Toast.LENGTH_SHORT).show();
            closeFragment();
        }
    }

    /**
     * closes the given fragment by doing popBackStack
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}

package com.example.rocket_launch.nav_fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.Notification;
import com.example.rocket_launch.R;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.notifications_tab.NotificationDetailsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment used to display all of a user's notifications
 * Author: Rachel
 */
public class NotificationsFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";

    private ListView notificationsListView;
    private ArrayAdapter<String> notificationsAdapter;
    private List<String> notificationList;

    private User user;

    private UsersDB usersDB;
    private String androidId;

    private FloatingActionButton notificationSettingsButton;

    // set what to do on result
    private final ActivityResultLauncher<Intent> settingsLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Log.d("SettingsResult", "Returned from settings");
                updateNotificationPreferences();
            });

    /**
     * default constructor
     */
    public NotificationsFragment(){
        // we are required to have (an) empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        usersDB = new UsersDB();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        notificationSettingsButton = view.findViewById(R.id.notification_settings_button);
        notificationSettingsButton.setOnClickListener(v -> {

            // open app notification settings

            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // For Android 8.0 (Oreo) and above
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                intent.setData(uri);
            }
            settingsLauncher.launch(intent);
        });

        notificationsListView = view.findViewById(R.id.notifications_list_view);
        notificationList = new ArrayList<>();

        notificationsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationList);
        notificationsListView.setAdapter(notificationsAdapter);

        notificationsListView.setOnItemClickListener((parent, itemView, position, id) -> {
            // get position from the back because of reversed list
            Notification selectedNotification = user.getNotifications().get(user.getNotifications().size() - 1 - position);
            NotificationDetailsFragment detailsFragment = new NotificationDetailsFragment(selectedNotification);

            Bundle args = new Bundle();

            args.putString("androidID", user.getAndroidId());
            args.putString("title", selectedNotification.getTitle());
            args.putString("message", selectedNotification.getMessage());

            if (selectedNotification.getInvitation() != null && selectedNotification.getInvitation()) {
                args.putBoolean("isInvitation", true);
                args.putString("eventID", selectedNotification.getEventId());
            } else {
                args.putBoolean("isInvitation", false);
            }
            detailsFragment.setArguments(args);

            // go to NotificationDetailsFragment
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_frame, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });
        loadNotifications();
        return view;
    }

    /**
     * Function used to update the notification preferences of the user. The
     * user can choose to have them on or off
     */
    private void updateNotificationPreferences() {
        boolean notificationPreferences = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled();
        Log.d("NotificationPreferences", "Notifications enabled: " + notificationPreferences);

        // Update Firestore directly
        new UsersDB().getUsersRef().document(androidId)
                .update("notificationPreferences", notificationPreferences)
                .addOnSuccessListener(aVoid -> Log.d("NotificationPreferences", "Updated successfully in Firestore."))
                .addOnFailureListener(e -> Log.e("NotificationPreferences", "Failed to update preferences in Firestore.", e));
    }

    /**
     * function used to load and display all notifications
     */
    private void loadNotifications() {
        usersDB.getUser(androidId, newUser -> {
            user = newUser;

            // Clear current list and load new notifications
            notificationList.clear();

            // Check and iterate over user notifications (neweset first)
            if (user.getNotifications() != null) {
                for (int i = user.getNotifications().size() - 1; i >= 0; i--) {
                    // Concatenate title (message will display when clicked)
                    notificationList.add(user.getNotifications().get(i).getTitle());
                }
            }

            // Notify adapter of data change
            notificationsAdapter.notifyDataSetChanged();
        }, e -> Log.e(TAG, "Error fetching user", e));
    }
}


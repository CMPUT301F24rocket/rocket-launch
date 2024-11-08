package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.util.Log;
import android.provider.Settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import android.provider.Settings.Secure;

import com.example.rocket_launch.NotificationPreferencesFragment;
import com.example.rocket_launch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

public class NotificationsFragment extends Fragment {

    private ListView notificationsListView;
    private ArrayAdapter<String> notificationsAdapter;
    private List<String> notificationList;

    private User user;

    private static final String TAG = "NotificationsFragment";
    private FirebaseFirestore db;
    private UsersDB usersDB;
    private String androidId;
    private List notifications;

    private FloatingActionButton notificationSettingsButton;

    public NotificationsFragment(){
        // we are required to have (an) empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        db = FirebaseFirestore.getInstance();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        notificationSettingsButton = view.findViewById(R.id.notification_settings_button);
        notificationSettingsButton.setOnClickListener(v -> {
            NotificationPreferencesFragment notifPreferences = new NotificationPreferencesFragment(user.getNotificationPreferences(), usersDB.getUsersRef().document(androidId));
            notifPreferences.setOnSuccessListener(new NotificationPreferencesFragment.onSuccessListener() {
                @Override
                public void onSuccess() {
                    // this is def inefficient but it works
                    loadNotifications();
                }
            });
            notifPreferences.show(getParentFragmentManager(), "edit notifs");
        });

        notificationsListView = view.findViewById(R.id.notifications_list_view);
        notificationList = new ArrayList<>();

        notificationsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, notificationList);
        notificationsListView.setAdapter(notificationsAdapter);

        loadNotifications();

        return view;

    }

    private void loadNotifications() {

        usersDB = new UsersDB();
        // reference https://stackoverflow.com/questions/16869482/how-to-get-unique-device-hardware-id-in-android
        String androidID = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

        usersDB.getUser(androidID, documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);

            assert user != null;
            List<String> notifications = user.getNotifications();
            if (notifications != null) {
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationsAdapter.notifyDataSetChanged();
            } else {
                Log.d("NotificationFragment", "No notifications found");
            }

        }, e -> {
            // Handle the failure case
            Log.e("NotificationFragment", "Error fetching user", e);
        }); // <-- Closing parenthesis and semicolon added here

    }
}
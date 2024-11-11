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

import com.example.rocket_launch.notifications_tab.NotificationPreferencesFragment;
import com.example.rocket_launch.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.rocket_launch.User;
import com.example.rocket_launch.UsersDB;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * fragment used to display all of a user's notifications
 */
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

    /**
     * function used to load and display all notifications
     */
    private void loadNotifications() {

        usersDB = new UsersDB();
        // reference https://stackoverflow.com/questions/16869482/how-to-get-unique-device-hardware-id-in-android
        String androidID = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);

        usersDB.getUser(androidID, new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User newUser) {
                user = newUser;
                List<String> notifications = user.getNotifications();
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationsAdapter.notifyDataSetChanged();

            }
        }, e -> Log.e("NotificationFragment", "Error fetching user", e));
    }
}
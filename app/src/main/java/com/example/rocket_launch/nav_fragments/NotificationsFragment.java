package com.example.rocket_launch.nav_fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.Notification;
import com.example.rocket_launch.NotificationArrayAdapter;
import com.example.rocket_launch.NotificationPreferencesFragment;
import com.example.rocket_launch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class NotificationsFragment extends Fragment {
    private static final String TAG = "NotificationsFragment";
    private FirebaseFirestore db;
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

        notificationSettingsButton = view.findViewById(R.id.notification_settings_button);
        notificationSettingsButton.setOnClickListener(v -> {
            new NotificationPreferencesFragment().show(getParentFragmentManager(), "edit notifs");
        });

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        androidId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        getNotifications(); // get all notifications from firebase


        //TODO: Display list of user' notifications
        // Click on notification in list and open in large format

    }

    private void getNotifications() {
//        notifications = new List<Notification>;
    }
}
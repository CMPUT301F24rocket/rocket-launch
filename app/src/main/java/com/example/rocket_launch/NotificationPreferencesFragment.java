package com.example.rocket_launch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;

public class NotificationPreferencesFragment  extends DialogFragment {
    DocumentReference userRef;
    private UserNotificationPreferences preferences;

    public NotificationPreferencesFragment(UserNotificationPreferences preferences, DocumentReference userRef) {
        this.preferences = preferences;
        this.userRef = userRef;
    }

    public interface onSuccessListener {
        void onSuccess();
    }
    private NotificationPreferencesFragment.onSuccessListener listener;

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.notification_preferences_fragment, null);

        SwitchCompat whenChosenSwitch = view.findViewById(R.id.chosen_notification_switch);
        SwitchCompat whenNotChosenSwitch = view.findViewById(R.id.not_chosen_notification_switch);
        SwitchCompat optOutSwitch = view.findViewById(R.id.opt_out_notification_switch);

        if (preferences == null) {
            preferences = new UserNotificationPreferences();
        }

        whenChosenSwitch.setChecked(preferences.isReceiveOnChosen());
        whenNotChosenSwitch.setChecked(preferences.isReceiveOnNotChosen());
        optOutSwitch.setChecked(preferences.isGeneraloptOut());



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Notification Preferences")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    preferences.setReceiveOnNotChosen(whenChosenSwitch.isChecked());
                    preferences.setReceiveOnNotChosen(whenNotChosenSwitch.isChecked());
                    preferences.setGeneraloptOut(optOutSwitch.isChecked());

                    if (userRef != null) {
                        userRef.update("notificationPreferences", preferences);
                    }
                })
                .create();

    }

    public void setOnSuccessListener(NotificationPreferencesFragment.onSuccessListener listener) {
        this.listener = listener;
    }
}

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
    private DocumentReference userRef;
    private boolean preferences;

    public NotificationPreferencesFragment(Boolean preferences, DocumentReference userRef) {
        if (preferences == null) {
            this.preferences = true;
        }
        else {
            this.preferences = preferences;
        }
        this.userRef = userRef;
    }

    public interface onSuccessListener {
        void onSuccess();
    }
    private NotificationPreferencesFragment.onSuccessListener listener;

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.notification_preferences_fragment, null);

        SwitchCompat optOutSwitch = view.findViewById(R.id.opt_out_notification_switch);

        optOutSwitch.setChecked(preferences);



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Notification Preferences")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    preferences = optOutSwitch.isChecked();

                    if (userRef != null) {
                        userRef.update("notificationPreferences", preferences);
                    }
                    listener.onSuccess();
                })
                .create();

    }

    public void setOnSuccessListener(NotificationPreferencesFragment.onSuccessListener listener) {
        this.listener = listener;
    }
}

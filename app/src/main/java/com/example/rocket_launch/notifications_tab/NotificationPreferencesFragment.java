package com.example.rocket_launch.notifications_tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.example.rocket_launch.R;
import com.google.firebase.firestore.DocumentReference;

/**
 * fragment shown when the user wants to edit notification preferences
 */
public class NotificationPreferencesFragment  extends DialogFragment {
    private DocumentReference userRef;
    private boolean preferences;

    /**
     * constructor
     * @param preferences
     *  current preferences
     * @param userRef
     *  reference to user database
     */
    public NotificationPreferencesFragment(Boolean preferences, DocumentReference userRef) {
        if (preferences == null) {
            this.preferences = true;
        }
        else {
            this.preferences = preferences;
        }
        this.userRef = userRef;
    }

    /**
     * used for callback
     */
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

    /**
     * function to set the onSuccessListener
     * @param listener
     *  listener to be set
     */
    public void setOnSuccessListener(NotificationPreferencesFragment.onSuccessListener listener) {
        this.listener = listener;
    }
}

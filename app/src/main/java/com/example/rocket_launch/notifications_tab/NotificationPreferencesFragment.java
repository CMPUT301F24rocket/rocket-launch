package com.example.rocket_launch.notifications_tab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.example.rocket_launch.R;
import com.google.firebase.firestore.DocumentReference;

public class NotificationPreferencesFragment extends DialogFragment {
    private boolean preferences;
    private DocumentReference userRef;

    /**
     * Constructor
     * @param preferences Current preferences
     * @param userRef Reference to user database
     */
    public NotificationPreferencesFragment(Boolean preferences, DocumentReference userRef) {
        this.preferences = preferences != null ? preferences : true;
        this.userRef = userRef;
    }

    /**
     * Used for callback
     */
    public interface OnSuccessListener {
        void onSuccess();
    }

    private OnSuccessListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.notification_preferences_fragment, null);

        SwitchCompat optOutSwitch = view.findViewById(R.id.opt_out_notification_switch);
        optOutSwitch.setChecked(preferences);

        optOutSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences = isChecked;
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        return builder
                .setView(view)
                .setTitle("Notification Preferences")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Save the preference
                    if (userRef != null) {
                        userRef.update("notificationPreferences", preferences);
                    }
                    // Inform the user if notifications are disabled at the system level
                    if (preferences && !areSystemNotificationsEnabled()) {
                        showSystemSettingsDialog();
                    } else {
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    }
                })
                .create();
    }

    /**
     * Check if notifications are enabled in the system settings
     */
    private boolean areSystemNotificationsEnabled() {
        return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled();
    }

    /**
     * Show dialog to guide the user to system settings
     */
    private void showSystemSettingsDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Enable Notifications")
                .setMessage("Notifications are disabled in your system settings. Please enable them to receive notifications.")
                .setPositiveButton("Open Settings", (dialog, which) -> openSystemNotificationSettings())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Open the system notification settings for your app
     */
    private void openSystemNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        }
        startActivity(intent);
    }

    /**
     * Set the OnSuccessListener
     * @param listener Listener to be set
     */
    public void setOnSuccessListener(OnSuccessListener listener) {
        this.listener = listener;
    }
}

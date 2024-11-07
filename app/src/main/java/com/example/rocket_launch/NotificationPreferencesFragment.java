package com.example.rocket_launch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NotificationPreferencesFragment  extends DialogFragment {
    NotificationPreferencesFragment() {

    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.select_role_fragment, null);



        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Select Role")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {

                })
                .create();

    }
}

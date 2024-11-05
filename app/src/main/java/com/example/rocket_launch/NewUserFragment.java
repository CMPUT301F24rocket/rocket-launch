package com.example.rocket_launch;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewUserFragment  extends DialogFragment {
    Roles roles;

    NewUserFragment(Roles roles) {
        this.roles = new Roles();
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.select_role_fragment, null);

        SwitchCompat entrant_switch = view.findViewById(R.id.entrant_switch);
        SwitchCompat organizer_switch = view.findViewById(R.id.organizer_switch);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Select Role")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    roles.setEntrant(entrant_switch.isChecked());
                    roles.setOrganizer(organizer_switch.isChecked());
                })
                .create();
    }
}

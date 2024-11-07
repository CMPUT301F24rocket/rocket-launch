package com.example.rocket_launch;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;

public class SelectRolesFragment extends DialogFragment {
    private Roles roles;
    private DocumentReference userRef;

    SelectRolesFragment(Roles roles, DocumentReference userRef) {
        this.roles = roles;
        this.userRef = userRef;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.select_role_fragment, null);

        SwitchCompat entrant_switch = view.findViewById(R.id.entrant_switch);
        SwitchCompat organizer_switch = view.findViewById(R.id.organizer_switch);

        entrant_switch.setChecked(roles.isEntrant());
        organizer_switch.setChecked(roles.isOrganizer());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Select Role")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> {
                    roles.setEntrant(entrant_switch.isChecked());
                    roles.setOrganizer(organizer_switch.isChecked());
                    if (userRef != null) {
                        userRef.update("roles", roles);

                        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav_view);
                        if (bottomNav != null) {
                            Menu menu = bottomNav.getMenu();

                            if (!roles.isEntrant()) {
                                // if not entrant, don't show user events
                                menu.findItem(R.id.navigation_user_events).setVisible(false);
                            }
                            else {
                                menu.findItem(R.id.navigation_user_events).setVisible(true);
                            }
                            if (!roles.isOrganizer()) {
                                // if not organizer don't show create events
                                menu.findItem(R.id.navigation_create_events).setVisible(false);
                            }
                            else {
                                menu.findItem(R.id.navigation_create_events).setVisible(true);
                            }
                        }
                    }
                })
                .create();
    }
}

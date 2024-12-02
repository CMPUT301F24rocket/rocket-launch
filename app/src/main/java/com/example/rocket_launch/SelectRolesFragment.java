package com.example.rocket_launch;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

/**
 * fragment displayed when a user wants to edit their roles
 * Author: Kaiden
 * Contributors: Rachel
 */
public class SelectRolesFragment extends DialogFragment {
    private Roles roles;
    private UsersDB usersDB;
    private String androidID;

    /**
     * constructor
     * @param roles
     *  current roles
     */
    SelectRolesFragment(Roles roles) {
        this.roles = roles;
        usersDB = new UsersDB();
    }

    /**
     * interface for callback
     */
    public interface onSuccessListener {
        void onSuccess(Roles roles);
    }
    private onSuccessListener listener;

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.select_role_fragment, null);

        SwitchCompat entrant_switch = view.findViewById(R.id.entrant_switch);
        SwitchCompat organizer_switch = view.findViewById(R.id.organizer_switch);

        entrant_switch.setChecked(roles.isEntrant());
        organizer_switch.setChecked(roles.isOrganizer());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setTitle("Select Role")
                //.setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null)
                .create();

        //prevent positiveButton click if  no role is picked
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            positiveButton.setOnClickListener(v -> {
                if (entrant_switch.isChecked() || organizer_switch.isChecked()){
                    roles.setEntrant(entrant_switch.isChecked());
                    roles.setOrganizer(organizer_switch.isChecked());

                    BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav_view);

                    if (bottomNav != null) {
                        Menu menu = bottomNav.getMenu();
                        // update navbar
                        menu.findItem(R.id.navigation_user_events).setVisible(roles.isEntrant());
                        menu.findItem(R.id.navigation_create_events).setVisible(roles.isOrganizer());
                    }
                    listener.onSuccess(roles);
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), "Please select at least one role", Toast.LENGTH_SHORT).show();}
            });
        });
        return dialog;

    }

    /**
     * sets the onSuccessListener to a given listener
     * @param listener
     *  listener to be set to
     */
    public void setOnSuccessListener(onSuccessListener listener) {
        this.listener = listener;
    }
}
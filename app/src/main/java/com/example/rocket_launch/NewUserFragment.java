package com.example.rocket_launch;

import androidx.fragment.app.DialogFragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewUserFragment  extends DialogFragment {
    User user;

    NewUserFragment(User user) {
        this.user = user;
    }

    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.select_role_fragment, null);
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Select Role")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", null) // we override so we can error check
                .create();

        // customize listener for ok button to validate data
        dialog.setOnShowListener(dialogInterface -> {
            Button ok_button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            ok_button.setOnClickListener(v -> {
                // get data from view

                // validate the data
                boolean valid = true;
                // set valid to false if anything not true

                if (valid) {
                    dialog.dismiss();
                }
            });
        });
        return dialog;
    }
}

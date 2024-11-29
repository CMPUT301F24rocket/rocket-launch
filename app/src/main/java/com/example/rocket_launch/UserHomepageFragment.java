package com.example.rocket_launch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.rocket_launch.organizer_events_tab.CreatedEventDetailsFragment;


public class UserHomepageFragment extends Fragment {

    private TextView welcome;
    private String username;


    public UserHomepageFragment(String username) {
        this.username = username;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_homepage, container, false);

        // Initialize the TextView and set the welcome message
        welcome = view.findViewById(R.id.welcome_user);
        welcome.setText(String.format("Welcome %s", username));


        return view;
    }

}



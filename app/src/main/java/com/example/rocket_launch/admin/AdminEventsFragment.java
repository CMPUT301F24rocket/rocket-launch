package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.rocket_launch.R;

/**
 * fragment for displaying contents of events tab for admin
 */
public class AdminEventsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_event_fragment, container, false);
        TextView labelText = view.findViewById(R.id.tabLabel);
        labelText.setText("Events Empty");
        return view;
    }
}
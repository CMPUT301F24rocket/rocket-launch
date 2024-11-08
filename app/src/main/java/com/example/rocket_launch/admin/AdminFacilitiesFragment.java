package com.example.rocket_launch.admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rocket_launch.R;

/**
 * fragment for displaying contents of facilities tab for admin
 */
public class AdminFacilitiesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_facilities_fragment, container, false);
        TextView labelText = view.findViewById(R.id.tabLabel);
        labelText.setText("Facilities Empty");
        return view;
    }
}
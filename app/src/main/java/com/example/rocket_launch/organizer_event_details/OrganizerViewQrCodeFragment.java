package com.example.rocket_launch.organizer_event_details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.rocket_launch.R;


public class OrganizerViewQrCodeFragment extends Fragment {


    public OrganizerViewQrCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.organizer_view_qr_fragment, container, false);

        //back button
        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> closeFragment());

        return view;
    }

    // Close the fragment and return to the Created Activities view
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
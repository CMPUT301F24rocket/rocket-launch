package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.rocket_launch.EventDetailsFragment;
import com.example.rocket_launch.QRCodeScannerActivity;
import com.example.rocket_launch.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


public class UserEventsFragment extends Fragment {

    FloatingActionButton addEvent;
    ActivityResultLauncher<ScanOptions> QRLauncher;

    public UserEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_events, container, false);

        addEvent = view.findViewById(R.id.open_qr_scan_button);
        addEvent.setOnClickListener(l -> scanQR());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QRLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String eventId = result.getContents();
                EventDetailsFragment showDetails = new EventDetailsFragment(eventId);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_frame, showDetails)
                .addToBackStack(null)  // Add to back stack so we can come back to this activity
                        .commit();
            }
        });
    }


    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRCodeScannerActivity.class);
        QRLauncher.launch(options);
    }
}
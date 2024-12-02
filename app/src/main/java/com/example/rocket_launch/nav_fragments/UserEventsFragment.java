package com.example.rocket_launch.nav_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rocket_launch.QRCodesDB;
import com.example.rocket_launch.UsersDB;
import com.example.rocket_launch.entrant_events_tab.ScannedEventDetailsFragment;
import com.example.rocket_launch.entrant_events_tab.QRCodeScannerActivity;
import com.example.rocket_launch.R;
import com.example.rocket_launch.entrant_events_tab.UserEventsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * fragment used to display all events related to an entrant
 * Author: Rachel
 */
public class UserEventsFragment extends Fragment {

    FloatingActionButton addEvent;
    ActivityResultLauncher<ScanOptions> QRLauncher;
    QRCodesDB qrCodesDB;

    /**
     * default constructor
     */
    public UserEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_events, container, false);

        TabLayout tabLayout = view.findViewById(R.id.user_tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.user_view_pager);

        UserEventsPagerAdapter adapter = new UserEventsPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Waited Events"); break;
                case 1: tab.setText("Registered Events"); break;
            }
        }).attach();

        // add by qr code button
        addEvent = view.findViewById(R.id.open_qr_scan_button);
        addEvent.setOnClickListener(l -> scanQR());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qrCodesDB = new QRCodesDB(); // initialize qrCodesDB
        QRLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                String code = result.getContents();
                qrCodesDB.loadEventId(code, eventId -> {

                    ScannedEventDetailsFragment showDetails = new ScannedEventDetailsFragment(eventId);
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .replace(R.id.fragment_frame, showDetails)
                            .addToBackStack(null)  // Add to back stack so we can come back to this activity
                            .commit();
                }, e -> {
                    Log.e("Firebase", "unknown Event");
                    Toast.makeText(requireContext(), "Unknown Event", Toast.LENGTH_SHORT).show();

                });
            }
        });
    }

    /**
     * opens QR code scanner activity
     */
    private void scanQR() {
        ScanOptions options = new ScanOptions();
        options.setOrientationLocked(true);
        options.setCaptureActivity(QRCodeScannerActivity.class);
        QRLauncher.launch(options);
    }
}
package com.example.rocket_launch.organizer_events_tab;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.R;

/**
 * fragment that displays qr code to organizer
 */
public class OrganizerViewQrCodeFragment extends Fragment {
    Event event;

    /**
     * default constructor
     */
    public OrganizerViewQrCodeFragment() {
        // Required empty public constructor
    }

    /**
     * constructor used for passing an event
     * @param event
     *  event of qr code to display
     */
    public OrganizerViewQrCodeFragment(Event event) {
        this.event = event;
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

        // QR code
        ImageView qrCodeImage = view.findViewById(R.id.event_qr_code);
        Bitmap qrCodeBitmap = event.generateQRCode();
        if (qrCodeBitmap != null) {
            qrCodeImage.setImageBitmap(qrCodeBitmap);
        }

        return view;
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
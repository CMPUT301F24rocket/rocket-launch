package com.example.rocket_launch.organizer_events_tab;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rocket_launch.Event;
import com.example.rocket_launch.EventsDB;
import com.example.rocket_launch.QRCodesDB;
import com.example.rocket_launch.R;

/**
 * fragment that displays qr code to organizer
 */
public class OrganizerViewQrCodeFragment extends Fragment {
    QRCodesDB qrCodesDB;
    Bitmap qrCodeBitmap;
    Event event;

    // UI
    TextView QRCodeTitle;
    ImageButton backButton;
    Button generateButton;
    ImageView qrCodeImage;


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
        this.qrCodesDB = new QRCodesDB();
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
        
        QRCodeTitle = view.findViewById(R.id.QRCodeTitle);

        //back button
        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> closeFragment());


        generateButton = view.findViewById(R.id.generateCode);
        generateButton.setOnClickListener(l -> generateQRCode());

        qrCodeImage = view.findViewById(R.id.event_qr_code);


        // QR code
        if (!event.getQRCode().isEmpty()) {
            qrCodeBitmap = event.generateQRCode();
        }
        if (qrCodeBitmap != null) {
            qrCodeImage.setImageBitmap(qrCodeBitmap);
            generateButton.setText("Generate new QR code");
        } else {
        // if no QR code yet, hide the views
        QRCodeTitle.setText("No QR code yet, Generate one!");
        qrCodeImage.setVisibility(View.GONE);

        }

        return view;
    }

    /**
     *
     */
    public void generateQRCode() {
        qrCodesDB.addCode(event.getEventID(), s -> {
            Toast.makeText(requireContext(), "Generation Successful!", Toast.LENGTH_LONG).show();
            Log.d("Generate QR Code", "creating QR code successful");
        }, l -> {
            Log.d("Generate QR Code", "error creating QR code");
            Toast.makeText(requireContext(), "Generation Failed :(", Toast.LENGTH_LONG).show();
        });
    }

    /**
     * Close the fragment and return to the Created Activities view
     */
    private void closeFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
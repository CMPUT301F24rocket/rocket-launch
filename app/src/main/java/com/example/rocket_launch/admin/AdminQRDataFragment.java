package com.example.rocket_launch.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocket_launch.QRCodesDB;
import com.example.rocket_launch.R;

import java.util.ArrayList;

/**
 * Fragment for displaying QR code data in the admin panel.
 * Shows a list of QR codes and their linked event IDs.
 * Author: Pouyan
 */
public class AdminQRDataFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminQRDataAdapter adapter;
    private ArrayList<QRCodeItem> qrDataList = new ArrayList<>();
    private QRCodesDB qrCodesDB;

    /**
     * Called to create and return the view hierarchy for the fragment.
     * Sets up the RecyclerView and loads QR code data from the database.
     *
     * @param inflater LayoutInflater to inflate views.
     * @param container Parent view that the fragment UI is attached to.
     * @param savedInstanceState Saved state for restoring fragment state.
     * @return The view for the fragment's UI.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View view = inflater.inflate(R.layout.admin_qrdata_fragment, container, false);

        qrCodesDB = new QRCodesDB(); // Initialize database for QR codes
        recyclerView = view.findViewById(R.id.qr_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Set up layout manager

        // Add default dividers between RecyclerView items
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new AdminQRDataAdapter(qrDataList); // Set up the adapter
        recyclerView.setAdapter(adapter);

        loadQRData(); // Load QR code data

        return view;
    }

    /**
     * Loads all QR code data and their linked event IDs from the database.
     * Updates the RecyclerView with new data.
     * Author: Pouyan
     */
    private void loadQRData() {
        qrCodesDB.loadAll(codes -> {
            qrDataList.clear(); // Clear the list before adding new data
            for (String code : codes) {
                qrCodesDB.loadEventId(code, eventId -> {
                    // Add QR code and linked event ID to the list
                    qrDataList.add(new QRCodeItem("QR Code: " + code, "Event ID: " + (eventId != null ? eventId : "No event linked")));
                    adapter.notifyDataSetChanged(); // Notify the adapter about data changes
                }, e -> {
                    // Handle failure to load event ID
                    qrDataList.add(new QRCodeItem("QR Code: " + code, "Event ID: Failed to load"));
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    /**
     * Represents a QR code item with its linked event ID.
     * Used for populating the RecyclerView in the fragment.
     * Author: Pouyan
     */
    public static class QRCodeItem {
        private String qrCode;
        private String eventId;

        /**
         * Constructor for QRCodeItem.
         *
         * @param qrCode The QR code value.
         * @param eventId The event ID linked to the QR code.
         * Author: Pouyan
         */
        public QRCodeItem(String qrCode, String eventId) {
            this.qrCode = qrCode;
            this.eventId = eventId;
        }

        /**
         * Gets the QR code value.
         *
         * @return The QR code as a String.
         * Author: Pouyan
         */
        public String getQrCode() {
            return qrCode;
        }

        /**
         * Gets the linked event ID.
         *
         * @return The event ID as a String.
         * Author: Pouyan
         */
        public String getEventId() {
            return eventId;
        }
    }
}

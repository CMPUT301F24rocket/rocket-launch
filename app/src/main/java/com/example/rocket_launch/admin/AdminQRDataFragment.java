package com.example.rocket_launch.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * Fragment for managing QR code data in the admin panel.
 * Provides functionality for viewing and deleting QR codes.
 * Author: Pouyan
 */
public class AdminQRDataFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminQRDataAdapter adapter;
    private ArrayList<QRCodeItem> qrDataList = new ArrayList<>();
    private QRCodesDB qrCodesDB;

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that this fragment's UI should attach to.
     * @param savedInstanceState A bundle containing the fragment's previously saved state.
     * @return The root view of the fragment.
     * Author: Pouyan
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.admin_qrdata_fragment, container, false);

        // Initialize database and RecyclerView
        qrCodesDB = new QRCodesDB();
        recyclerView = view.findViewById(R.id.qr_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        // Set up the adapter with the list of QR code items
        adapter = new AdminQRDataAdapter(qrDataList);
        recyclerView.setAdapter(adapter);

        // Set the deletion listener for QR codes
        adapter.setOnQRCodeDeleteListener(this::showDeleteConfirmationDialog);

        // Load QR data into the RecyclerView
        loadQRData();
        return view;
    }

    /**
     * Loads all QR codes from the database and populates the RecyclerView.
     * Author: Pouyan
     */
    private void loadQRData() {
        qrCodesDB.loadAll(codes -> {
            qrDataList.clear(); // Clear existing data
            for (String code : codes) {
                // Fetch event ID for each QR code
                qrCodesDB.loadEventId(code, eventId -> {
                    qrDataList.add(new QRCodeItem(code, eventId != null ? eventId : "No Event Linked"));
                    adapter.notifyDataSetChanged(); // Update RecyclerView
                }, e -> {
                    qrDataList.add(new QRCodeItem(code, "Failed to Load Event ID"));
                    adapter.notifyDataSetChanged(); // Update RecyclerView
                });
            }
        });
    }

    /**
     * Shows a confirmation dialog to delete a QR code.
     *
     * @param item     The QR code item to delete.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    private void showDeleteConfirmationDialog(QRCodeItem item, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete QR Code?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteQRCode(item, position))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes a QR code from the database and updates the RecyclerView.
     *
     * @param item     The QR code item to delete.
     * @param position The position of the item in the list.
     * Author: Pouyan
     */
    private void deleteQRCode(QRCodeItem item, int position) {
        qrCodesDB.removeCode(item.getQrCode(), item.getEventId(), unused -> {
            qrDataList.remove(position); // Remove from the list
            adapter.notifyItemRemoved(position); // Notify the adapter

            // Display a success toast
            Toast.makeText(requireContext(), "QR Code deleted successfully", Toast.LENGTH_SHORT).show();
        }, e -> {
            // Display a failure toast
            Toast.makeText(requireContext(), "Failed to delete QR Code. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e("AdminQRDataFragment", "Error deleting QR Code", e);
        });
    }

    /**
     * Data class representing a QR code and its associated event ID.
     * Author: Pouyan
     */
    public static class QRCodeItem {
        private String qrCode;
        private String eventId;

        /**
         * Constructor for creating a QRCodeItem.
         *
         * @param qrCode The QR code string.
         * @param eventId The associated event ID.
         * Author: Pouyan
         */
        public QRCodeItem(String qrCode, String eventId) {
            this.qrCode = qrCode;
            this.eventId = eventId;
        }

        /**
         * Gets the QR code string.
         *
         * @return The QR code string.
         * Author: Pouyan
         */
        public String getQrCode() {
            return qrCode;
        }

        /**
         * Gets the associated event ID
         *
         * @return The event ID.
         * Author: Pouyan
         */
        public String getEventId() {
            return eventId;
        }
    }
}
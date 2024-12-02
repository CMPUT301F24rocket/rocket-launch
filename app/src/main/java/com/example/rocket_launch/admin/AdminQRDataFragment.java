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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

/**
 * Fragment for managing QR code data in the admin panel.
 */
public class AdminQRDataFragment extends Fragment {
    private RecyclerView recyclerView;
    private AdminQRDataAdapter adapter;
    private ArrayList<QRCodeItem> qrDataList = new ArrayList<>();
    private QRCodesDB qrCodesDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_qrdata_fragment, container, false);

        qrCodesDB = new QRCodesDB();
        recyclerView = view.findViewById(R.id.qr_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new AdminQRDataAdapter(qrDataList);
        recyclerView.setAdapter(adapter);

        adapter.setOnQRCodeDeleteListener(this::showDeleteConfirmationDialog);

        loadQRData();
        return view;
    }

    private void loadQRData() {
        qrCodesDB.loadAll(codes -> {
            qrDataList.clear();
            for (String code : codes) {
                qrCodesDB.loadEventId(code, eventId -> {
                    qrDataList.add(new QRCodeItem(code, eventId != null ? eventId : "No Event Linked"));
                    adapter.notifyDataSetChanged();
                }, e -> {
                    qrDataList.add(new QRCodeItem(code, "Failed to Load Event ID"));
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void showDeleteConfirmationDialog(QRCodeItem item, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete QR Code?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteQRCode(item, position))
                .setNegativeButton("No", null)
                .show();
    }
    private void deleteQRCode(QRCodeItem item, int position) {
        qrCodesDB.removeCode(item.getQrCode(), item.getEventId(), unused -> {
            qrDataList.remove(position);
            adapter.notifyItemRemoved(position);

            // Display a success toast
            Toast.makeText(requireContext(), "QR Code deleted successfully", Toast.LENGTH_SHORT).show();
        }, e -> {
            // Display a failure toast
            Toast.makeText(requireContext(), "Failed to delete QR Code. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e("AdminQRDataFragment", "Error deleting QR Code", e);
        });
    }

    public static class QRCodeItem {
        private String qrCode;
        private String eventId;

        public QRCodeItem(String qrCode, String eventId) {
            this.qrCode = qrCode;
            this.eventId = eventId;
        }

        public String getQrCode() {
            return qrCode;
        }

        public String getEventId() {
            return eventId;
        }
    }
}
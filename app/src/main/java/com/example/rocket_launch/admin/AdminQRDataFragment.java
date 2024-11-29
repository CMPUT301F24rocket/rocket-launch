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

        // Add default dividers between items
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        adapter = new AdminQRDataAdapter(qrDataList);
        recyclerView.setAdapter(adapter);

        loadQRData();

        return view;
    }

    private void loadQRData() {
        qrCodesDB.loadAll(codes -> {
            qrDataList.clear();
            for (String code : codes) {
                qrCodesDB.loadEventId(code, eventId -> {
                    qrDataList.add(new QRCodeItem("QR Code: " + code, "Event ID: " + (eventId != null ? eventId : "No event linked")));
                    adapter.notifyDataSetChanged();
                }, e -> {
                    // Handle failure (e.g., log or show a message)
                    qrDataList.add(new QRCodeItem("QR Code: " + code, "Event ID: Failed to load"));
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    public static class QRCodeItem {
        String qrCode;
        String eventId;

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

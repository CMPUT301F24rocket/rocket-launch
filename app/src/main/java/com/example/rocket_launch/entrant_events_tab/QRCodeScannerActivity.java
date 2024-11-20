package com.example.rocket_launch.entrant_events_tab;

import android.widget.Button;

import com.example.rocket_launch.R;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * activity that displays the qr code scanner
 */
public class QRCodeScannerActivity extends CaptureActivity {

    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_qr_scanner);

        Button button = findViewById(R.id.qr_button_cancel);
        button.setOnClickListener(l -> finish());


        return findViewById(R.id.zxing_barcode_scanner);
    }
}

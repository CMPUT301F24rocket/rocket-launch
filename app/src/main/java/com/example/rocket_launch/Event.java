package com.example.rocket_launch;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Event {
    private String eventID;
    private String name;
    private String description;
    private int capacity;
    private boolean geolocationRequired;
    private Calendar startTime;
    private Calendar endTime;
    private int participants;
    private Image photo;
    private List<String> waitingList;
    private int maxWaitlistSize; // Integer

    public Event(){
    }

    public void setEventID(String eventID){this.eventID = eventID;}
    public void setName(String name){this.name = name;}
    public void setDescription(String description){this.description = description;}
    public void setCapacity(int capacity){this.capacity = capacity;}
    public void setGeolocationRequired(boolean geolocationRequired){this.geolocationRequired = geolocationRequired;}
    public void setStartTime(Calendar startTime){this.startTime = startTime;}
    public void setEndTime(Calendar endTime){this.endTime = endTime;}
    public void setParticipants(int participants){this.participants = participants;}
    public void setPhoto(Image photo){this.photo = photo;}
    public void setWaitingList(){this.waitingList = new ArrayList<>();}
    public void setMaxWaitlistSize(int maxWaitlistSize){this.maxWaitlistSize = maxWaitlistSize;}




    public Event(String eventID, String name, String description, Calendar startTime, Calendar endTime, int participants, Image photo, int maxWaitlistSize) {
        this.eventID = eventID;
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.participants = participants;
        this.photo = photo;
        this.waitingList = new ArrayList<>();
        this.maxWaitlistSize = maxWaitlistSize;
    }


    public int getMaxWaitlistSize() {
        return maxWaitlistSize;
    }

    public void addToWaitingList(String userID){
        waitingList.add(userID);
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public Image getPhoto() {
        return photo;
    }

    public String getEventID() {
        return eventID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCapacity() {return capacity;}

    public boolean getGeolocationRequired() {return geolocationRequired;}

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public int getParticipants() {
        return participants;
    }

    public Bitmap generateQRCode() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            // Adjust the width and height of the QR code as needed
            int width = 500, height = 500;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(getEventID(), BarcodeFormat.QR_CODE, width, height);

            // Convert the BitMatrix into a bitmap
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Log.e("error creating qr code", e.toString());
        }
        return null;
    }
}

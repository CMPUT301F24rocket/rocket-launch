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
    private List<String> invitedEntrants;
    private List<String> cancelledEntrants;
    private List<String> finalEntrants;
    private int maxWaitlistSize; // Integer

    public Event(){
        // verify lists appear in database -> ensures no access to undefined attribute
        this.waitingList = new ArrayList<>();
        this.cancelledEntrants = new ArrayList<>();
        this.finalEntrants = new ArrayList<>();
        this.invitedEntrants = new ArrayList<>();
    }

    public void setEventID(String eventID){this.eventID = eventID;}
    public void setName(String name){this.name = name;}
    public void setDescription(String description){this.description = description;}
    public void setCapacity(int capacity){this.capacity = capacity;}
    public void setGeolocationRequired(boolean geolocationRequired){this.geolocationRequired = geolocationRequired;}
    public void setStartTime(Calendar startTime){this.startTime = startTime;}
    public void setEndTime(Calendar endTime){this.endTime = endTime;}
    public void setParticipants(Integer participants) {
        this.participants = (participants != null) ? participants : 0; // Default to 0 if participants is null
    }

    public void setPhoto(Image photo){this.photo = photo;}
    public void setWaitingList(){this.waitingList = new ArrayList<>();}
    public void setMaxWaitlistSize(int maxWaitlistSize){this.maxWaitlistSize = maxWaitlistSize;}


    public int getMaxWaitlistSize() {
        return maxWaitlistSize;
    }

    public void addToWaitingList(String userID){
        waitingList.add(userID);
    }

    public void removeFromWaitingList(String userID) {waitingList.remove(userID);}

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

    // public boolean isGeolocationRequired() {return geolocationRequired;}

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public int getParticipants() {
        return participants;
    }

    public boolean canJoinWaitingList(){
        return waitingList.size() < maxWaitlistSize;
    }

    public boolean acceptInvitation(String userID) {
        return waitingList.contains(userID) && participants < capacity;
    }

    public void declineInvitation(String userID) {
        removeFromWaitingList(userID);
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

    public List<String> getCancelledEntrants() {
        return cancelledEntrants;
    }

    public void setCancelledEntrants(List<String> cancelledEntrants) {
        this.cancelledEntrants = cancelledEntrants;
    }

    public List<String> getFinalEntrants() {
        return finalEntrants;
    }

    public void setFinalEntrants(List<String> finalEntrants) {
        this.finalEntrants = finalEntrants;
    }

    public List<String> getInvitedEntrants() {
        return invitedEntrants;
    }

    public void setInvitedEntrants(List<String> invitedEntrants) {
        this.invitedEntrants = invitedEntrants;
    }
}

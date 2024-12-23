package com.example.rocket_launch;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Class that defines a new instance of an event
 * Author: Kaiden
 */
public class Event {
    private String eventID;
    private String QRCode;
    private String name;
    private String description;
    private int capacity;
    private boolean geolocationRequired;
    private int participants;
    private List<String> waitingList;
    private List<String> invitedEntrants;
    private List<String> cancelledEntrants;
    private List<String> registeredEntrants;

    private String posterUrl;
    private List<EntrantLocationData> entrantLocationDataList;

    private int maxWaitlistSize;// Integer
    private List<Notification> notifications; // new notification list

    private String organizer;


    /**
     * Constructor for event class
     */
    public Event(){

        // verify lists appear in database -> ensures no access to undefined attribute
        this.waitingList = new ArrayList<>();
        this.cancelledEntrants = new ArrayList<>();
        this.registeredEntrants = new ArrayList<>();
        this.invitedEntrants = new ArrayList<>();

        this.entrantLocationDataList = new ArrayList<>();

        this.notifications = new ArrayList<>(); // initialize notification list

        this.QRCode = "";
    }

    // ############################ //
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public void addNotifications(List<Notification> notifications){
        this.notifications = notifications;

    }

    // ############################ //


    public void setEventID(String eventID){this.eventID = eventID;}
    public void setName(String name){this.name = name;}
    public void setDescription(String description){this.description = description;}
    public void setCapacity(int capacity){this.capacity = capacity;}
    public void setGeolocationRequired(boolean geolocationRequired){this.geolocationRequired = geolocationRequired;}
    public void setParticipants(Integer participants) {
        this.participants = (participants != null) ? participants : 0; // Default to 0 if participants is null
    }

    public void setWaitingList(){this.waitingList = new ArrayList<>();}
    public void setMaxWaitlistSize(int maxWaitlistSize){this.maxWaitlistSize = maxWaitlistSize;}

    //For Entrant Location Data List
    public void setEntrantLocationDataList(){this.entrantLocationDataList = new ArrayList<>();}
    public void addToEntrantLocationDataList(EntrantLocationData entrantLocationData) {entrantLocationDataList.add(entrantLocationData);}
    public List<EntrantLocationData> getEntrantLocationDataList(){return entrantLocationDataList;}
    public void removeFromEntrantLocationDataList(EntrantLocationData entrantLocationData){entrantLocationDataList.remove(entrantLocationData);}

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

    public String getEventID() {
        return eventID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getCapacity() {return capacity;}

    public boolean getGeolocationRequired() {return geolocationRequired;}

    public int getParticipants() {
        return participants;
    }

    public boolean acceptInvitation(String userID) {
        return waitingList.contains(userID) && participants < capacity;
    }

    public void declineInvitation(String userID) {
        removeFromWaitingList(userID);
    }


    /**
     * Generates the bitmap of the QR code
     * @return bitmap of QR code, or null
     */
    public Bitmap generateQRCode() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            int width = 500, height = 500;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(getQRCode(), BarcodeFormat.QR_CODE, width, height);

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

    /**
     * Author: Kaiden
     * remove sampleAmount of users from waitlist and add them to invited list
     * @param sampleAmount
     *  (int) amount of entrants to remove and return
     */
    public List<String> sampleWaitlist(int sampleAmount) {
        List<String> sampledUsers = new ArrayList<>();

        if (sampleAmount <= capacity) {
            Random rand = new Random();
            // sample sampleAmount from waitlist
            for (int i = 0; i < sampleAmount; i++) {
                int index = rand.nextInt(waitingList.size());
                
                sampledUsers.add(waitingList.get(index)); // add to list to return
                invitedEntrants.add(waitingList.get(index)); // add to invited entrants list
                waitingList.remove(index); // remove from waiting list

            }
        }
        return sampledUsers;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }


    public List<String> getCancelledEntrants() {
        return cancelledEntrants;
    }

    public void setCancelledEntrants(List<String> cancelledEntrants) {
        this.cancelledEntrants = cancelledEntrants;
    }

    public List<String> getregisteredEntrants() {
        return registeredEntrants;
    }

    public void setregisteredEntrants(List<String> registeredEntrants) {
        this.registeredEntrants = registeredEntrants;
    }

    public List<String> getInvitedEntrants() {
        return invitedEntrants;
    }

    public void setInvitedEntrants(List<String> invitedEntrants) {
        this.invitedEntrants = invitedEntrants;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    /**
     * Defines the User Array Adapter class
     */
    public static class UserArrayAdapter extends ArrayAdapter<User> {

        /**
         * constructor
         * @param context
         *  context of where fragment is
         * @param users
         *  users list to display
         */
        public UserArrayAdapter(Context context, ArrayList<User> users) {
            super(context,0,users);
        }

        /**
         * get current view
         * @param position
         *  position in array
         * @param convertView
         *  view to convert to
         * @param parent
         *  parent display
         * @return
         *  returns a view to display
         */
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            User user = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false);
            }

            TextView userName = convertView.findViewById(R.id.list_event_name);

            assert user != null;
            userName.setText(user.getUserName());

            return convertView;
        }
    }
}

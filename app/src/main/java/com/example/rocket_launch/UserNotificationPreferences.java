package com.example.rocket_launch;

public class UserNotificationPreferences {

    private boolean receiveOnChosen;
    private boolean receiveOnNotChosen;
    private boolean GeneraloptOut;

    UserNotificationPreferences() {
        // initially we to give users all notifications
        this.receiveOnChosen = true;
        this.receiveOnNotChosen = true;
        this.GeneraloptOut = true;
    }

    public boolean isReceiveOnChosen() {
        return receiveOnChosen;
    }

    public void setReceiveOnChosen(boolean receiveOnChosen) {
        this.receiveOnChosen = receiveOnChosen;
    }

    public boolean isReceiveOnNotChosen() {
        return receiveOnNotChosen;
    }

    public void setReceiveOnNotChosen(boolean receiveOnNotChosen) {
        this.receiveOnNotChosen = receiveOnNotChosen;
    }

    public boolean isGeneraloptOut() {
        return GeneraloptOut;
    }

    public void setGeneraloptOut(boolean generaloptOut) {
        GeneraloptOut = generaloptOut;
    }
}

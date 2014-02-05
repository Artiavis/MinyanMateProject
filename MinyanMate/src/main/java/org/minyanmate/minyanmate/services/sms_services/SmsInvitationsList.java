package org.minyanmate.minyanmate.services.sms_services;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to to parcel invite message data for passing around
 * {@link org.minyanmate.minyanmate.services.sms_services}
 */
public class SmsInvitationsList implements Parcelable, Serializable {

    /**
     * The name of the cached file to use for storing a SmsInvitationsList
     */
    static final String FILENAME = "invitationsListCache";
    private int eventId;
    private int timesSent = 1;
    private boolean isNewList = true;
    private List<SmsInvite> smsInviteList = new ArrayList<SmsInvite>();

    public SmsInvitationsList(int eventId) {
        this.eventId = eventId;
    }

    public SmsInvitationsList(int eventId, int timesSent) {
        this.eventId = eventId;
        this.timesSent = timesSent;
    }

    public SmsInvitationsList(int eventId, List<SmsInvite> inviteList) {
        this.eventId = eventId;
        this.smsInviteList = inviteList;
    }

    public SmsInvitationsList(int eventId, List<SmsInvite> inviteList, int timesSent) {
        this(eventId, inviteList);
        this.timesSent = timesSent;
    }

    public int getEventId() {
        return eventId;
    }

    public int getTimesSent() {
        return timesSent;
    }

    public boolean isNewList() {
        return isNewList;
    }

    public void setIsNewList(boolean isNewList) {
        this.isNewList = isNewList;
    }

    public List<SmsInvite> getSmsInviteList() {
        return smsInviteList;
    }

    public void setSmsInviteList(List<SmsInvite> smsInviteList) {
        this.smsInviteList = smsInviteList;
    }

    public void incrementTimesSent() {
        timesSent += 1;
    }

    public SmsInvitationsList(Parcel inParcel) {

        // first read in integer data
        int[] intData = new int[2];
        inParcel.readIntArray(intData);

        this.eventId = intData[0];
        this.timesSent = intData[1];

        // then read in SmsInvite data
        inParcel.readTypedList(smsInviteList, SmsInvite.CREATOR);
    }

    static SmsInvitationsList readListFromFile(Context context) {

        FileInputStream fis = null;
        ObjectInputStream is = null;
        SmsInvitationsList smsInvitationsList = null;

        try {

            fis = context.openFileInput(SmsInvitationsList.FILENAME);

            try {
                is = new ObjectInputStream(fis);
                smsInvitationsList = (SmsInvitationsList) is.readObject();
                is.close();
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            fis.close();
            return smsInvitationsList; // may be null and that's okay

        } catch (FileNotFoundException e) {
            System.err.println("File " + FILENAME + " does not exist");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    void writeListToFile(Context context) {

        try {
            FileOutputStream fos = context.openFileOutput(SmsInvitationsList.FILENAME, Context.MODE_PRIVATE);
            try {
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(this);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void clearFile(Context context) {

        context.deleteFile(FILENAME);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeIntArray(new int[] { this.eventId, this.timesSent});
        destParcel.writeTypedList(this.smsInviteList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SmsInvitationsList createFromParcel(Parcel in) {
            return new SmsInvitationsList(in);
        }

        public SmsInvitationsList[] newArray(int size) {
            return new SmsInvitationsList[size];
        }
    };
}

package org.minyanmate.minyanmate.models;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Used to to parcel invite message data for passing around
 * {@link org.minyanmate.minyanmate.services.sms_services}
 */
public class SmsInvite implements Parcelable {

    private int eventId;
    private String inviteMessage;
    private String inviteAddress;
    private String phoneNumberId;
    private int timesSent = 0;

    public SmsInvite(int eventId, String msg, String address, String phoneNumberId) {
        this.eventId = eventId;
        this.inviteMessage = msg;
        this.inviteAddress = address;
        this.phoneNumberId = phoneNumberId;
    }

    public SmsInvite(int eventId, String msg, String address, String phoneNumberId, int timesSent) {
        this(eventId, msg, address, phoneNumberId);
        this.timesSent = timesSent;
    }

    public int getEventId() {
        return eventId;
    }

    public String getInviteMessage() {
        return inviteMessage;
    }

    public String getInviteAddress() {
        return inviteAddress;
    }

    public String getPhoneNumberId() {
        return phoneNumberId;
    }

    public int getTimesSent() {
        return timesSent;
    }

    public void incrementTimesSent() {
        timesSent += 1;
    }

    public SmsInvite(Parcel inParcel) {
        String[] strData = new String[3];
        int[] intData = new int[2];

        inParcel.readStringArray(strData);
        inParcel.readIntArray(intData);

        this.inviteMessage = strData[0];
        this.inviteAddress = strData[1];
        this.phoneNumberId = strData[2];
        this.eventId = intData[0];
        this.timesSent = intData[1];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeStringArray(new String[] { this.inviteMessage, this.inviteAddress, this.phoneNumberId });
        destParcel.writeIntArray(new int[] { this.eventId, this.timesSent });
    }

    public static final Creator CREATOR = new Parcelable.Creator() {
        public SmsInvite createFromParcel(Parcel in) {
            return new SmsInvite(in);
        }
        public SmsInvite[] newArray(int size) {
            return new SmsInvite[size];
        }
    };
}

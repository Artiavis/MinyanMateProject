package org.minyanmate.minyanmate.services.sms_services;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to to parcel invite message data for passing around
 * {@link org.minyanmate.minyanmate.services.sms_services}
 */
public class SmsInvitationsList implements Parcelable {

    private int eventId;
    private int timesSent = 1;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeIntArray(new int[] { this.eventId, this.timesSent });
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

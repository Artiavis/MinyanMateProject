package org.minyanmate.minyanmate.services.sms_services;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jeff on 2/3/14.
 *
 * Used to contain data about an individual sms invite, especially when resending
 * an Sms in the event of a failure.
 */

public class SmsInvite implements Parcelable {

    public SmsInvite(String msg, String addr, long numid, String name) {
        this.inviteMessage = msg;
        this.inviteAddress = addr;
        this.phoneNumberId = numid;
        this.name = name;
    }

    private String inviteMessage;
    private String inviteAddress;
    private long phoneNumberId;
    private String name;

    public String getInviteMessage() {
        return inviteMessage;
    }

    public String getInviteAddress() {
        return inviteAddress;
    }

    public long getPhoneNumberId() {
        return phoneNumberId;
    }

    public String getName() {
        return name;
    }

    public SmsInvite(Parcel inParcel) {
        String[] strings = new String[3];

        inParcel.readStringArray(strings);
        this.inviteMessage = strings[0];
        this.inviteAddress = strings[1];
        this.name          = strings[3];

        phoneNumberId = inParcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeStringArray(new String[] {
            inviteMessage, inviteAddress,
                name
        });
        parcel.writeLong(phoneNumberId);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public SmsInvite createFromParcel(Parcel in) {
            return new SmsInvite(in);
        }

        public SmsInvite[] newArray(int size) {
            return new SmsInvite[size];
        }
    };
}

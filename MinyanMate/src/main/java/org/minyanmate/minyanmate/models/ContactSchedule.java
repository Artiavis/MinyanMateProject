package org.minyanmate.minyanmate.models;


import android.database.Cursor;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.ContactScheduleMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used for the odd and strange
 * {@link org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider#CONTACT_SCHEDULE}
 * query provided in order to help with
 * {@link org.minyanmate.minyanmate.ContactManagerActivity}.
 * @author Jeff
 *
 */
public class ContactSchedule extends MinyanSchedule {

    private Long contactId, phoneNumberId;

    ContactSchedule(int scheduleId, String dayName, String prayerName,
                    int prayerHour, int prayerMinute, Long contactId,
                    Long phoneNumberId) {
        super(prayerName, prayerHour, prayerMinute, contactId != null, dayName);
        this._id = scheduleId;
        this.phoneNumberId = phoneNumberId;
        this.contactId = contactId;
    }

    public Long getContactId() {
        return contactId;
    }

    public Long getPhoneNumberId() {
        return phoneNumberId;
    }

    public static ContactSchedule contactScheduleFromCursor(Cursor c) {
        return new ContactSchedule(
                c.getInt(ContactScheduleMatrix.PRAYER_SCHEDULE_ID),
                c.getString(ContactScheduleMatrix.PRAYER_DAY),
                c.getString(ContactScheduleMatrix.PRAYER_NAME),
                c.getInt(ContactScheduleMatrix.PRAYER_HOUR),
                c.getInt(ContactScheduleMatrix.PRAYER_MIN),
                c.isNull(ContactScheduleMatrix.CONTACT_ID) ?
                        null :
                        c.getLong(ContactScheduleMatrix.CONTACT_ID),
                c.isNull(ContactScheduleMatrix.PHONE_ID) ?
                        null :
                        c.getLong(ContactScheduleMatrix.PHONE_ID)
        );
    }

    public static List<ContactSchedule> contactScheduleListFromCursor(Cursor c) {

        List<ContactSchedule> contactScheduleList = new ArrayList<ContactSchedule>();
        while(c.moveToNext()) {
            contactScheduleList.add(contactScheduleFromCursor(c));
        }

        return contactScheduleList;
    }
}

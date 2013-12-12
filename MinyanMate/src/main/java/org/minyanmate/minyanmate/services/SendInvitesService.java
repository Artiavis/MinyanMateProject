package org.minyanmate.minyanmate.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.UserParticipationPopupActivity;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class SendInvitesService extends WakefulIntentService {

	public SendInvitesService() {
		super("SendInvitesService");
	}

    /**
     * The requestCode to be used after {@link org.minyanmate.minyanmate.services.OnMinyanAlarmReceiver}
     * fires.
     */
    public static final int SEND_SCHEDULE_INVITES = 1;

    /**
     * The requestCode to be used after selecting a contact to be invited, ad-hoc.
     */
    public static final int SEND_INVITE_TO_CONTACT = 2;
    public static final int SEND_CANCELLATION_NOTIFICATION = 3;

    public static final String REQUEST_CODE = "requestCode";
    public static final String SCHEDULE_ID = "scheduleId";
    public static final String EVENT_ID = "eventId";
    public static final String PHONE_NUMBER_ID = "phoneNumberId";

    @Override
	protected void doWakefulWork(Intent intent) {

		Bundle b = intent.getExtras();
		
		Log.i("SendInvitesService", "Inside SendInvitesService");
		Log.d("SendInvitesService", "Bundle: " + b);

        int requestCode = b.getInt(REQUEST_CODE);
		int scheduleId = b.getInt(SCHEDULE_ID);

        Log.d("SendInvitesService", " Request Code: " + requestCode);
        Log.d("SendInvitesService", " Schedule Id: " + scheduleId);

        switch (requestCode) {
            case SEND_SCHEDULE_INVITES:
                beginSendScheduleInvites(scheduleId);
                break;

            case SEND_INVITE_TO_CONTACT:
                int eventId = b.getInt(EVENT_ID);
                long phoneNumberId = b.getLong(PHONE_NUMBER_ID);
                Log.d("SendInvitesService", " Event Id: " + eventId);
                Log.d("SendInvitesService", " Phone Number Id: " + phoneNumberId);
                beginSendInviteToContact(scheduleId, eventId, phoneNumberId);

            default:

        }
	}



    private void beginSendScheduleInvites(int scheduleId) {
        Cursor c = getContentResolver().query(
                Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + scheduleId),
                null, null, null, null
                );
        if (c.moveToNext()) {
            MinyanSchedule sched = MinyanSchedule.schedFromCursor(c);

            Log.d("SendInvitesService", "Scheduling Minyan " + sched.getId());

            int prayerHour = sched.getHour();
            int prayerMinute = sched.getMinute();

            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, prayerHour);
            date.set(Calendar.MINUTE, prayerMinute);

            ContentValues eventValues = new ContentValues();
            eventValues.put(MinyanEventsTable.COLUMN_MINYAN_SCHEDULE_TIME, System.currentTimeMillis());
            eventValues.put(MinyanEventsTable.COLUMN_MINYAN_START_TIME, date.getTimeInMillis());
            eventValues.put(MinyanEventsTable.COLUMN_MINYAN_END_TIME, date.getTimeInMillis() + TimeUnit.MINUTES.toMillis(30)); // give it 30 minutes
            eventValues.put(MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE, 0);
            eventValues.put(MinyanEventsTable.COLUMN_MINYAN_COMPLETE_ALERTED, 0);
            eventValues.put(MinyanEventsTable.COLUMN_MINYAN_SCHEDULE_ID, scheduleId);
            eventValues.put(MinyanEventsTable.COLUMN_DAY_NAME, sched.getDay());
            eventValues.put(MinyanEventsTable.COLUMN_PRAYER_NAME, sched.getPrayerName());
            Uri eventUri = getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENTS, eventValues);

            long eventId = ContentUris.parseId(eventUri);
            Log.i("SendInvitesService", "Event id: " + eventId);

            Cursor contactsToBeInvited = getContentResolver().query(
                    MinyanMateContentProvider.CONTENT_URI_CONTACTS, null,
                    MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?",
                    new String[] { String.valueOf(scheduleId) }, null);


            while (contactsToBeInvited.moveToNext()) {

                String name = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.DISPLAY_NAME);
                long phoneNumId = contactsToBeInvited.getLong(MinyanMateContentProvider.ContactMatrix.PHONE_NUMBER_ID);
                String number = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.PHONE_NUMBER);

                sendInviteSms(sched, eventId, name, phoneNumId, number);


                SystemClock.sleep(1000);
            }
            UserParticipationPopupActivity.createUserParticipationPopup((int) eventId, getApplicationContext());

        }
    }

    private void beginSendInviteToContact(int scheduleId, int eventId, long phoneNumberId) {
        Cursor scheduleCursor = getContentResolver().query(
                Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + scheduleId),
                null, null, null, null
        );

        if (scheduleCursor.moveToFirst()) {
            MinyanSchedule schedule = MinyanSchedule.schedFromCursor(scheduleCursor);

            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?",
                    new String[] { Long.toString(phoneNumberId) }, null);

            if (phoneCursor.moveToFirst()) {

                String name = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));

                sendInviteSms(schedule, eventId, name, phoneNumberId, number);
            }
        }
    }

    private void sendInviteSms(MinyanSchedule sched, long eventId, String name, long phoneNumId, String number) {

        ContentValues inviteValues;

        Log.i("SendInvitesService", "Inviting " + name);
        SmsManager smsm = SmsManager.getDefault();

        String fullInviteMessage = MinyanSchedule.formatInviteMessage(this, sched.getInviteMessage(),
                sched.getPrayerName(), sched.getHour(), sched.getMinute());

        // TODO fire intent to log the invited recipient in the Goers table if the message was received
        smsm.sendTextMessage(number, null, fullInviteMessage, null, null);

        inviteValues = new ContentValues();
        inviteValues.put(MinyanGoersTable.COLUMN_DISPLAY_NAME, name);
        inviteValues.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE));
        inviteValues.put(MinyanGoersTable.COLUMN_IS_INVITED, 1);
        inviteValues.put(MinyanGoersTable.COLUMN_PHONE_NUMBER_ID, phoneNumId);
        inviteValues.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, eventId);
        getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, inviteValues);
    }

}

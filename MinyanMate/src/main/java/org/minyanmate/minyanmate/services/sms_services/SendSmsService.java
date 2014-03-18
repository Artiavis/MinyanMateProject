package org.minyanmate.minyanmate.services.sms_services;

import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.UserParticipationPopupActivity;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.FullMinyanSchedule;
import org.minyanmate.minyanmate.models.InviteStatus;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SendSmsService extends WakefulIntentService {

    public SendSmsService() {
		super("SendSmsService");
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

    /**
     * To be used if implementing a feature to automatically cancel a minyan.
     * Not currently implemented.
     */
    public static final int SEND_CANCELLATION_NOTIFICATION = 3;

    public static final int SEND_HEADCOUNT_UPDATE = 4;

    /**
     * Used by {@link org.minyanmate.minyanmate.services.sms_services.ResendSmsReceiver}
     * to indicate that it will attempt to resend a batch of Sms
     */
    public static final int RESEND_FAILED_INVITES = 5;

    public static final String REQUEST_CODE = "requestCode";
    public static final String SCHEDULE_ID = "scheduleId";
    public static final String EVENT_ID = "eventId";
    public static final String PHONE_NUMBER_ID = "phoneNumberId";
    public static final String UPDATE_MESSAGE = "updateMessage";
    public static final String SMS_INVITE = "smsInvite";
    public static final String SMS_INVITATIONS = "smsInvitations";
    public static final String TIMES_SENT = "timesSent";

    /**
     * Used to uniquely identify all pendingintents for {@link #sendInviteSms}
     */
    private static int pendingIntentNumber = 0;

    @Override
	protected void doWakefulWork(Intent intent) {

		Bundle b = intent.getExtras();
		
		Log.i("SendSmsService", "Inside SendSmsService");
		Log.d("SendSmsService", "Bundle: " + b);

        int requestCode = b.getInt(REQUEST_CODE);
		int scheduleId = b.getInt(SCHEDULE_ID);

        Log.d("SendSmsService", " Request Code: " + requestCode);
        Log.d("SendSmsService", " Schedule Id: " + scheduleId);

        switch (requestCode) {
            case SEND_SCHEDULE_INVITES:
                beginSendScheduleInvites(scheduleId);
                break;

            case SEND_INVITE_TO_CONTACT:
                int eventId = b.getInt(EVENT_ID);
                long phoneNumberId = b.getLong(PHONE_NUMBER_ID);
                Log.d("SendSmsService", " Event Id: " + eventId);
                Log.d("SendSmsService", " Phone Number Id: " + phoneNumberId);
                beginSendInviteToContact(scheduleId, eventId, phoneNumberId);
                break;

            case SEND_HEADCOUNT_UPDATE:
                String msg = b.getString(UPDATE_MESSAGE);
                sendUpdateSms(msg);
                break;

            case RESEND_FAILED_INVITES:
                SmsInvitationsList smsInvitationsList = b.getParcelable(SendSmsService.SMS_INVITATIONS);
                sendScheduledInvites(smsInvitationsList);

                break;

            default:

        }
	}

    /**
     * Forward an Sms to an interested third party containing a summary of the latest headcount.
     * Is used in conjunction with forwarding contact preferences.
     * @param msg the text of the message to be sent
     */

    private void sendUpdateSms(String msg) {

        String contactUriString = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.forwardContactPreference), "");
        Uri contactUri = Uri.parse(contactUriString);

        Cursor phoneContacts = getContentResolver().query(contactUri,
                null, null, null, null);

        if (phoneContacts != null) {
            if (phoneContacts.moveToFirst()){
                String phoneNumber = phoneContacts.getString(
                        phoneContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneContacts.close();

                SmsManager smsm = SmsManager.getDefault();
                smsm.sendTextMessage(phoneNumber, null, msg, null, null);
            } else
                Log.e("sendUpdateSms", "Error! Could not send update message!");

            phoneContacts.close();
        }
    }

    /**
     * Upon reaching the scheduled time of a service, query the latest information about that
     * service and contact all subscribed parties with that information. Additionally creates
     * an event in the database for that service.
     * @param scheduleId the id of the service to begin
     */
    private void beginSendScheduleInvites(int scheduleId) {
        Cursor c = getContentResolver().query(
                Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + scheduleId),
                null, null, null, null
                );
        if (c.moveToNext()) {
            FullMinyanSchedule sched = FullMinyanSchedule.scheduleFromCursor(c);
            c.close();
            Log.d("SendSmsService", "Scheduling Minyan " + sched.getId());

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
            Log.i("SendSmsService", "Event id: " + eventId);

            Cursor contactsToBeInvited = getContentResolver().query(
                    MinyanMateContentProvider.CONTENT_URI_CONTACTS, null,
                    MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?",
                    new String[] { String.valueOf(scheduleId) }, null);

            // Create smsInvitationsList to pass to the following method
            SmsInvitationsList smsInvitationsList = new SmsInvitationsList((int) eventId);

            while (contactsToBeInvited.moveToNext()) {

                String name = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.DISPLAY_NAME);
                long phoneNumId = contactsToBeInvited.getLong(MinyanMateContentProvider.ContactMatrix.PHONE_NUMBER_ID);
                String number = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.PHONE_NUMBER);
                String fullInviteMessage = FullMinyanSchedule.formatInviteMessage(this, sched.getInviteMessage(),
                        sched.getPrayerName(), sched.getHour(), sched.getMinute());

                SmsInvite smsInvite = new SmsInvite(fullInviteMessage, number, phoneNumId, name);
                smsInvitationsList.getSmsInviteList().add(smsInvite);
            }
            contactsToBeInvited.close();
            sendScheduledInvites(smsInvitationsList);

            UserParticipationPopupActivity.createUserParticipationPopup(smsInvitationsList.getEventId(),
                    getApplicationContext());

        } else {
            Log.e("SendSmsService: beginSendScheduledInvites", "No minyans by this id!");
        }
    }

    /**
     * A function to send invitations to the list contained in smsInvitationlist.
     * This function can be called both the original time invitations get sent out
     * as well as successive times if any of the original invitations failed.
     *
     * @param smsInvitationsList An object with the eventId, the number of times sent, and a list of invitations
     * to send out.
     */
    private void sendScheduledInvites(SmsInvitationsList smsInvitationsList) {


        Log.d("SendSmsService: sendScheduledInvites","Sending " +
                smsInvitationsList.getSmsInviteList().size() + " invites");
        // Get list of contacts from smsInvitationsList
        List<SmsInvite> smsInvites = smsInvitationsList.getSmsInviteList();

        for(SmsInvite smsInvite : smsInvites) {

            sendInviteSms(smsInvitationsList.getEventId(), smsInvitationsList.getTimesSent(),
                    smsInvite);
            SystemClock.sleep(800);
        }
    }

    /**
     * Sends an additional invite to a contact who was not originally intended to receive one
     * according to the database.
     * @param scheduleId the id of the service to invite contact to
     * @param eventId the instance of the service to invite contact to
     * @param phoneNumberId the phone number id of that contact
     */
    private void beginSendInviteToContact(int scheduleId, int eventId, long phoneNumberId) {
        Cursor scheduleCursor = getContentResolver().query(
                Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + scheduleId),
                null, null, null, null
        );

        if (scheduleCursor.moveToFirst()) {

            FullMinyanSchedule schedule = FullMinyanSchedule.scheduleFromCursor(scheduleCursor);
            scheduleCursor.close();

            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?",
                    new String[] { Long.toString(phoneNumberId) }, null);

            if (phoneCursor.moveToFirst()) {

                String name = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneCursor.close();

                String fullInviteMessage = FullMinyanSchedule.formatInviteMessage(this, schedule.getInviteMessage(),
                        schedule.getPrayerName(), schedule.getHour(), schedule.getMinute());

                SmsInvite smsInvite = new SmsInvite(fullInviteMessage, number, phoneNumberId, name);
                sendInviteSms(eventId, 1, smsInvite);

//                sendInviteSms(schedule, eventId, name, phoneNumberId, number);
            } else {
                Log.e("SendSmsService: beginSendInviteToContact", "No contact by that id!");
            }
        } else {
            Log.e("SendSmsService: beginSendInviteToContact", "No minyan by that schedule id!");
        }
    }

    /**
     * A new method for actually performing the sms invitations. Also constructs
     * a {@link android.app.PendingIntent} for both the success and fail cases.
     * @param eventId
     * @param timesSent
     * @param smsInvite
     */
    private void sendInviteSms(int eventId, int timesSent, SmsInvite smsInvite) {

        Log.i("SendSmsService", "Inviting " + smsInvite.getName() + " for the " + timesSent +
                " time with pendingIntent number " + pendingIntentNumber);
        SmsManager smsm = SmsManager.getDefault();

        Intent si = new Intent(getApplicationContext(), SentSmsStatusReceiver.class);
        si.putExtra(SMS_INVITE, (Parcelable) smsInvite);
        si.putExtra(EVENT_ID, eventId);
        si.putExtra(TIMES_SENT, timesSent);
        PendingIntent sentIntent = PendingIntent.getBroadcast(getApplicationContext(),
                pendingIntentNumber, si, PendingIntent.FLAG_UPDATE_CURRENT);

        // this doesn't seem to work on all networks so switch to using sentintents
//        Intent di = new Intent(getApplicationContext(), SmsSentReceiver.class);
//        di.putExtra(REQUEST_CODE, SmsSentReceiver.INVITE_RECEIVED);
//        di.putExtra(SMS_INVITE, (Parcelable) smsInvite);
//        di.putExtra(EVENT_ID, eventId);
//        PendingIntent deliveryIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                pendingIntentNumber, di, PendingIntent.FLAG_UPDATE_CURRENT);


        smsm.sendTextMessage(smsInvite.getInviteAddress(), null, smsInvite.getInviteMessage(),
                sentIntent, null);

        pendingIntentNumber++;
    }

    @Deprecated
    private void sendInviteSms(FullMinyanSchedule sched, long eventId, String name, long phoneNumId, String number) {

        ContentValues inviteValues;

        Log.i("SendSmsService", "Inviting " + name);
        SmsManager smsm = SmsManager.getDefault();

        String fullInviteMessage = FullMinyanSchedule.formatInviteMessage(this, sched.getInviteMessage(),
                sched.getPrayerName(), sched.getHour(), sched.getMinute());

        //  fire intent to log the invited recipient in the Goers table if the message was received
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

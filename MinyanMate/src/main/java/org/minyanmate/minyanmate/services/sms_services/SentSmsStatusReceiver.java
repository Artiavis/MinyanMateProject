package org.minyanmate.minyanmate.services.sms_services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.util.Log;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;

import java.util.Calendar;

/**
 * This BroadcastReceiver is passed as a pendingIntent to the SmsManager in methods of
 * {@link org.minyanmate.minyanmate.services.sms_services.SendSmsService} to receive the
 * sentIntent. It should be used to handle cases related to SMS failure.
 */
public class SentSmsStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int resultCode = getResultCode();
        Log.d("SentSmsStatusReceiver", "Receiving sentIntent with resultCode " + resultCode);

        SmsInvite smsInvite = intent.getParcelableExtra(SendSmsService.SMS_INVITE);
        int eventId = intent.getIntExtra(SendSmsService.EVENT_ID, 0);

        switch (resultCode) {
            // If successful, do nothing!
            case Activity.RESULT_OK:
                insertSmsInviteRecord(context, smsInvite, eventId);
                break;

            // If received an error code, try again using an alarm
            // Multiple messages can go to the same alarm using the same intent
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                // try again

                int timesSent = intent.getIntExtra(SendSmsService.TIMES_SENT, 1);
                setResendMessage(context, smsInvite, eventId, timesSent);
                break;


            default:
                break;
        }
    }

    private void insertSmsInviteRecord(Context context, SmsInvite smsInvite, int eventId) {

        Log.d("SmsSentReceiver", "inserting invite for " + smsInvite.getName());

        ContentValues inviteValues = new ContentValues();
        inviteValues.put(MinyanGoersTable.COLUMN_DISPLAY_NAME, smsInvite.getName());
        inviteValues.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE));
        inviteValues.put(MinyanGoersTable.COLUMN_IS_INVITED, 1);
        inviteValues.put(MinyanGoersTable.COLUMN_PHONE_NUMBER_ID, smsInvite.getPhoneNumberId());
        inviteValues.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, eventId);
        context.getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, inviteValues);
    }

    /**
     * This method gets called when automatically scheduling failed messages to resend. This method
     * creates a {@link android.app.PendingIntent} using the {@link SmsInvitationsList#eventId}
     * of a failed message if no previous intent exists (otherwise it recalls the existing one).
     * <p>
     * Text files are used to cache the value of the
     * {@link org.minyanmate.minyanmate.services.sms_services.SmsInvitationsList} to protect against
     * the garbage collector. If the file does not exist, create it. And in between requests, store
     * the file results therein.
     * <p>
     * If the list contained therein is null or contains references to messages from previous events,
     * it should be replaced. If it references a current event, it should update the existing data
     */
    private void setResendMessage(Context context, SmsInvite smsInvite, int eventId, int timesSent) {

        Log.d("SentSmsStatusReceiver: setResendMessage","Setting a timer to resend messages");
        Log.i("SentSmsStatusReceiver: setResendMessage", "Message failed to go to " + smsInvite.getName());

        Intent i = new Intent(context, ResendSmsReceiver.class);

        SmsInvitationsList smsInvitationsList = SmsInvitationsList.readListFromFile(context);

        // If either there's no event stored, or if it's old, create a new one
        if (smsInvitationsList == null || smsInvitationsList.getEventId() != eventId) {
            smsInvitationsList = new SmsInvitationsList(eventId, timesSent);
        }

        Log.d("SentSmsStatusReceiver: smsInvitationsList", "getTimesSent(): " + smsInvitationsList.getTimesSent());
        Log.d("SentSmsStatusReceiver: smsInvitationsList", "timesSent: " + timesSent);

        // add the latest failed invite to the list
        smsInvitationsList.getSmsInviteList().add(smsInvite);
        smsInvitationsList.writeListToFile(context);

        Log.d("SentSmsStatusReceiver: setResendMessage", "Currently there are " +
                smsInvitationsList.getSmsInviteList().size() + " messages to resend");

        // is this actually necessary?
        i.putExtra(SendSmsService.SMS_INVITATIONS, (Parcelable) smsInvitationsList);
        PendingIntent pi = PendingIntent.getBroadcast(context, eventId,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        mgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

    }
}

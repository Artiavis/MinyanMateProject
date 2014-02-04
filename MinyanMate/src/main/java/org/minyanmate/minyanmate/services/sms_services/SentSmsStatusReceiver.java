package org.minyanmate.minyanmate.services.sms_services;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.Calendar;

/**
 * This BroadcastReceiver is passed as a pendingIntent to the SmsManager in methods of
 * {@link org.minyanmate.minyanmate.services.sms_services.SendSmsService} to receive the
 * sentIntent. It should be used to handle cases related to SMS failure.
 */
public class SentSmsStatusReceiver extends BroadcastReceiver {


    private static SmsInvitationsList smsInvitationsList = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        int resultCode = getResultCode();
        switch (resultCode) {
            // If successful, do nothing!
            case Activity.RESULT_OK:
                break;

            // If received an error code, try again using an alarm
            // Multiple messages can go to the same alarm using the same intent
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
            case SmsManager.RESULT_ERROR_NO_SERVICE:
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                // try again
                SmsInvite smsInvite = intent.getParcelableExtra(SendSmsService.SMS_INVITE);
                int eventId = intent.getIntExtra(SendSmsService.EVENT_ID, 0);
                int timesSent = intent.getIntExtra(SendSmsService.TIMES_SENT, 1);
                setResendMessage(context, smsInvite, eventId, timesSent);
                break;


            default:
                break;
        }
    }

    /**
     * This method gets called when automatically scheduling failed messages to resend. This method
     * creates a {@link android.app.PendingIntent} using the {@link SmsInvitationsList#eventId}
     * of a failed message if no previous intent exists (otherwise it recalls the existing one).
     * <p>
     * If {@link #resendMessageBundle} is null or contains references to messages from previous events,
     * it should be replaced. If it references a current event, it should update the existing data
     * (by copying it out, appending to it, and putting it back in).
     */
    private void setResendMessage(Context context, SmsInvite smsInvite, int eventId, int timesSent) {

        Intent i = new Intent(context, ResendSmsReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, eventId,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        // If either there's no event stored, or if it's old, create a new one
        if (smsInvitationsList == null || smsInvitationsList.getEventId() != eventId)
            smsInvitationsList = new SmsInvitationsList(eventId, timesSent);

        // add the latest failed invite to the list
        smsInvitationsList.getSmsInviteList().add(smsInvite);
        i.putExtra(SendSmsService.SMS_INVITATIONS, smsInvitationsList);

        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        mgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

//        if (resendMessageBundle == null)
//            resendMessageBundle = new Bundle();
//
//        // Get existing list of stuff
//        ArrayList<SmsInvitationsList> smsInvitationses = resendMessageBundle.getParcelableArrayList(SendSmsService.SMS_INVITATIONS);
//
//        // if null then list doesn't exist, ie create new one
//        if (smsInvitationses == null) {
//            smsInvitationses = new ArrayList<SmsInvitationsList>();
//            smsInvitationses.add(smsInvitations);
//            resendMessageBundle.putParcelableArrayList(SendSmsService.SMS_INVITATIONS, smsInvitationses);
//            i.putExtras(resendMessageBundle);
//        }
//
//        // else if the eventId's don't match up, the previous list is old, ditch it
//        else if ( ((SmsInvitationsList) smsInvitationses.get(0)).getEventId() != smsInvitations.getEventId()) {
//            smsInvitationses = new ArrayList<SmsInvitationsList>();
//            smsInvitationses.add(smsInvitations);
//            resendMessageBundle.putParcelableArrayList(SendSmsService.SMS_INVITATIONS, smsInvitationses);
//            i.putExtras(resendMessageBundle);
//        }
//
//        // else if the eventId's do match up, push to the back of the list
//        else if ( ((SmsInvitationsList) smsInvitationses.get(0)).getEventId() == smsInvitations.getEventId()) {
//            smsInvitationses.add(smsInvitations);
//        }
//
//        // afterwards place the bundle into a pendingintent
//        i.putExtras(resendMessageBundle);
//
//        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, 2);
//        mgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }
}

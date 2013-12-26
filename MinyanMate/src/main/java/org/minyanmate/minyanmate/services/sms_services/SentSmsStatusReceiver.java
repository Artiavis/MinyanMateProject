package org.minyanmate.minyanmate.services.sms_services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

/**
 * This BroadcastReceiver is passed as a pendingIntent to the SmsManager in methods of
 * {@link org.minyanmate.minyanmate.services.sms_services.SendSmsService} to receive the
 * sentIntent. It should be used to handle cases related to SMS failure.
 */
public class SentSmsStatusReceiver extends BroadcastReceiver {

    private static Bundle resendMessageBundle = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO use this method to handle statuses
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
                // TODO make a queue and put the messages into here
                break;


            default:
                break;
        }
    }

    /**
     * This method gets called when automatically scheduling failed messages to resend. This method
     * creates a {@link android.app.PendingIntent} using the {@link org.minyanmate.minyanmate.models.SmsInvite#eventId}
     * of a failed message if no previous intent exists (otherwise it recalls the existing one).
     * <p>
     * If {@link #resendMessageBundle} is null or contains references to messages from previous events,
     * it should be replaced. If it references a current event, it should update the existing data
     * (by copying it out, appending to it, and putting it back in).
     */
    private void setResendMessage() {

    }
}

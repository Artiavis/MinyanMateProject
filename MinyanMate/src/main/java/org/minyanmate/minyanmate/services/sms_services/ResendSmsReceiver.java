package org.minyanmate.minyanmate.services.sms_services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.R;

/**
 * This class receives a  {@link android.app.PendingIntent} to begin resending SMS invites contained
 * in a {@link android.os.Bundle} in the form of {@link SmsInvitationsList}.
 */
public class ResendSmsReceiver extends BroadcastReceiver {

    public static final int SEND_LIMIT = 3;

    public static final String MANUAL_RESEND = "manualResend";

    @Override
    public void onReceive(Context context, Intent intent) {

        SmsInvitationsList smsInvitationsList = intent.getParcelableExtra(SendSmsService.SMS_INVITATIONS);
        boolean manualResend = intent.getBooleanExtra(MANUAL_RESEND, false);

        if (smsInvitationsList != null) {
            // If not too many retries, automatically begin sending again
            if (smsInvitationsList.getTimesSent() < SEND_LIMIT || manualResend) {

                smsInvitationsList.incrementTimesSent();

                Intent i = new Intent(context, SendSmsService.class);
                i.putExtra(SendSmsService.SMS_INVITATIONS, smsInvitationsList);
                i.putExtra(SendSmsService.REQUEST_CODE, SendSmsService.RESEND_FAILED_INVITES);
                WakefulIntentService.sendWakefulWork(context, i);
            }

            // If there are too many retries, prompt the user to manually resend
            else {

                Intent i = new Intent(context, ResendSmsReceiver.class);
                i.putExtra(SendSmsService.SMS_INVITATIONS, smsInvitationsList);
                i.putExtra(MANUAL_RESEND, true);
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);

                NotificationManager nm = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);

                Notification n = new Notification.Builder(context)
                        .setContentTitle("Sending invites failed!")
                        .setContentText("Some invites failed to out. Press to resend?")
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .getNotification();

                nm.notify(0, n);

            }
        } else {
            Log.d("ResendSmsReceiver: onReceive","No smsInvitationsList object received!");
        }
    }
}

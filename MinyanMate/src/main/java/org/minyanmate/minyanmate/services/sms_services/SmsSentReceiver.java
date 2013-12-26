package org.minyanmate.minyanmate.services.sms_services;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This BroadcastReceiver is passed as a pendingIntent to the SmsManager in methods of
 * {@link org.minyanmate.minyanmate.services.sms_services.SendSmsService} to receive the
 * deliveryIntent.
 */
public class SmsSentReceiver extends BroadcastReceiver {

    public static final String REQUEST_CODE = "request_code";
    public static final int INVITE_RECEIVED = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
        switch (requestCode) {
            case INVITE_RECEIVED:
                // Extract data and store to database
                break;
            default:
                break;
        }
    }
}

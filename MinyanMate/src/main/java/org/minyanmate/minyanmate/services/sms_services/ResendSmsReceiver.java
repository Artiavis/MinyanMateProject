package org.minyanmate.minyanmate.services.sms_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class receives a  {@link android.app.PendingIntent} to begin resending SMS invites contained
 * in a {@link android.os.Bundle} in the form of {@link org.minyanmate.minyanmate.models.SmsInvite}.
 */
public class ResendSmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}

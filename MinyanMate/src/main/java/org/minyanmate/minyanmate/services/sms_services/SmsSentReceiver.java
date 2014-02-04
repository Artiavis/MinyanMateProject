package org.minyanmate.minyanmate.services.sms_services;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;

/**
 * This BroadcastReceiver is passed as a pendingIntent to the SmsManager in methods of
 * {@link org.minyanmate.minyanmate.services.sms_services.SendSmsService} to receive the
 * deliveryIntent.
 */
public class SmsSentReceiver extends BroadcastReceiver {

    public static final int INVITE_RECEIVED = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra(SendSmsService.REQUEST_CODE, 0);
        switch (requestCode) {
            case INVITE_RECEIVED:
                // Extract data and store to database
                insertSmsInviteRecord(context, intent.getExtras());
                break;
            default:
                break;
        }
    }

    private void insertSmsInviteRecord(Context context, Bundle bundle) {
        SmsInvite smsInvite = bundle.getParcelable(SendSmsService.SMS_INVITE);
        int eventId = bundle.getInt(SendSmsService.EVENT_ID);

        ContentValues inviteValues = new ContentValues();
        inviteValues.put(MinyanGoersTable.COLUMN_DISPLAY_NAME, smsInvite.getName());
        inviteValues.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE));
        inviteValues.put(MinyanGoersTable.COLUMN_IS_INVITED, 1);
        inviteValues.put(MinyanGoersTable.COLUMN_PHONE_NUMBER_ID, smsInvite.getPhoneNumberId());
        inviteValues.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, eventId);
        context.getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, inviteValues);
    }
}

package org.minyanmate.minyanmate.services.sms_services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.services.HeadcountUpdater;

import java.util.Locale;

/**
 * Checks whether a Minyan is active against the time stamps in 
 * {@link MinyanEventsTable} and whether the received Sms is from
 * someone in the {@link MinyanGoersTable}; if so, and if the
 * invited party has not previously responded (
 * {@link MinyanGoersTable#COLUMN_INVITE_STATUS} is 1), log their
 * attendance. 
 */
public class GotSmsReceiver extends BroadcastReceiver{

    public static final String POSITIVE_RESPONSE = "accept";
    public static final String NEGATIVE_RESPONSE = "decline";

    final SmsManager smsManager = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (Object aPdusObj : pdusObj) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    String phoneNumber = currentMessage.getOriginatingAddress();
                    String response = currentMessage.getDisplayMessageBody();


                    Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; response: " + response);

                    //If SMS doesn't contain one of the two predefined responses, don't attempt to process it
                    if (!response.toLowerCase(Locale.ENGLISH).contains(POSITIVE_RESPONSE) &&
                            !response.toLowerCase(Locale.ENGLISH).contains(NEGATIVE_RESPONSE))
                        return;

                    ContentResolver cr = context.getContentResolver();

                    //Get phone ID associated with phone number which sent SMS
                    Uri contentURI = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

                    Cursor c = cr.query(contentURI, new String[]{Phone.DISPLAY_NAME, Phone._ID},
                            null, null, null);

                    if (c.getCount() < 1) {
                        Log.e("SmsReceiver", "Unidentifiable Phone Number");
                        return;
                    }

                    c.moveToFirst();
                    String senderDisplayName = c.getString(c.getColumnIndex(Phone.DISPLAY_NAME));
                    long senderPhoneNumberId = c.getLong(c.getColumnIndex(Phone._ID));
                    Log.i("SmsReceiver", "Phone Number: " + phoneNumber);
                    Log.i("SmsReceiver", "Phone Number Id: " + senderPhoneNumberId);
                    c.close();

                    //Retrieve & update Goer row associated with this Display Name
                    contentURI = MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS;
                    //String[] projection = new String[] { MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, MinyanGoersTable.COLUMN_INVITE_STATUS, MinyanGoersTable.COLUMN_LOOKUP_KEY };
                    String[] projection = null;

                    String selection = MinyanGoersTable.COLUMN_PHONE_NUMBER_ID + " = ?"
                            + " AND " + MinyanGoersTable.QUERY_LATEST_GOERS;
                    String[] selectionArgs = new String[]{Long.toString(senderPhoneNumberId)};
                    String sortOrder = null;

                    c = cr.query(contentURI, projection, selection, selectionArgs, sortOrder);

                    if (c.getCount() < 1) {
                        Log.e("SmsReceiver", "Phone Number isn't associated with a MinyanMate Goer");
                        return;
                    }

                    c.moveToFirst();

                    int eventId = c.getInt(c.getColumnIndex(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID));
                    int inviteStatus = c.getInt(c.getColumnIndex(MinyanGoersTable.COLUMN_INVITE_STATUS));
                    InviteStatus responseCode = InviteStatus.AWAITING_RESPONSE;

                    switch (inviteStatus) {
                        case 2:
                        case 3:
                            Log.i("SmsReceiver", "Already received RSVP from " + senderDisplayName + ", updating to " + response + ".");
                    }

                    if (response.toLowerCase(Locale.ENGLISH).contains(POSITIVE_RESPONSE))
                        responseCode = InviteStatus.ATTENDING;
                    else if (response.toLowerCase(Locale.ENGLISH).contains(NEGATIVE_RESPONSE))
                        responseCode = InviteStatus.NOT_ATTENDING;

                    ContentValues goerUpdates = new ContentValues();
                    goerUpdates.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(responseCode));

                    selection = MinyanGoersTable.COLUMN_PHONE_NUMBER_ID + " = ? AND " + MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + " = ?";
                    selectionArgs = new String[]{Long.toString(senderPhoneNumberId), Integer.toString(eventId)};

                    int rowsUpdated = cr.update(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, goerUpdates, selection, selectionArgs);

                    if (rowsUpdated < 1)
                        Log.e("SmsReceiver", "MinyanMateGoersTable Update Failed");
                    else if (rowsUpdated > 1)
                        Log.e("SmsReceiver", "MinyanMateGoersTable Updated Too Many Rows");

                    c.close();

                    // This shouldn't be explicitly necessary anymore because of the database trigger
/*                    //Check how many Goers are going to the event, update event completion status if attendance count > 9
                    contentURI = MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS;
                    //projection = new String[] { MinyanGoersTable.COLUMN_GENERAL_NAME };
                    projection = null;
                    selection = MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + " = ? AND " +
                            MinyanGoersTable.COLUMN_INVITE_STATUS + "=" + Long.toString(InviteStatus.toInteger(InviteStatus.ATTENDING));
                    selectionArgs = new String[] { Integer.toString(eventId) };
                    sortOrder = null;

                    c = cr.query(contentURI, projection, selection, selectionArgs, null);

                    if(c.getCount() > 9) {

                        ContentValues eventUpdates = new ContentValues();
                        eventUpdates.put(MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE, 1);

                        selection = MinyanEventsTable.COLUMN_EVENT_ID + " = ?";
                        selectionArgs = new String[] { Integer.toString(eventId) };

                        rowsUpdated = cr.update(MinyanMateContentProvider.CONTENT_URI_EVENTS, eventUpdates, selection, selectionArgs);

                    }

                    c.close();*/


                    HeadcountUpdater.checkMinyanCompletionChange(context, eventId, false);

                    c.close();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " +e);

        }
    }

}
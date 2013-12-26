package org.minyanmate.minyanmate.services;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.MinyanMateActivity;
import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.services.sms_services.SendSmsService;

public class HeadcountUpdater {

    /**
     * Check whether the specified Minyan event has ten members and whether the user
     * was notified about it. If the count just hit ten and user doesn't know about it,
     * send a notification, and if it dropped below ten and the user doesn't know abou it,
     * also send a notification.
     * @param context the context of the BroadcastReceiver
     * @param eventId the id of the event
     * @param fromUI
     */
    public static void checkMinyanCompletionChange(Context context, int eventId, boolean fromUI) {

        ContentResolver cr = context.getContentResolver();

        Cursor c = cr.query(MinyanMateContentProvider.CONTENT_URI_EVENTS,
                null, MinyanEventsTable.COLUMN_EVENT_ID + "=?",
                new String[] { Integer.toString(eventId) }, null);
        c.moveToFirst();

        // Is the Minyan count complete ie > 9?
        boolean hasMinyan = c.getInt(c.getColumnIndex(
                MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE)) == 1;
        // If the minyan is complete, does the user know about it?
        boolean isMinyanNotified = c.getInt(c.getColumnIndex(
                MinyanEventsTable.COLUMN_MINYAN_COMPLETE_ALERTED)) == 1;
        c.close();

        boolean completedButNotNotified = hasMinyan && !isMinyanNotified;
        boolean notifiedButNotLongerComplete = isMinyanNotified && !hasMinyan;

        Log.d("checkMinyanCompletionChange", "hasMinyan: " + hasMinyan);
        Log.d("checkMinyanCompletionChange", "isMinyanNotified: " + isMinyanNotified);

        if (completedButNotNotified) {

            sendMinyanUpdates(true, context, eventId, fromUI);

        } else if (notifiedButNotLongerComplete) {

            sendMinyanUpdates(false, context, eventId, fromUI);
        }
    }

    public static String formattedHeadcountMessage(Context context, int eventId) {

        Cursor c = context.getContentResolver().query(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS,
                null, MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "=?",
                new String[]{Integer.toString(eventId)}, null);

        int inviteStatus;
        int[] headcountList = new int[InviteStatus.values().length]; // should be 3

        while (c.moveToNext()) {
            inviteStatus = c.getInt(MinyanMateContentProvider.GoerMatrix.INVITE_STATUS);
            headcountList[inviteStatus-1] ++;
        }
        c.close();

        int attendingCount = headcountList[InviteStatus.toInteger(InviteStatus.ATTENDING)-1];
        int awaitingCount = headcountList[InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE)-1];
        int notAttendingCount = headcountList[InviteStatus.toInteger(InviteStatus.NOT_ATTENDING)-1];
        int sum = attendingCount + awaitingCount + notAttendingCount;

        String headcountMsg = "Minyan Headcount:\n" +
                "Attending: " + attendingCount + "/" + sum + "\n" +
                "Awaiting Response: " + awaitingCount + "/" + sum + "\n" +
                "Not Attending: " + notAttendingCount + "/" + sum + "\n";

        return headcountMsg;
    }

    public static void sendMinyanUpdates(boolean hasMinyan, Context context, int eventId,
                                         boolean fromUI) {

        if (! fromUI)
            sendUpdateNotification(hasMinyan, context, eventId);

        sendUpdateSms(hasMinyan, context, eventId);

        ContentValues eventUpdates = new ContentValues();
        eventUpdates.put(MinyanEventsTable.COLUMN_MINYAN_COMPLETE_ALERTED, hasMinyan ? 1 : 0);
        context.getContentResolver().update(MinyanMateContentProvider.CONTENT_URI_EVENTS, eventUpdates,
                MinyanEventsTable.COLUMN_EVENT_ID + "=?",
                new String[] { Integer.toString(eventId) });

    }

    static void sendUpdateNotification(boolean hasMinyan, Context context, int eventId) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MinyanMateActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        String title;
        String msg;

        if (hasMinyan) {
            title = "Minyan Complete!";
            msg = "Your minyan now has 10 members";
        } else {
            title = "Minyan Incomplete!";
            msg = "Count dropped below 10";
        }

        Notification n = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(msg)
//                    .setStyle(new Notification.BigTextStyle().bigText(msg))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pi)
//                                .addAction(R.drawable.edit, "Send headcount", shareIntent)
                .setAutoCancel(true)
                .getNotification();

        notificationManager.notify(0,n);

    }

    static void sendUpdateSms(boolean hasMinyan, Context context, int eventId) {

        // Check whether to automatically send an update
        boolean isForwarding = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.isForwardingPreference),false);

        if (isForwarding) {

            Log.i("sendUpdateSms in HeadcountUpdater", "Sending update sms!");

            String msg = "Minyan " + (hasMinyan ? "" : "in") + "complete!\n" +
                    formattedHeadcountMessage(context, eventId);

            Intent i = new Intent(context, SendSmsService.class);
            i.putExtra(SendSmsService.REQUEST_CODE, SendSmsService.SEND_HEADCOUNT_UPDATE);
            i.putExtra(SendSmsService.UPDATE_MESSAGE, msg);
            WakefulIntentService.sendWakefulWork(context, i);
        }
    }
}

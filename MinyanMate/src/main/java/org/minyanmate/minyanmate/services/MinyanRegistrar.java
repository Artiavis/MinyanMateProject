package org.minyanmate.minyanmate.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A class which exposes static functionality to register, reschedule,
 * and cancel Minyan events using the {@link AlarmManager}.
 */
public class MinyanRegistrar {

	public static void registerMinyanEvent(Context context, MinyanSchedule sched, TimeZone timeZone) {
		
		Log.d("MinyanRegistrar", "Inside MinyanRegistrar registering minyan " + sched.getId());
		
		// Get alarm manager to set recurring alarm for minyan
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d("Minyan Registrar Registering schedule", " Schedule Id: " + sched.getId());
		PendingIntent pi = sendScheduledInvitesPendingIntent(context, sched.getId());

		Calendar date = new GregorianCalendar(timeZone);
		date.set(Calendar.HOUR_OF_DAY, sched.getHour());
		date.set(Calendar.MINUTE, sched.getMinute());
		date.set(Calendar.DAY_OF_WEEK, sched.getDayNum());
        date.set(Calendar.SECOND, 0);
        date.add(Calendar.MILLISECOND, (int) (-1*sched.getSchedulingWindowLength()));
		if (date.getTimeInMillis() < System.currentTimeMillis()) 
			date.add(Calendar.WEEK_OF_YEAR, 1);
		
		mgr.setRepeating(AlarmManager.RTC_WAKEUP,
                  date.getTimeInMillis(),
                  AlarmManager.INTERVAL_DAY*7,
                  pi);
		
		Log.d("Alarm Time", "Alarm should fire at " + date.toString());
	}

    private static  PendingIntent sendScheduledInvitesPendingIntent(Context context, int scheduleId) {
        Intent i =new Intent(context, OnMinyanAlarmReceiver.class);
        i.putExtra(SendSmsService.SCHEDULE_ID, scheduleId);
        i.putExtra(SendSmsService.REQUEST_CODE, SendSmsService.SEND_SCHEDULE_INVITES);
        return PendingIntent.getBroadcast(context, scheduleId, i, 0);
    }

    public static void updateMinyanRegistrar(Context context) {

        Cursor cursor =  context.getContentResolver().query(
                MinyanMateContentProvider.CONTENT_URI_SCHEDULES,
                null, null, null, null);

        MinyanRegistrar.registerMinyanEvents(context, cursor);
        cursor.close();
    }

	/**
	 * A brute force method meant to be called from the {@link MinyanMateContentProvider}
	 * whenever updating any schedule to guarantee that all schedules are updated and synchronized
	 * to go off. Most likely incurs a large performance penalty for performing every calculation
	 * every time. 
	 * @param context the {@link android.content.Context}
	 * @param cursor the {@link android.database.Cursor} to the {@link org.minyanmate.minyanmate.models.MinyanSchedule}s
     *               to schedule
	 */
	public static void registerMinyanEvents(Context context, Cursor cursor) {
		
		Log.d("MinyanRegistrar", "Inside MinyanRegistrar registering all minyans");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String timeZoneId = preferences.getString(context.getString(R.string.timezonePreference),"");
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
		while (cursor.moveToNext()) {
			
			MinyanSchedule sched = MinyanSchedule.schedFromCursor(cursor);
			
			// Try and cancel a minyan assuming one is scheduled
			cancelMinyanEvent(context, sched);
			
			// Register a minyan to be scheduled if it's marked active
			if (sched.isActive()) {
				registerMinyanEvent(context, sched, timeZone);
			}
		}
	}
	
	public static void rescheduleMinyanEvent(Context context, MinyanSchedule sched) {
		// TODO implement this using alarm managers
		// this may be replaceable using just a combination of cancelMinyanEvent
		// and then registerMinyanEvent, seeing as how database state is tricky with this op
		
		
	}
	
	public static void cancelMinyanEvent(Context context, MinyanSchedule sched) {
		
//		Log.d("MinyanRegistrar", "Inside MinyanRegistrar cancelling minyan " + sched.getId());
		
		// Get alarm manager to cancel recurring alarm for minyan
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		PendingIntent pi = sendScheduledInvitesPendingIntent(context, sched.getId());
		
		mgr.cancel(pi);
	}
}

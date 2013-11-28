package org.minyanmate.minyanmate.services;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

/**
 * A class which exposes static functionality to register, reschedule,
 * and cancel Minyan events using the {@link AlarmManager}.
 */
public class MinyanRegistrar {

	public static void registerMinyanEvent(Context context, MinyanSchedule sched) {		
		
		Log.d("MinyanRegistrar", "Inside MinyanRegistrar registering minyan " + sched.getId());
		
		// Get alarm manager to set recurring alarm for minyan
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				
		Intent i =new Intent(context, OnMinyanAlarmReceiver.class);
		i.putExtra("requestCode", sched.getId());
		PendingIntent pi = PendingIntent.getBroadcast(context, sched.getId(), i, 0);

		Calendar date = new GregorianCalendar();
		date.set(Calendar.HOUR_OF_DAY, sched.getHour());
		date.set(Calendar.MINUTE, sched.getMinute());
		date.set(Calendar.DAY_OF_WEEK, sched.getDayNum());
		if (date.getTimeInMillis() < System.currentTimeMillis()) 
			date.add(Calendar.WEEK_OF_YEAR, 1);
		date.add(Calendar.SECOND, (int) (-1*sched.getSchedulingWindowLength()));
		
		mgr.setRepeating(AlarmManager.RTC_WAKEUP,
                  date.getTimeInMillis(),
                  AlarmManager.INTERVAL_DAY*7,
                  pi);
		
		Log.d("Alarm Time", "Alarm should fire at " + date.toString());
	}
	
	/**
	 * A brute force method meant to be called from the {@link MinyanMateContentProvider}
	 * whenever updating any schedule to guarantee that all schedules are updated and synchronized
	 * to go off. Most likely incurs a large performance penalty for performing every calculation
	 * every time. 
	 * @param context
	 * @param cursor
	 */
	public static void registerMinyanEvents(Context context, Cursor cursor) {
		
		Log.d("MinyanRegistrar", "Inside MinyanRegistrar registering all minyans");
		
		while (cursor.moveToNext()) {
			
			MinyanSchedule sched = MinyanSchedule.schedFromCursor(cursor);
			
			// Try and cancel a minyan assuming one is scheduled
			cancelMinyanEvent(context, sched);
			
			// Register a minyan to be scheduled if it's marked active
			if (sched.isActive()) {
				registerMinyanEvent(context, sched);
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
		
		Intent i =new Intent(context, OnMinyanAlarmReceiver.class);
		i.putExtra("requestCode", sched.getId());
		PendingIntent pi = PendingIntent.getBroadcast(context, sched.getId(), i, 0);
		
		mgr.cancel(pi);
		// TODO test that this actually cancels the alarms
	}
}

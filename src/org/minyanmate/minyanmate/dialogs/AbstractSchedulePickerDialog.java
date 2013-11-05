package org.minyanmate.minyanmate.dialogs;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public abstract class AbstractSchedulePickerDialog extends DialogFragment 
implements TimePickerDialog.OnTimeSetListener {

	/* TODO move the business logic from the TimePickerFragment and the WindowSchedulePickerFragment
	*  classes into this interface and make them both inherit from it
	*/
	int id;
	int minute;
	int hour;
	MinyanSchedule schedule;
	
	public void initialize(int id, int hour, int minute, MinyanSchedule sched) {
		this.minute = minute;
		this.hour = hour;
		this.id = id;
		this.schedule = sched;
	}
	
	/**
	 * This method checks the validity of the insert operation against the adjacent minyans
	 * scheduled to possibly occur. It checks both forwards and backwards.
	 * @param context of the calling view
	 * @param thisScheduleHour is the desired hour (0-23) of the minyan
	 * @param thisScheduleMinute is the desired minute of the minyan 
	 * @param thisScheduleWindow is the desired scheduling window length of the minyan
	 * @param contentValues contains the updated columns 
	 */
	void saveSelection(Context context, int thisScheduleHour, int thisScheduleMinute,
			long thisScheduleWindow, ContentValues contentValues) {
		
		// ABS( thisID - _id ) = 1 finds immediately adjacent schedules
		
			Cursor adjacentSchedules = context.getContentResolver().query(
					MinyanMateContentProvider.CONTENT_URI_TIMES, 
					null, "ABS(" + id + "- " + MinyanSchedulesTable.COLUMN_ID + "=1" , null, 
					MinyanSchedulesTable.COLUMN_ID + "ASC");
			
			long thisWindowLength = this.schedule.getSchedulingWindowLength(); // assume constant
			long thisScheduleStartTime = 3600*thisScheduleHour + 60*thisScheduleMinute;
			long thisScheduleEndTime = thisScheduleStartTime + 30*60; // 30 minutes later
			
			if (id == 1 && adjacentSchedules.getCount() == 1 // if first event, only check forwards 
					&& adjacentSchedules.moveToFirst()) {
				
				MinyanSchedule nextSched = MinyanSchedule.schedFromCursor(adjacentSchedules);
				long nextWindowLength = nextSched.getSchedulingWindowLength();
				long nextSchedStartTime = nextSched.getHour() * 3600 + nextSched.getMinute() * 60;
				
				if ( thisScheduleEndTime + nextWindowLength < nextSchedStartTime) { // no need to check day-wrap-around
					context.getContentResolver().update(
							Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
							contentValues,
//								MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
							null, null
							);
				} else 
					Toast.makeText(context, "Failed to save! May be another minyan " +
							"too soon afterwards!", Toast.LENGTH_SHORT).show();
				
			} else if (adjacentSchedules.getCount() == 1     // else assume last event and only check backwards
					&& adjacentSchedules.moveToFirst()) {
				
				MinyanSchedule previousSched = MinyanSchedule.schedFromCursor(adjacentSchedules);
				
				long prevSchedEndTime = previousSched.getHour() * 3600 + previousSched.getMinute() * 60;
				
				if ( prevSchedEndTime + thisWindowLength >thisScheduleStartTime ) { // no need to check day-wrap-around
					context.getContentResolver().update(
							Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
							contentValues,
//								MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
							null, null
							);
				} else 
					Toast.makeText(context, "Failed to save! May be another minyan " +
							"scheduled too recently to this one!", Toast.LENGTH_SHORT).show();
				
			} else { /* otherwise assume somewhere in between and check both forwards and backwards
					  * being very careful about day-wrap-around - need to check both whether end/start of 
					  * day and if so wrap around, and if not don't
					  */
				
				adjacentSchedules.moveToFirst();			
				MinyanSchedule previousSched = MinyanSchedule.schedFromCursor(adjacentSchedules);
				
				long prevSchedEndTime = previousSched.getHour() * 3600 + previousSched.getMinute() * 60;
				
				adjacentSchedules.moveToNext();
				MinyanSchedule nextSched = MinyanSchedule.schedFromCursor(adjacentSchedules);
				
				long nextWindowLength = nextSched.getSchedulingWindowLength();
				long nextSchedStartTime = nextSched.getHour() * 3600 + nextSched.getMinute() * 60;
				
				boolean previousMinyanCanWrapDay = (previousSched.getPrayerNum() == 3 && schedule.getPrayerNum() == 1);
				boolean thisMinyanCanWrapDay = (schedule.getPrayerNum() == 3 && nextSched.getPrayerNum() == 1);
				
				/*
				 * If this minyan is at the beginning of the day, the previous minyan is at the end of the day, 
				 * can "wrap around" to today. To make sure that absolute time references are preserved, add 24 hours.
				 */
				
				thisScheduleStartTime += (previousMinyanCanWrapDay ? 24*3600 : 0);
				
				/*
				 * If this minyan is at the end of the day, the next minyan is at the beginning of the day, and this
				 * can "wrap around" to tomorrow. To make sure that absolute time references are preserved, add 24 hours.
				 */
				
				nextSchedStartTime += (thisMinyanCanWrapDay ? 24*3600 : 0);
				
				if ( prevSchedEndTime + thisWindowLength < thisScheduleStartTime) {
					
					if ( thisScheduleEndTime + nextWindowLength < nextSchedStartTime) {
						context.getContentResolver().update(
								Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
								contentValues,
//									MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
								null, null
								);
						
					} else 
						Toast.makeText(context, "Failed to save! May be another minyan " +
								"too soon afterwards!", Toast.LENGTH_SHORT).show();

				} else
					Toast.makeText(context, "Failed to save! May be another minyan " +
							"scheduled too recently to this one!", Toast.LENGTH_SHORT).show();
			}
	}
}

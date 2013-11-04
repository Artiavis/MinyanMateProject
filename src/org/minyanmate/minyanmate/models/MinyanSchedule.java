package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import org.minyanmate.minyanmate.database.MinyanDaysTable;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;

import android.database.Cursor;

/**
 * A model class containing the attributes of objects from the 
 * {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} table. Contains 
 * {@link MinyanSchedule#prayerFromCursor(Cursor)} and {@link MinyanSchedule#cursorToPrayerList(Cursor)}
 * static helper methods to pull Prayer objects from the cursors.
 */
public class MinyanSchedule {

	private int _id;
	private String day;
	private String prayerName;
	private int dayNum;
	private int prayerNum;
	private int hour;
	private int minute;
	private long scheduleWindowLength;
	private boolean isActive;
	private String inviteMessage;
	
	public MinyanSchedule(int id, String day, int dayNum, 
			int prayerNum, String prayerName, long winLen, int hour, int minute, 
			boolean isActive, String msg) {
		this(day, dayNum, prayerNum, prayerName, winLen, hour, minute, isActive, msg);
		this._id = id;
	}
	
	public MinyanSchedule(String day, int dayNum, int prayerNum, String prayerName, long winLen, int hour,
			int minute, boolean isActive, String msg) {
		this.day = day;
		this.dayNum = dayNum;
		this.prayerNum = prayerNum;
		this.hour = hour;
		this.minute = minute;
		this.isActive = isActive;
		this.scheduleWindowLength = winLen;
		this.prayerName = prayerName;
		this.inviteMessage = msg;
	}
	
	public int getPrayerNum() {
		return prayerNum;
	}
	
	public String getDay() {
		return day;
	}
	
	public int getDayNum() {
		return dayNum;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public boolean isActive() {
		return isActive;
	}
	
	public int getId() {
		return _id;
	}
	
	public long getSchedulingWindowLength() {
		return scheduleWindowLength;
	}
	
	public int getSchedulingWindowHours() {
		return (int) scheduleWindowLength / (3600);
	}
	
	public int getSchedulingWindowMinutes() {
		return (int) scheduleWindowLength % (3600);
	}
	
	public String getPrayerName() {
		return prayerName;
	}
	
	public String getInviteMessage() {
		return inviteMessage;
	}
	
	/**
	 * A helper function to extract the columns from a cursor over the 
	 * {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} and returns a new {@link MinyanSchedule} object
	 * based upon it. 
	 * <p>
	 * @param cursor a cursor over the {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} table
	 * which is already moved to a specific row. 
	 * @return prayer, a new {@link MinyanSchedule} object
	 */
	public static MinyanSchedule prayerFromCursor(Cursor cursor) {
		
		int id = cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_ID));			
		int dayNum = cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_DAY_NUM));
		long winLen = cursor.getLong(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_SCHEDULE_WINDOW));
		int hour = cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_PRAYER_HOUR));
		int min = cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_PRAYER_MIN));
		int prayerNum = cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_PRAYER_NUM));
		boolean isActive = (cursor.getInt(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_IS_ACTIVE)) == 1 ? true : false);
		String dayName = cursor.getString(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_DAY_NAME));
		String prayerName = cursor.getString(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_PRAYER_NAME));
		String invMsg = cursor.getString(cursor.getColumnIndex(MinyanSchedulesTable.COLUMN_SCHEDULE_MESSAGE));
		
		return new MinyanSchedule(id, dayName, dayNum, prayerNum, prayerName, winLen,hour, min, isActive, invMsg);
	}
	
	/**
	 * Given a cursor over a set of multiple results from the 
	 * {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} table, iterate over the cursor using
	 * {@link MinyanSchedule#prayerFromCursor(Cursor)} to generate a list of {@link MinyanSchedule} objects.
	 * <p>
	 * @param cursor a new cursor over the {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} table.
	 * @return prayerList, a list of new {@link MinyanSchedule} objects
	 */
	public static List<MinyanSchedule> cursorToPrayerList(Cursor cursor) {
		
		List<MinyanSchedule> prayerList = new ArrayList<MinyanSchedule>();
		while(cursor.moveToNext()) {
			prayerList.add(prayerFromCursor(cursor));
		}
		
		return prayerList;
	}
}

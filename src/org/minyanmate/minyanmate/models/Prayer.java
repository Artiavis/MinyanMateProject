package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import org.minyanmate.minyanmate.database.MinyanDaysTable;
import org.minyanmate.minyanmate.database.MinyanTimesTable;

import android.database.Cursor;

/**
 * A model class containing the attributes of objects from the 
 * {@link MinyanTimesTable#TABLE_MINYAN_TIMES} table. Contains 
 * {@link Prayer#prayerFromCursor(Cursor)} and {@link Prayer#cursorToPrayerList(Cursor)}
 * static helper methods to pull Prayer objects from the cursors.
 */
public class Prayer {

	private int _id;
	private String day;
	private String prayerName;
	private int dayNum;
	private int prayerNum;
	private int hour;
	private int minute;
	private long scheduleWindowLength;
	private boolean isActive;
	private List<Contact> contactList; // not yet used
	
	public Prayer(int id, String day, int dayNum, 
			int prayerNum, String prayerName, long winLen, int hour, int minute, boolean isActive) {
		this(day, dayNum, prayerNum, prayerName, winLen, hour, minute, isActive);
		this._id = id;
	}
	
	public Prayer(String day, int dayNum, int prayerNum, String prayerName, long winLen, int hour,
			int minute, boolean isActive) {
		this.day = day;
		this.dayNum = dayNum;
		this.prayerNum = prayerNum;
		this.hour = hour;
		this.minute = minute;
		this.isActive = isActive;
		this.scheduleWindowLength = winLen;
		this.prayerName = prayerName;
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
	
	public String getPrayerName() {
		return prayerName;
	}
	
	/**
	 * A helper function to extract the columns from a cursor over the 
	 * {@link MinyanTimesTable#TABLE_MINYAN_TIMES} and returns a new {@link Prayer} object
	 * based upon it. 
	 * <p>
	 * @param cursor a cursor over the {@link MinyanTimesTable#TABLE_MINYAN_TIMES} table
	 * which is already moved to a specific row. 
	 * @return prayer, a new {@link Prayer} object
	 */
	public static Prayer prayerFromCursor(Cursor cursor) {
		
		int id = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_ID));			
		int dayNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NUM));
		long winLen = cursor.getLong(cursor.getColumnIndex(MinyanTimesTable.COLUMN_SCHEDULE_WINDOW));
		int hour = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_HOUR));
		int min = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_MIN));
		int prayerNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_NUM));
		boolean isActive = (cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_IS_ACTIVE)) == 1 ? true : false);
		String dayName = cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NAME));
		String prayerName = cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_NAME));
		
		return new Prayer(id, dayName, dayNum, prayerNum, prayerName, winLen,hour, min, isActive);
	}
	
	/**
	 * Given a cursor over a set of multiple results from the 
	 * {@link MinyanTimesTable#TABLE_MINYAN_TIMES} table, iterate over the cursor using
	 * {@link Prayer#prayerFromCursor(Cursor)} to generate a list of {@link Prayer} objects.
	 * <p>
	 * @param cursor a new cursor over the {@link MinyanTimesTable#TABLE_MINYAN_TIMES} table.
	 * @return prayerList, a list of new {@link Prayer} objects
	 */
	public static List<Prayer> cursorToPrayerList(Cursor cursor) {
		
		List<Prayer> prayerList = new ArrayList<Prayer>();
		while(cursor.moveToNext()) {
			prayerList.add(prayerFromCursor(cursor));
		}
		
		return prayerList;
	}
}

package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import org.minyanmate.minyanmate.database.MinyanDaysTable;
import org.minyanmate.minyanmate.database.MinyanTimesTable;

import android.database.Cursor;

public class Prayer {

	private int _id;
	private String day;
	private String prayerName;
	private int dayNum;
	private int prayerNum;
	private int hour;
	private int minute;
	private boolean isActive;
	private List<Contact> contactList;
	
	public Prayer(int id, String day, int dayNum, 
			int prayerNum, String prayerName, int hour, int minute, boolean isActive) {
		this(day, dayNum, prayerNum, prayerName, hour, minute, isActive);
		this._id = id;
	}
	
	public Prayer(String day, int dayNum, int prayerNum, String prayerName, int hour,
			int minute, boolean isActive) {
		this.day = day;
		this.dayNum = dayNum;
		this.prayerNum = prayerNum;
		this.hour = hour;
		this.minute = minute;
		this.isActive = isActive;
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
	
	public String getPrayerName() {
		return prayerName;
	}
	
	public static Prayer prayerFromCursor(Cursor cursor) {
		
		int id = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_ID));			
		int dayNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NUM));
		int hour = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_HOUR));
		int min = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_MIN));
		int prayerNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_NUM));
		boolean isActive = (cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_IS_ACTIVE)) == 1 ? true : false);
		String dayName = cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NAME));
		String prayerName = cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_NAME));
		
		return new Prayer(id, dayName, dayNum, prayerNum, prayerName, hour, min, isActive);
	}
	
	public static List<Prayer> cursorToPrayerList(Cursor cursor) {
		
		List<Prayer> prayerList = new ArrayList<Prayer>();
		while(cursor.moveToNext()) {
			prayerList.add(prayerFromCursor(cursor));
		}
		
		return prayerList;
	}
}

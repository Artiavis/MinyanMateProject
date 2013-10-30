package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import org.minyanmate.minyanmate.database.MinyanDaysTable;
import org.minyanmate.minyanmate.database.MinyanTimesTable;

import android.database.Cursor;

public class Prayer {

	private int _id;
	private String day;
	private int dayNum;
	private int prayerNum;
	private int hour;
	private int minute;
	private boolean isActive;
	private List<Contact> contactList;
	
	public Prayer(int id, String day, int dayNum, 
			int prayerNum, int hour, int minute, boolean isActive) {
		this(day, dayNum, prayerNum, hour, minute, isActive);
		this._id = id;
	}
	
	public Prayer(String day, int dayNum, int prayerNum, int hour,
			int minute, boolean isActive) {
		this.day = day;
		this.dayNum = dayNum;
		this.prayerNum = prayerNum;
		this.hour = hour;
		this.minute = minute;
		this.isActive = isActive;
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
	
	public static List<Prayer> cursorToPrayerList(Cursor cursor) {
		
		List<Prayer> prayerList = new ArrayList<Prayer>();
		Prayer temp;
		while(cursor.moveToNext()) {
			
			int id = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_ID));			
			int dayNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NUM));
			int hour = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_HOUR));
			int min = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_MIN));
			int prayerNum = cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_NUM));
			boolean isActive = (cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_IS_ACTIVE)) == 1 ? true : false);
			
			String dayName = cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY_NAME));
			
			temp = new Prayer(id, dayName, dayNum, prayerNum, hour, min, isActive);
			prayerList.add(temp);
		}
		
		temp = null;
		
		return prayerList;
	}
}

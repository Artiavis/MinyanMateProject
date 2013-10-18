package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

public class Prayer {

	private int _id;
	private String day;
	private int dayNum;
	private int hour;
	private int minute;
	private boolean isActive;
	private List<Contact> contactList;
	
	public Prayer(int id, String day, int dayNum, 
			int hour, int minute, boolean isActive) {
		this(day, dayNum, hour, minute, isActive);
		this._id = id;
	}
	
	public Prayer(String day, int dayNum, int hour,
			int minute, boolean isActive) {
		this.day = day;
		this.dayNum = dayNum;
		this.hour = hour;
		this.minute = minute;
		this.isActive = isActive;
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
		// TODO convert cursor to model
		
		return prayerList;
	}
}

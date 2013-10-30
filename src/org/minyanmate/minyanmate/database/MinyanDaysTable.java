package org.minyanmate.minyanmate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

public class MinyanDaysTable {
	public static final String TABLE_MINYAN_DAYS = "minyan_days";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DAY_NUM = "day_num"; // 1 - 7
	public static final String COLUMN_DAY_NAME = "day_name"; // Sun - Saturday
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_DAYS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DAY_NUM + " int not null, " 
			+ COLUMN_DAY_NAME + " text not null" + ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		ContentValues day;
		
		for (int i = 1; i < 8; i++) {
			day = new ContentValues();
			day.put(COLUMN_DAY_NUM, i);
			day.put(COLUMN_DAY_NAME, days.get(i));
			database.insert(TABLE_MINYAN_DAYS, null, day);
		}
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_DAYS);
		onCreate(database);
	}
	
	private static final SparseArray<String> days = new SparseArray<String>(7);	
	static {
		days.append(1, "Sunday");
		days.append(2, "Monday");
		days.append(3, "Tuesday");
		days.append(4, "Wednesday");
		days.append(5, "Thursday");
		days.append(6, "Friday");
		days.append(7, "Saturday");
	}
	
}

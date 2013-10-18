package org.minyanmate.minyanmate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class MinyanTimesTable {

	public static final String TABLE_MINYAN_TIMES = "minyan_times";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DAY = "day_num"; // 1 - 7
	public static final String COLUMN_PRAYER_NUM = "prayer_num"; // 1 - 3
	public static final String COLUMN_PRAYER_HOUR = "prayer_hour"; // 0 - 23
	public static final String COLUMN_PRAYER_MIN = "prayer_min"; // 0 - 59
	public static final String COLUMN_IS_ACTIVE = "is_active"; // {0, 1} 
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_TIMES
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DAY + "int not null, " 
			+ COLUMN_PRAYER_NUM + "int not null, " 
			+ COLUMN_PRAYER_HOUR + "int not null, " 
			+ COLUMN_PRAYER_MIN + "int not null, "
			+ COLUMN_IS_ACTIVE + "int not null" + ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		ContentValues time;
		
		for (int i = 1; i < 7; i++) { // Sun - Fri
			for (int j = 1; j < 4; j++) { // Morn - Night
				if (i > 5 && j == 3) 
					break;
				
				time = new ContentValues();
				time.put(COLUMN_DAY, i);
				time.put(COLUMN_PRAYER_NUM, j);
				time.put(COLUMN_PRAYER_HOUR, 8 + 6*(j-1));
				time.put(COLUMN_PRAYER_MIN, 0);
				time.put(COLUMN_IS_ACTIVE, 0);
				database.insert(TABLE_MINYAN_TIMES, null, time);
			}
		}
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_TIMES);
		onCreate(database);
	}
}

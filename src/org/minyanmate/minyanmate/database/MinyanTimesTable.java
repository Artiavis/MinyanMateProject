package org.minyanmate.minyanmate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

public class MinyanTimesTable {

	public static final String TABLE_MINYAN_TIMES = "minyan_times";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DAY_NUM = "day_num"; // 1 - 7
	public static final String COLUMN_PRAYER_NUM = "prayer_num"; // 1 - 3
	public static final String COLUMN_PRAYER_HOUR = "prayer_hour"; // 0 - 23
	public static final String COLUMN_PRAYER_MIN = "prayer_min"; // 0 - 59
	public static final String COLUMN_IS_ACTIVE = "is_active"; // {0, 1} 
	public static final String COLUMN_DAY_NAME = "day_name"; // Sun - Saturday
	public static final String COLUMN_PRAYER_NAME = "prayer_name";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_TIMES
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DAY_NUM + " int not null, " 
			+ COLUMN_DAY_NAME + " text not null, "
			+ COLUMN_PRAYER_NUM + " int not null, " 
			+ COLUMN_PRAYER_NAME + " text not null, "
			+ COLUMN_PRAYER_HOUR + " int not null, " 
			+ COLUMN_PRAYER_MIN + " int not null, "
			+ COLUMN_IS_ACTIVE + " int not null" + ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		
		ContentValues time;
		
		for (int i = 1; i < 7; i++) { // Sun - Fri
			for (int j = 1; j < 4; j++) { // Morn - Night
				if (i > 5 && j == 3) 
					break;
				
				time = new ContentValues();
				time.put(COLUMN_DAY_NUM, i);
				time.put(COLUMN_PRAYER_NUM, j);
				time.put(COLUMN_PRAYER_HOUR, 8 + 6*(j-1));
				time.put(COLUMN_DAY_NAME, days.get(i));
				time.put(COLUMN_PRAYER_NAME, prayers.get(j));
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
	
	private static final SparseArray<String> prayers = new SparseArray<String>(3);
	static {
		prayers.append(1, "Shacharis");
		prayers.append(2, "Mincha");
		prayers.append(3, "Maariv");
	}
}

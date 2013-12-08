package org.minyanmate.minyanmate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;


/**
 * A class containing the column descriptions and {@link MinyanSchedulesTable#onCreate(SQLiteDatabase)}
 * and {@link MinyanSchedulesTable#onUpgrade(SQLiteDatabase, int, int)} commands. Used to dereference
 * columns from the {@link MinyanMateContentProvider}.
 */
public class MinyanSchedulesTable {

	public static final String TABLE_MINYAN_SCHEDULES = "minyan_schedules";
	
	/**
	 * Describes the unique integer identifier of a row.
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * An integer describing the day, from 1 - 7, with Sunday always corresponding
	 * to 1 and Saturday always corresponding to 7.
	 */
	public static final String COLUMN_DAY_NUM = "day_num"; 
	
	/**
	 * An integer enumerating the prayer service, from 1 - 3, with 1 always
	 * corresponding to Shacharis (morning prayer) and 3 always corresponding with
	 * Maariv (evening prayer).
	 */
	public static final String COLUMN_PRAYER_NUM = "schedule_num";
	
	/**
	 * A long in milliseconds describing the length of the time of the scheduling window
	 * assigned to a given prayer. 
	 * <p>
	 * For instance, the scheduling for Shacharis (morning
	 * prayer) may begin 10 or 11 hours in advance, ie the night before, to allow
	 * a sufficient amount of time for people to respond.
	 */
	public static final String COLUMN_SCHEDULE_WINDOW = "sched_win_len";
	
	/**
	 * An integer describing the hour of the prayer during that day 0 - 23.
	 */
	public static final String COLUMN_PRAYER_HOUR = "prayer_hour";
	
	/**
	 * An integer describing the minute of the prayer, 0 - 59.
	 */
	public static final String COLUMN_PRAYER_MIN = "prayer_min";
	
	/**
	 * A integer (which should be converted to a boolean) describing whether the specified
	 * minyan should be included in the scheduler.
	 * <p>
	 * 0 should be false, 1 should be true.
	 */
	public static final String COLUMN_IS_ACTIVE = "is_active";
	
	/**
	 * A string corresponding with {@link MinyanSchedulesTable#COLUMN_DAY_NUM}, Sunday - Saturday.
	 */
	public static final String COLUMN_DAY_NAME = "day_name";
	
	/**
	 * A string corresponding with {@link MinyanSchedulesTable#COLUMN_PRAYER_NUM}, indicating
	 * the name of the service (Shacharis, Mincha, Maariv).
	 */
	public static final String COLUMN_PRAYER_NAME = "prayer_name";
	
	public static final String COLUMN_SCHEDULE_MESSAGE = "invite_msg";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_SCHEDULES
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_DAY_NUM + " int not null, " 
			+ COLUMN_DAY_NAME + " text not null, "
			+ COLUMN_PRAYER_NUM + " int not null, " 
			+ COLUMN_PRAYER_NAME + " text not null, "
			+ COLUMN_SCHEDULE_WINDOW + " text nt null, "
			+ COLUMN_PRAYER_HOUR + " int not null, " 
			+ COLUMN_PRAYER_MIN + " int not null, "
			+ COLUMN_IS_ACTIVE + " int not null, " 
			+ COLUMN_SCHEDULE_MESSAGE + " text not null" + ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_SCHEDULES + "_index ON " + TABLE_MINYAN_SCHEDULES
			+ "(" 
			+ COLUMN_ID + ", "
			+ COLUMN_DAY_NUM + ", "
			+ COLUMN_PRAYER_NUM + ", " 
			+ COLUMN_IS_ACTIVE
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
		
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
				time.put(COLUMN_SCHEDULE_WINDOW, 3600);
				time.put(COLUMN_IS_ACTIVE, 0);
				time.put(COLUMN_SCHEDULE_MESSAGE, "Can you come to " + prayers.get(j) + "? Please " +
						"respond either \"!yes!\" or \"!no!\". Thank you.");
				database.insert(TABLE_MINYAN_SCHEDULES, null, time);
			}
		}
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_SCHEDULES);
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

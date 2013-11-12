package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table containing the id's and times of scheduled minyan events to which parties are invited
 * For the table of invited parties, see {@link MinyanGoersTable}.
 */
public class MinyanEventsTable {

	
	public static final String TABLE_MINYAN_EVENTS = "minyan_events";
	
	/**
	 * Returns an integer describing the unique id of the specific minyan event.
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * Returns a long in milliseconds describing the beginning of the scheduling period
	 * for the minyan.
	 */
	public static final String COLUMN_MINYAN_SCHEDULE_TIME = "scheduling_time";
	
	/**
	 * Returns a long in milliseconds describing the beginning of the minyan event.
	 */
	public static final String COLUMN_MINYAN_START_TIME = "start_time";
	
	/**
	 * Returns a long in milliseconds describing the end of the minyan event.
	 */
	public static final String COLUMN_MINYAN_END_TIME = "end_time";
	
	/**
	 * Returns an integer representing a boolean describing whether the minyan was completed
	 * by ten people responding affirmatively.
	 */
	public static final String COLUMN_IS_MINYAN_COMPLETE = "is_minyan_complete";
	
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_MINYAN_EVENTS
			+ "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_MINYAN_SCHEDULE_TIME + " int not null, " 
			+ COLUMN_MINYAN_START_TIME + " int not null, " 
			+ COLUMN_MINYAN_END_TIME + " int not null, " 
			+ COLUMN_IS_MINYAN_COMPLETE
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_EVENTS + "_index ON " + TABLE_MINYAN_EVENTS
			+ "(" 
			+ COLUMN_ID + ", "
			+ COLUMN_IS_MINYAN_COMPLETE
			+ ");";
			
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_EVENTS);
		onCreate(database);
	}
}

package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table containing the id's and times of scheduled minyan events to which parties are invited
 * For the table of invited parties, see {@link MinyanGoersTable}.
 */
public class MinyanEventsTable {

    public static final String QUERY_LATEST_EVENT = MinyanEventsTable.COLUMN_EVENT_ID +
            "= (SELECT MAX("
            + MinyanEventsTable.COLUMN_EVENT_ID + ") FROM "
            + MinyanEventsTable.TABLE_MINYAN_EVENTS + ")";

	public static final String TABLE_MINYAN_EVENTS = "minyan_events";
	
	/**
	 * Returns an integer describing the unique id of the specific minyan event.
	 */
	public static final String COLUMN_EVENT_ID = "event_id";
	
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

    /**
     * Returns an integer representing a boolean describing whether the minyan being completed in
     * {@link #COLUMN_IS_MINYAN_COMPLETE} was observed by the application.
     *
     * <p>When the application observes the minyan being completed, in the form of
     * {@link #COLUMN_IS_MINYAN_COMPLETE} being 1 and this column being 0, it should fire an
     * alert to the user and then toggle this column to 1.</p>
     *
     * <p>Conversely, if this column is 1 and {@link #COLUMN_IS_MINYAN_COMPLETE} is 0,
     * it implies that someone backed out, and an alert should be fired, after which this should
     * be toggled back to 0.</p>
     */
    // TODO implement the above alert functionality
    public static final String COLUMN_MINYAN_COMPLETE_ALERTED = "minyan_complete_alerted";

    /**
     * Returns the integer index of the Minyan Schedule, corresponding to
     * {@link org.minyanmate.minyanmate.database.MinyanSchedulesTable.COLUMN_SCHEDULE_ID}
     */
    public static final String COLUMN_MINYAN_SCHEDULE_ID = "minyan_schedule_id";

    /**
     * A string corresponding with {@link MinyanSchedulesTable#COLUMN_DAY_NUM}, Sunday - Saturday.
     */
    public static final String COLUMN_DAY_NAME = "event_day_name";

    /**
     * A string corresponding with {@link MinyanSchedulesTable#COLUMN_PRAYER_NUM}, indicating
     * the name of the service (Shacharis, Mincha, Maariv).
     */
    public static final String COLUMN_PRAYER_NAME = "event_prayer_name";

	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_MINYAN_EVENTS
			+ "("
			+ COLUMN_EVENT_ID + " integer primary key autoincrement, "
			+ COLUMN_MINYAN_SCHEDULE_TIME + " int not null, " 
			+ COLUMN_MINYAN_START_TIME + " int not null, " 
			+ COLUMN_MINYAN_END_TIME + " int not null, " 
			+ COLUMN_IS_MINYAN_COMPLETE + " int not null, "
            + COLUMN_MINYAN_COMPLETE_ALERTED + "int not null, "
            + COLUMN_MINYAN_SCHEDULE_ID + " int not null, "
            + COLUMN_DAY_NAME + " text, "
            + COLUMN_PRAYER_NAME + " text "
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_EVENTS + "_index ON " + TABLE_MINYAN_EVENTS
			+ "(" 
			+ COLUMN_EVENT_ID + ", "
			+ COLUMN_IS_MINYAN_COMPLETE + ", "
            + COLUMN_MINYAN_SCHEDULE_ID
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

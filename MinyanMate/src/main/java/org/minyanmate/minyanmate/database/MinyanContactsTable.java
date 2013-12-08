package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table containing a list of contacts for various minyan times and their lookup id's.
 *
 */
public class MinyanContactsTable {
	public static final String TABLE_MINYAN_CONTACTS = "minyan_contacts";
	
	/**
	 * Returns an integer describing the unique contact/prayer combination, ie the
	 * composite key.
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * Returns a string with the {@link android.provider.ContactsContract.CommonDataKinds.Phone._ID}
	 * for querying against the {@link android.provider.ContactsContract.CommonDataKinds.Phone#CONTENT_URI}.
	 */
	public static final String COLUMN_PHONE_NUMBER_ID = "contact_phone_id";
	
	/**
	 * Returns an integer with a primary key to the {@link MinyanSchedulesTable#TABLE_MINYAN_SCHEDULES} table.
	 */
	public static final String COLUMN_MINYAN_SCHEDULE_ID = "minyan_schedule_id";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_PHONE_NUMBER_ID + " integer not null, "
			+ COLUMN_MINYAN_SCHEDULE_ID + " integer not null, "
				+ "unique(" + COLUMN_PHONE_NUMBER_ID + ", "
				+ COLUMN_MINYAN_SCHEDULE_ID 
				+ ") on conflict replace, "
				+ "foreign key(" + COLUMN_MINYAN_SCHEDULE_ID + ") references "
				+ MinyanSchedulesTable.TABLE_MINYAN_SCHEDULES + "(" + MinyanSchedulesTable.COLUMN_ID
				+ ")" 
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_CONTACTS + "_index ON " + TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_ID
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_CONTACTS);
		onCreate(database);
	} 
}

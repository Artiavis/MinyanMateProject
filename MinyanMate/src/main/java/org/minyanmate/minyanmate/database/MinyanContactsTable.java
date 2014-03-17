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
    // To avoid collisions with ContactsContract, don't name "contact_id"
	public static final String COLUMN_MINYAN_CONTACT_ID = "minyan_contact_id";
	
	/**
	 * Returns a string with the {@link android.provider.ContactsContract.CommonDataKinds.Phone._ID}
	 * for querying against the {@link android.provider.ContactsContract.CommonDataKinds.Phone#CONTENT_URI}.
	 */
	public static final String COLUMN_PHONE_NUMBER_ID = "contact_phone_id";
	
	/**
	 * Returns an integer with a primary key to the {@link MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} table.
	 */
	public static final String COLUMN_MINYAN_SCHEDULE_ID = "minyan_schedule_id";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_MINYAN_CONTACT_ID + " integer primary key autoincrement, "
			+ COLUMN_PHONE_NUMBER_ID + " integer not null, "
			+ COLUMN_MINYAN_SCHEDULE_ID + " integer not null, "
				+ "unique(" + COLUMN_PHONE_NUMBER_ID + ", "
				+ COLUMN_MINYAN_SCHEDULE_ID 
				+ ") on conflict replace, "
				+ "foreign key(" + COLUMN_MINYAN_SCHEDULE_ID + ") references "
				+ MinyanPrayerSchedulesTable.TABLE_MINYAN_PRAYER_SCHEDULES + "(" + MinyanPrayerSchedulesTable.COLUMN_PRAYER_SCHEDULE_ID
				+ ")" 
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_CONTACTS + "_index ON " + TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_MINYAN_CONTACT_ID
			+ ");";

    /*
    * Created in Version 1
    * Changed slightly in Version 2
    * Not modified in Version 3
    * */


	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
        if (oldVersion == 1) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_CONTACTS);
            onCreate(database);
        } else if (oldVersion == 2 && newVersion == 3) {
            // do nothing!
        }
	} 
}

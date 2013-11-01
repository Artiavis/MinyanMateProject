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
	 * Returns a string with the {@link android.provider.ContactsContract.ContactsColumns.LOOKUP_KEY}
	 * for querying against the {@link android.provider.ContactsContract.Contacts#CONTENT_URI}.
	 */
	public static final String COLUMN_CONTACT_LOOKUP_KEY = "contact_lookup_key";
	
	/**
	 * Returns an integer with a primary key to the {@link MinyanTimesTable#TABLE_MINYAN_TIMES} table.
	 */
	public static final String COLUMN_MINYAN_TIME_ID = "minyan_time_id";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CONTACT_LOOKUP_KEY + " text, " 
			+ COLUMN_MINYAN_TIME_ID + " int not null, " 
				+ "unique(" + COLUMN_CONTACT_LOOKUP_KEY + ", "
				+ COLUMN_MINYAN_TIME_ID 
				+ ") on conflict replace"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_CONTACTS);
		onCreate(database);
	} 
}

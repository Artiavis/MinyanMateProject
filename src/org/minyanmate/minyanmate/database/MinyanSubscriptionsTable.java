package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

public class MinyanSubscriptionsTable {

	public static final String TABLE_SUBSCRIPTIONS = "subscriptions";
	
	public static final String COLUMN_ID = "_id";
	
	/**
	 * Returns a string with the {@link android.provider.ContactsContract.ContactsColumns.LOOKUP_KEY}.
	 */
	public static final String COLUMN_CONTACT_LOOKUP_KEY = "contact_lookup_key";
	
	/**
	 * Returns an integer representing a boolean as to whether a contact asked to not receive
	 * further communications.
	 */
	public static final String COLUMN_IS_SUBSCRIBED = "is_subscribed";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SUBSCRIPTIONS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CONTACT_LOOKUP_KEY + " text not null, " 
			+ COLUMN_IS_SUBSCRIBED + " int not null"
			+ ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
		onCreate(database);
	} 
}

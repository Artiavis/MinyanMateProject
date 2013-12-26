package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

public class MinyanSubscriptionsTable {

	public static final String TABLE_SUBSCRIPTIONS = "subscriptions";
	
	public static final String COLUMN_SUBSCRIBER_ID = "subscriber_id";
	
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
			+ COLUMN_SUBSCRIBER_ID + " integer primary key autoincrement, "
			+ COLUMN_CONTACT_LOOKUP_KEY + " text not null, " 
			+ COLUMN_IS_SUBSCRIBED + " int not null"
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_SUBSCRIPTIONS + "_index ON " + TABLE_SUBSCRIPTIONS
			+ "(" 
			+ COLUMN_SUBSCRIBER_ID + ", "
			+ COLUMN_CONTACT_LOOKUP_KEY
			+ ");";

    /*
    * Created in Version 1, still as-of-yet unused
    * Nothing changed in Version 2
    * Nothing changed in version 3
    * */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
        if (oldVersion == 1) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSCRIPTIONS);
            onCreate(database);
        } else if (oldVersion == 2 && newVersion == 3) {
            // do nothing!
        }
	} 
}

package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

public class MinyanContactsTable {
	public static final String TABLE_MINYAN_CONTACTS = "minyan_contacts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_LOOKUP_URI = "contact_lookup_uri";
	public static final String COLUMN_MINYAN_TIME_ID = "minyan_time_id";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MINYAN_CONTACTS
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_CONTACT_LOOKUP_URI + " text, " 
			+ COLUMN_MINYAN_TIME_ID + " int not null" + ");";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_CONTACTS);
		onCreate(database);
	} 
}

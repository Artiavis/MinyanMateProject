package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table of the parties invited to attend a minyan and their identifying information.
 * If a party was automatically invited via a text message, they are identified as not
 * random via {@link #COLUMN_IS_INVITED}, otherwise they are marked as random. This column
 * identifies the Single-Table Inheritance of this table.
 */
public class MinyanGoersTable {

	public static final String TABLE_MINYAN_INVITEES = "minyan_goers";
	
	/**
	 * Returns an integer describing the id of an invited party to a minyan.
	 */
	public static final String COLUMN_ID = "_id";
	
	/**
	 * Returns an integer describing the id of the minyan event associated
	 * with this contact. Functions as the primary key to {@link MinyanEventsTable}.
	 */
	public static final String COLUMN_MINYAN_EVENT_ID = "minyan_event_id";

	/**
	 * Returns an integer describing a boolean to identify whether the described 
	 * party was invited via text message or simply logged as attending manually.
	 * Functions as the Type for this table's Single-Table Inheritance. 
	 * <p>
	 * If false,
	 * refer to the party's {@link #COLUMN_GENERAL_NAME} column
	 * for their name. Otherwise, refer to their {@link #COLUMN_LOOKUP_KEY}
	 * to dereference their associated information. 
	 */
	public static final String COLUMN_IS_INVITED = "is_invited";
	
	/**
	 * Returns a string with the party's lookup_key if they are in the user's contacts
	 * and were invited to the minyan via text message, and null otherwise. 
	 * See {@link #COLUMN_IS_INVITED}.
	 */
	public static final String COLUMN_LOOKUP_KEY = "lookup_key";
	
	/**
	 * Returns a string with the party's name as entered by the user if the party
	 * was manually entered as attending the event, or a copy of the contact's name
	 * as entered in the phone at the time this record was created. See
	 * {@link #COLUMN_IS_INVITED}.
	 */
	public static final String COLUMN_GENERAL_NAME = "general_name";
	
	/**
	 * Returns an integer to identify the status of the invited party. Is guaranteed
	 * to be 2 for manually entered parties, see {@link #COLUMN_IS_INVITED}.
	 * <p>
	 * 1: Party was invited but has not yet responded. <p>
	 * 2: Party was invited and responded affirmatively. <p>
	 * 3: Party was invited and responded negatively.
	 */
	public static final String COLUMN_INVITE_STATUS = "invite_status";
	
	
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_MINYAN_INVITEES 
			+ "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_MINYAN_EVENT_ID + " integer not null, " 
			+ COLUMN_IS_INVITED + " integer not null, " 
			+ COLUMN_LOOKUP_KEY + " text, " 
			+ COLUMN_GENERAL_NAME + " text, "
			+ COLUMN_INVITE_STATUS + " integer not null, "
			+ "foreign key(" + COLUMN_MINYAN_EVENT_ID + ") references " 
				+ MinyanEventsTable.TABLE_MINYAN_EVENTS + "(" + MinyanEventsTable.COLUMN_ID 
				+ ") "
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_INVITEES + "_index ON " + TABLE_MINYAN_INVITEES
			+ "(" 
			+ COLUMN_ID + "," + COLUMN_INVITE_STATUS
			+ ");";
	

	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_INVITEES);
		onCreate(database);
	}
	
}

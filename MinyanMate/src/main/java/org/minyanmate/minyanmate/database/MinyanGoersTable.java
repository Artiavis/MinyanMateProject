package org.minyanmate.minyanmate.database;

import android.database.sqlite.SQLiteDatabase;

import org.minyanmate.minyanmate.models.InviteStatus;

/**
 * A table of the parties invited to attend a minyan and their identifying information.
 * If a party was automatically invited via a text message, they are identified as not
 * random via {@link #COLUMN_IS_INVITED}, otherwise they are marked as random. This column
 * identifies the Single-Table Inheritance of this table.
 */
public class MinyanGoersTable {

    public static final String QUERY_LATEST_GOERS = MinyanGoersTable.COLUMN_MINYAN_EVENT_ID +
            "= (SELECT MAX("
            + MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + ") FROM "
            + MinyanGoersTable.TABLE_MINYAN_INVITEES + ")";


	public static final String TABLE_MINYAN_INVITEES = "minyan_goers";
	
	/**
	 * Returns an integer describing the id of an invited party to a minyan.
	 */
	public static final String COLUMN_GOER_ID = "minyan_goer_id";
	
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
	 * refer to the party's {@link #COLUMN_DISPLAY_NAME} column
	 * for their name. Otherwise, refer to their {@link #COLUMN_PHONE_NUMBER_ID}
	 * to dereference their associated information. 
	 */
	public static final String COLUMN_IS_INVITED = "is_invited";
	
	/**
	 * Returns a string with the party's phone number id if they are in the user's contacts
	 * and were invited to the minyan via text message, and null otherwise. 
	 * See {@link #COLUMN_IS_INVITED}.
	 */
	public static final String COLUMN_PHONE_NUMBER_ID = "goer_phone_number_id";
	
	/**
	 * Returns a string with the party's name as entered by the user if the party
	 * was manually entered as attending the event, or a copy of the contact's name
	 * as entered in the phone at the time this record was created. See
	 * {@link #COLUMN_IS_INVITED}.
	 */
	public static final String COLUMN_DISPLAY_NAME = "goer_display_name";
	
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
			+ COLUMN_GOER_ID + " integer primary key autoincrement, "
			+ COLUMN_MINYAN_EVENT_ID + " integer not null, " 
			+ COLUMN_IS_INVITED + " integer not null, " 
			+ COLUMN_PHONE_NUMBER_ID + " integer, "
			+ COLUMN_DISPLAY_NAME + " text, "
			+ COLUMN_INVITE_STATUS + " integer not null, "
			+ "foreign key(" + COLUMN_MINYAN_EVENT_ID + ") references " 
				+ MinyanEventsTable.TABLE_MINYAN_EVENTS + "(" + MinyanEventsTable.COLUMN_EVENT_ID
				+ ") "
			+ ");";
	
	private static final String DATABASE_INDEX = "create index "
			+ TABLE_MINYAN_INVITEES + "_index ON " + TABLE_MINYAN_INVITEES
			+ "(" 
			+ COLUMN_GOER_ID + "," + COLUMN_INVITE_STATUS + "," + COLUMN_MINYAN_EVENT_ID + ","
                + COLUMN_PHONE_NUMBER_ID
			+ ");";


    private static final String TRIGGER_ON_INSERT_IS_MINYAN_COMPLETE = "on_insert_is_minyan_complete";

    /**
     * Trigger to update {@link org.minyanmate.minyanmate.database.MinyanEventsTable#COLUMN_IS_MINYAN_COMPLETE}
     * automatically after an insert into this table. The column should be 'automagically' correct.
     */

    /*
    Trigger prototype:

    CREATE TRIGGER update_is_minyan_complete AFTER INSERT ON minyan_goers
    BEGIN
        UPDATE minyan_events SET is_minyan_complete = (SELECT(
         SELECT COUNT(*) FROM (
          SELECT * FROM minyan_goers
            WHERE minyan_event_id = new.minyan_event_id AND invite_status = 1
        )
      ) > 9) WHERE event_id = new.minyan_event_id;
     END;
    * */
    private static final String DATABASE_INSERT_TRIGGER = "CREATE TRIGGER " + TRIGGER_ON_INSERT_IS_MINYAN_COMPLETE +
            " AFTER INSERT ON " + TABLE_MINYAN_INVITEES +
            " BEGIN " +
            " UPDATE " + MinyanEventsTable.TABLE_MINYAN_EVENTS + " SET " +
            MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE +
            " = (SELECT(" +
            "SELECT COUNT(" + COLUMN_INVITE_STATUS + ") FROM (" +
            "SELECT * FROM " + TABLE_MINYAN_INVITEES + " WHERE " + MinyanGoersTable.COLUMN_MINYAN_EVENT_ID +
            " = new." + COLUMN_MINYAN_EVENT_ID + " AND " + COLUMN_INVITE_STATUS + "="
            + InviteStatus.toInteger(InviteStatus.ATTENDING) +
            ")" +
            ") >= 10) WHERE " + MinyanEventsTable.COLUMN_EVENT_ID +
                      "=" + "new." + COLUMN_MINYAN_EVENT_ID + "; " +
            "END;";

    private static final String TRIGGER_ON_DELETE_IS_MINYAN_COMPLETE = "on_delete_is_minyan_complete";

    private static final String DATABASE_DELETE_TRIGGER = "CREATE TRIGGER " + TRIGGER_ON_DELETE_IS_MINYAN_COMPLETE +
            " AFTER DELETE ON " + TABLE_MINYAN_INVITEES +
            " BEGIN " +
            " UPDATE " + MinyanEventsTable.TABLE_MINYAN_EVENTS + " SET " +
            MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE +
            " = (SELECT(" +
            "SELECT COUNT(" + COLUMN_INVITE_STATUS + ") FROM (" +
            "SELECT * FROM " + TABLE_MINYAN_INVITEES + " WHERE " + MinyanGoersTable.COLUMN_MINYAN_EVENT_ID +
            " = new." + COLUMN_MINYAN_EVENT_ID + " AND " + COLUMN_INVITE_STATUS + "="
            + InviteStatus.toInteger(InviteStatus.ATTENDING) +
            ")" +
            ") >= 10) WHERE " + MinyanEventsTable.COLUMN_EVENT_ID +
            "=" + "new." + COLUMN_MINYAN_EVENT_ID + "; " +
            "END;";

    private static final String TRIGGER_ON_UPDATE_IS_MINYAN_COMPLETE = "on_update_is_minyan_complete";

    /**
     * Trigger to update {@link org.minyanmate.minyanmate.database.MinyanEventsTable#COLUMN_IS_MINYAN_COMPLETE}
     * automatically after an update of {@link #COLUMN_INVITE_STATUS}. The column should be 'automagically' correct.
     */

    private static final String DATABASE_UPDATE_TRIGGER = "CREATE TRIGGER " +
            TRIGGER_ON_UPDATE_IS_MINYAN_COMPLETE +
            " AFTER UPDATE OF " + COLUMN_INVITE_STATUS + " ON " + TABLE_MINYAN_INVITEES +
            " BEGIN " +
            "UPDATE " + MinyanEventsTable.TABLE_MINYAN_EVENTS + " SET " +
            MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE +
            " = (SELECT(" +
            "SELECT COUNT(" + COLUMN_INVITE_STATUS + ") FROM (" +
            "SELECT * FROM " + TABLE_MINYAN_INVITEES + " WHERE " + MinyanGoersTable.COLUMN_MINYAN_EVENT_ID +
            " = new." + COLUMN_MINYAN_EVENT_ID + " AND " + COLUMN_INVITE_STATUS + "="
            + InviteStatus.toInteger(InviteStatus.ATTENDING) +
            ")" +
            ") >= 10) WHERE " + MinyanEventsTable.COLUMN_EVENT_ID +
                    "=" + "new." + COLUMN_MINYAN_EVENT_ID + "; " +
            "END;";

    /*
    * Created in Version 1
    * Changed added misspelled triggers onUpdate and onInsert in Version 2
    * Corrected spelling of triggers and other mistakes in Version 3
    * */
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INDEX);
        database.execSQL(DATABASE_INSERT_TRIGGER);
        database.execSQL(DATABASE_UPDATE_TRIGGER);
        database.execSQL(DATABASE_DELETE_TRIGGER);
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
        if (oldVersion == 1) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_MINYAN_INVITEES);
            onCreate(database);
        } else if (oldVersion == 2 && newVersion == 3) {
            // the trigger names were misspelled...
            database.execSQL("DROP TRIGGER IF EXISTS on_insert_is_minyan_completeAFTER"); // typos :(
            database.execSQL("DROP TRIGGER IF EXISTS on_update_is_minyan_completeAFTER"); // typos :(
            database.execSQL(DATABASE_INSERT_TRIGGER);
            database.execSQL(DATABASE_UPDATE_TRIGGER);
            database.execSQL(DATABASE_DELETE_TRIGGER);
        }
	}
}

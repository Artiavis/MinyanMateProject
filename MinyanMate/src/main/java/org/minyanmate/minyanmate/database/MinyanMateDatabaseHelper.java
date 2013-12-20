package org.minyanmate.minyanmate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MinyanMateDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "minyanmate.db";
	// Updated to 2 on Dec 11 2013
    // Updated to 3 on Dec 19 2013
    private static final int DATABASE_VERSION = 3;
	
	public MinyanMateDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		
		if (!db.isReadOnly())
			db.execSQL("PRAGMA foreign_keys=ON;");
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		
		MinyanSchedulesTable.onCreate(database);
		MinyanContactsTable.onCreate(database);
		MinyanEventsTable.onCreate(database);
		MinyanGoersTable.onCreate(database);
		MinyanSubscriptionsTable.onCreate(database);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		MinyanSchedulesTable.onUpgrade(database, oldVersion, newVersion);
		MinyanContactsTable.onUpgrade(database, oldVersion, newVersion);
		MinyanEventsTable.onUpgrade(database, oldVersion, newVersion);
		MinyanGoersTable.onUpgrade(database, oldVersion, newVersion);
		MinyanSubscriptionsTable.onUpgrade(database, oldVersion, newVersion);
	}
}

package org.minyanmate.minyanmate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MinyanMateDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "minyanmate.db";
	private static final int DATABASE_VERSION = 1;
	
	public MinyanMateDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		MinyanTimesTable.onCreate(database);
		MinyanDaysTable.onCreate(database);
		MinyanContactsTable.onCreate(database);
		// TODO create all the other tables here
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		MinyanDaysTable.onUpgrade(database, oldVersion, newVersion);
		MinyanTimesTable.onUpgrade(database, oldVersion, newVersion);
		MinyanContactsTable.onUpgrade(database, oldVersion, newVersion);
		// TODO upgrade all the other tables here
	}
}

package org.minyanmate.minyanmate.contentprovider;

import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanMateDatabaseHelper;
import org.minyanmate.minyanmate.database.MinyanTimesTable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.Data;

public class MinyanMateContentProvider extends ContentProvider {

	private MinyanMateDatabaseHelper database;
	
	// used for the UriMatcher
	private static final int TIMES = 1;
	private static final int TIME_ID = 2;
	private static final int CONTACTS = 3;
	private static final int CONTACT_ID = 4;
	
	
	private static final String AUTHORITY = "org.minyanmate.minyanmate.contentprovider";
	private static final String PATH_TIMES = "times";
	private static final String PATH_CONTACTS = "contacts";
	
	public static final Uri CONTENT_URI_TIMES = Uri.parse("content://" + AUTHORITY + "/" + PATH_TIMES);
	public static final Uri CONTENT_URI_CONTACTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_CONTACTS);
	
	// Not really sure what these do but they seem to be used for passing MIME types for intents...
	public static final String CONTENT_TYPE_TIME = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.minyanmate.times";
	public static final String CONTENT_ITEM_TYPE_TIME = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.minyanmate.time";
	public static final String CONTENT_TYPE_CONTACT = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.minyanmate.contacts";
	public static final String CONTENT_ITEM_TYPE_CONTACT = ContentResolver.CURSOR_ITEM_BASE_TYPE + "vnd.minyanmate.contact";
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PATH_CONTACTS, CONTACTS);
		sURIMatcher.addURI(AUTHORITY, PATH_CONTACTS + "/*", CONTACT_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_TIMES, TIMES);
		sURIMatcher.addURI(AUTHORITY, PATH_TIMES + "/#", TIME_ID);
	}
	
	@Override
	public boolean onCreate() {
		database = new MinyanMateDatabaseHelper(getContext());
		return false;
	}	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor;
		
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		
			case TIME_ID:
				queryBuilder.appendWhere(MinyanTimesTable.COLUMN_ID + "=" + uri.getLastPathSegment());
				// Fall through
			case TIMES:
				queryBuilder.setTables(MinyanTimesTable.TABLE_MINYAN_TIMES);
				cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				return cursor;


			case CONTACT_ID:
				queryBuilder.appendWhere(MinyanContactsTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			case CONTACTS:
				
				/**
				 * use CursorJoiner and MatrixCursor to create a cursor of a data abstraction
				 * representing the JOIN of the stored contact keys and the phone's contact info
				 */
				
				queryBuilder.setTables(MinyanContactsTable.TABLE_MINYAN_CONTACTS);
				cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, 
						MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY + " asc");
				cursor.setNotificationUri(getContext().getContentResolver(), uri);
				
				String[] desiredAttributes = new String[] { 
						Contacts._ID,
						Contacts.PHOTO_THUMBNAIL_URI,
						Contacts.DISPLAY_NAME,
//						Data.DATA1,
						Contacts.LOOKUP_KEY
				};				
				
				Cursor contacts = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
						desiredAttributes, null, null, Contacts.LOOKUP_KEY + " asc");
				
				MatrixCursor m = new MatrixCursor(desiredAttributes);
				
				CursorJoiner joiner = new CursorJoiner(cursor, 
						new String[] { MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY }, 
						contacts, new String[] { Contacts.LOOKUP_KEY });
				
				for (CursorJoiner.Result joinerResult : joiner) {
					switch (joinerResult) {
					case LEFT: // Ignore LEFT JOIN
						break;
					case RIGHT: // Ignore RIGHT JOIN
						break;
					case BOTH: // Only do things on inner joins
						
						m.addRow(new Object[] {
							contacts.getLong(contacts.getColumnIndex(Contacts._ID)),
							contacts.getString(contacts.getColumnIndex(Contacts.PHOTO_THUMBNAIL_URI)),
							contacts.getString(contacts.getColumnIndex(Contacts.DISPLAY_NAME)),
//							contacts.getString(contacts.getColumnIndex(Data.DATA1)),
							contacts.getString(contacts.getColumnIndex(Contacts.LOOKUP_KEY))
						});
						
						break;
					}
				}
				
				return m;
			
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
			case CONTACTS:
				rowsDeleted = db.delete(MinyanContactsTable.TABLE_MINYAN_CONTACTS, 
						selection, selectionArgs);
				break;
				
			case CONTACT_ID:
				rowsDeleted = db.delete(MinyanContactsTable.TABLE_MINYAN_CONTACTS, 
						MinyanContactsTable.COLUMN_ID + "=?", new String[] { uri.getLastPathSegment()});
				break;
				
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	// TODO eventually implementing this could be a good idea
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDb = database.getWritableDatabase();
		long id = 0;
		switch(uriType) {
		case TIMES:
			// TODO delete this, shouldn't be able to add times
			// id = sqlDb.insert(MinyanTimesTable.TABLE_MINYAN_TIMES, null, values);	
//			getContext().getContentResolver().notifyChange(uri, null);
//			return Uri.parse(PATH_CONTACTS + "/" + id);
			
		case CONTACTS:
			id = sqlDb.insert(MinyanContactsTable.TABLE_MINYAN_CONTACTS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(PATH_CONTACTS + "/" + id);
		
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}



	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
			case TIMES:
				rowsUpdated = db.update(MinyanTimesTable.TABLE_MINYAN_TIMES,
						values, selection, selectionArgs);
				break;
			case TIME_ID:
				// TODO implement this
				rowsUpdated = db.update(MinyanTimesTable.TABLE_MINYAN_TIMES, values,
						MinyanTimesTable.COLUMN_ID + "=?", new String[] { uri.getLastPathSegment() });
				break;
		
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}

package org.minyanmate.minyanmate.models;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanGoersTable;

import android.content.ContentValues;
import android.content.Context;

public class UninvitedMinyanGoer extends MinyanGoer {

	UninvitedMinyanGoer(int goerId, String name, int eventId, boolean isInvited) {
		super(goerId, name, eventId, isInvited);
	}

//	@Override
//	public int updateStatus(Context context, ContentValues values) {
//		
//		int rowsAffected = context.getContentResolver().update(
//				MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, 
//				values, MinyanGoersTable.COLUMN_RANDOM_NAME + "=? AND " 
//				+ MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "=?", 
//				new String[] { name, String.valueOf(eventId) });
//		
//		return rowsAffected;
//		
//	}
//
//	@Override
//	public int delete(Context context) {
//		
//		int rowsAffected = context.getContentResolver().delete(
//				MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, 
//				MinyanGoersTable.COLUMN_RANDOM_NAME + "=? AND " 
//				+ MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "=?", 
//				new String[] { name, String.valueOf(eventId) });
//		
//		return rowsAffected;
//		
//	}
	
	
}

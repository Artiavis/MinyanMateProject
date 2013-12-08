package org.minyanmate.minyanmate.models;

public class UninvitedMinyanGoer extends MinyanGoer {

	public UninvitedMinyanGoer(int goerId, String name, int eventId, InviteStatus status, boolean isInvited) {
		super(goerId, name, eventId, isInvited, status);
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

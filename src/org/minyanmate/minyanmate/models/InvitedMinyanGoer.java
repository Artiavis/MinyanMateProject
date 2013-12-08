package org.minyanmate.minyanmate.models;

public class InvitedMinyanGoer extends MinyanGoer {

	private int contactId;
	private String photoUri;
	private String phoneNumber;
	private String lookUpKey;
	
	InvitedMinyanGoer(int goerId, int contactId, String name, int eventId, InviteStatus status, boolean isInvited, 
			String photoUri, String phoneNum, String key) {
		super(goerId, name, eventId, isInvited, status);
		this.photoUri = photoUri;
		this.phoneNumber = phoneNum;
		this.lookUpKey = key;
		this.contactId = contactId;
	}
	
	public int getContactId() {
		return contactId;
	}
	
	public String getPhotoThumbnailUri() {
		return photoUri;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public String getLookupKey() {
		return lookUpKey;
	}

//	@Override
//	public int updateStatus(Context context, ContentValues values) {
//		
//		int rowsAffected = context.getContentResolver().update(
//				MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, 
//				values, MinyanGoersTable.COLUMN_PHONE_NUMBER_ID + "=? AND "
//				+ MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "=?", 
//				new String[] { lookUpKey, String.valueOf(eventId) });
//		
//		return rowsAffected;
//	}
//
//	@Override
//	public int delete(Context context) {
//
//		int rowsAffected = context.getContentResolver().delete(
//				MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, 
//				MinyanGoersTable.COLUMN_PHONE_NUMBER_ID + "=? AND "
//						+ MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "=?", 
//						new String[] { lookUpKey, String.valueOf(eventId) });
//		
//		return rowsAffected;
//	}

}

package org.minyanmate.minyanmate.models;

import android.database.Cursor;
import android.database.MatrixCursor;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.GoerMatrix;

import java.util.ArrayList;
import java.util.List;

public abstract class MinyanGoer {
	
	int goerId;
	String name;
	int eventId;
	InviteStatus status;
	boolean isInvited;
	
	
	MinyanGoer(int id, String name, int eventId, boolean isInvited, InviteStatus status) {
		this.goerId = id;
		this.name = name;
		this.eventId = eventId;
		this.isInvited = isInvited;
		this.status = status;
	}
	
	public int getMinyanGoerId() {
		return goerId;
	}
	
	public String getName() {
		return name;
	}
	
	public int getEventId() {
		return eventId;
	}
	
	public boolean isInvited() {
		return isInvited;
	}
	
	public InviteStatus getInviteStatus() {
		return status;
	}
	
	/* Technically the following two methods follow the ActiveRecord pattern, but it
	 * is more convenient to implement this simple switch polymorphically than by 
	 * writing a data mapper explicitly for this purpose. 
	 */
	
//	/**
//	 * An abstract polymorphic method to update a {@link MinyanGoer} without needing to 
//	 * explicitly visit the object.
//	 * @param context is used for the {@link MinyanMateContentProvider}
//	 * @param values contains the new values to be used
//	 * @return an int with the number of affected columns
//	 */
//	public abstract int updateStatus(Context context, ContentValues values);
//	
//	/**
//	 * An abstract polymorphic method to delete a {@link MinyanGoer} without needing to 
//	 * explicitly visit the object.
//	 * @param context is used for the {@link MinyanMateContentProvider}
//	 * @return an int with the number of affected columns
//	 */
//	public abstract int delete(Context context);
	
	/**
	 * This method visits the cursor to decide whether to instantiate an instance
	 * of the {@link UninvitedMinyanGoer} class or {@link InvitedMinyanGoer} class.
	 * @param cursor contains columns necessary to instantiate either class
	 * @return a {@link org.minyanmate.minyanmate.models.MinyanGoer} object
	 */
	public static MinyanGoer cursorToMinyanGoer(Cursor cursor) {
		
		int goerId = cursor.getInt(GoerMatrix.GOER_ID);
		boolean isInvited = cursor.getInt(GoerMatrix.IS_INVITED) == 1 ? true : false;
		int eventId = cursor.getInt(GoerMatrix.EVENT_ID);
		int contactId = cursor.getInt(GoerMatrix.CONTACT_ID);
		InviteStatus status = InviteStatus.fromInteger(cursor.getInt(GoerMatrix.INVITE_STATUS));
		String photoUri = cursor.getString(GoerMatrix.THUMBNAIL_PHOTO_URI);
		String name = cursor.getString(GoerMatrix.DISPLAY_NAME);
		String phoneNum = cursor.getString(GoerMatrix.PHONE_NUMBER);
		String lookUpKey = cursor.getString(GoerMatrix.LOOKUP_KEY);
		
		if (isInvited) 
			return new InvitedMinyanGoer(goerId, contactId, name, eventId, status, 
					isInvited, photoUri, phoneNum, lookUpKey);
		else
			return new UninvitedMinyanGoer(goerId, name, eventId, status, isInvited);
	}
	
	public static List<MinyanGoer> cursorToMinyanGoerList(MatrixCursor cursor) {
		
		List<MinyanGoer> goerList = new ArrayList<MinyanGoer>();
		while(cursor.moveToNext()) {
			goerList.add(cursorToMinyanGoer(cursor));
		}
		
		return goerList;
	}
}

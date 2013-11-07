package org.minyanmate.minyanmate.models;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.GoerMatrix;

import android.database.MatrixCursor;

public abstract class MinyanGoer {
	
	private int id;
	private String name;
	private int eventId;
	private boolean isInvited;
	
	MinyanGoer(int id, String name, int eventId, boolean isInvited) {
		this.id = id;
		this.name = name;
		this.eventId = eventId;
		this.isInvited = isInvited;
	}
	
	public int getId() {
		return id;
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
	
	/**
	 * This method visits the cursor to decide whether to instantiate an instance
	 * of the {@link UninvitedMinyanGoer} class or {@link InvitedMinyanGoer} class.
	 * @param cursor contains columns necessary to instantiate either class
	 * @return 
	 */
	public static MinyanGoer cursorToMinyanGoer(MatrixCursor cursor) {
		
		// TODO resolve issue where not storing eventId and these constructors are messed up too
		
		boolean isInvited = cursor.getInt(GoerMatrix.IS_INVITED) == 1 ? true : false;
		int contactId = cursor.getInt(GoerMatrix.ID);
		String photoUri = cursor.getString(GoerMatrix.THUMBNAIL_PHOTO_URI);
		String name = cursor.getString(GoerMatrix.NAME);
		String phoneNum = cursor.getString(GoerMatrix.NUM);
		String lookUpKey = cursor.getString(GoerMatrix.KEY);
		
		if (isInvited) 
			return new InvitedMinyanGoer(contactId, name, 0, isInvited, photoUri, phoneNum, lookUpKey);
		
		return null;
	}
}

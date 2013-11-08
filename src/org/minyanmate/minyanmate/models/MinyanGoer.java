package org.minyanmate.minyanmate.models;

import java.util.ArrayList;
import java.util.List;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.GoerMatrix;

import android.database.MatrixCursor;

public abstract class MinyanGoer {
	
	int goerId;
	String name;
	int eventId;
	boolean isInvited;
	
	MinyanGoer(int id, String name, int eventId, boolean isInvited) {
		this.goerId = id;
		this.name = name;
		this.eventId = eventId;
		this.isInvited = isInvited;
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
	 * @return 
	 */
	public static MinyanGoer cursorToMinyanGoer(MatrixCursor cursor) {
		
		int goerId = cursor.getInt(GoerMatrix.GOER_ID);
		boolean isInvited = cursor.getInt(GoerMatrix.IS_INVITED) == 1 ? true : false;
		int eventId = cursor.getInt(GoerMatrix.EVENT_ID);
		int contactId = cursor.getInt(GoerMatrix.CONTACT_ID);
		String photoUri = cursor.getString(GoerMatrix.THUMBNAIL_PHOTO_URI);
		String name = cursor.getString(GoerMatrix.NAME);
		String phoneNum = cursor.getString(GoerMatrix.NUM);
		String lookUpKey = cursor.getString(GoerMatrix.KEY);
		
		if (isInvited) 
			return new InvitedMinyanGoer(goerId, contactId, name, eventId, isInvited, photoUri, phoneNum, lookUpKey);
		else
			return new UninvitedMinyanGoer(goerId, name, eventId, isInvited);
	}
	
	public static List<MinyanGoer> cursorToMinyanGoerList(MatrixCursor cursor) {
		
		List<MinyanGoer> goerList = new ArrayList<MinyanGoer>();
		while(cursor.moveToNext()) {
			goerList.add(cursorToMinyanGoer(cursor));
		}
		
		return goerList;
	}
}

package org.minyanmate.minyanmate.models;

import java.io.InputStream;
import java.util.List;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

/**
 * This will be useful for the scheduling assistant services, but until
 * those are developed everything here is subject to change. Should probably
 * be given a similar interface as {@link MinyanSchedule}.
 * @author Jeff
 *
 */
public class Contact {

	private int id;
	private String name;
	private String phoneNum;
	private InputStream photoStream;
	
	public Contact(String name, String num, InputStream photo) {
		this.name = name;
		this.phoneNum = num;
		this.photoStream = photo;
	}
	
	public Contact(int id, String name, String num, InputStream photo) {
		this(name, num, photo);
		this.id = id;
	}
	
	
	public int getContactId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPhoneNumber() {
		return phoneNum;
	}
	
	public InputStream getContactPhotoStream() {
		return photoStream;
	}
	
	public static Contact contactFromCursor(Cursor cursor) {
		// TODO may not be necessary
		return null;
	}
	
	public static List<Contact> cursorToContactList(Cursor cursor) {
		// TODO strategy:
		/* Given a 
		 * 
		 * 
		 */
		return null;
	}
	
}

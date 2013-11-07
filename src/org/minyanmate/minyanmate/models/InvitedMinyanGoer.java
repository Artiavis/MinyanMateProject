package org.minyanmate.minyanmate.models;

public class InvitedMinyanGoer extends MinyanGoer {

	private String photoUri;
	private String phoneNumber;
	private String lookUpKey;
	
	InvitedMinyanGoer(int id, String name, int eventId, boolean isInvited, 
			String photoUri, String phoneNum, String key) {
		super(id, name, eventId, isInvited);
		this.photoUri = photoUri;
		this.phoneNumber = phoneNum;
		this.lookUpKey = key;
	}
	
	InvitedMinyanGoer(int id, String name, int eventId, boolean isInvited) {
		super(id, name, eventId, isInvited);
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

}

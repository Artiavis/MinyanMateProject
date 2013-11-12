package org.minyanmate.minyanmate.models;

import org.minyanmate.minyanmate.database.MinyanGoersTable;

	/**
	 * Refer to {@link MinyanGoersTable#COLUMN_INVITE_STATUS}
	 */
	public enum InviteStatus {
		ATTENDING("Attending"), 
		NOT_ATTENDING("Not Attending"), 
		AWAITING_RESPONSE("Awaiting Response");
		
		private final String text;
		
		private InviteStatus(final String text) {
			this.text = text;
		}
		
		/**
		 * Refer to {@link MinyanGoersTable#COLUMN_INVITE_STATUS}
		 * @param stat
		 * @return
		 */
		static InviteStatus fromInteger(int stat) {
			switch (stat) {
			case 1:
				return AWAITING_RESPONSE;
			case 2:
				return ATTENDING;
			case 3:
				return NOT_ATTENDING;
			}
			
			return null;
		}
	}
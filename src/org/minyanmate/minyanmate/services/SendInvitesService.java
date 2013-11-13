package org.minyanmate.minyanmate.services;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SendInvitesService extends WakefulIntentService {

	public SendInvitesService() {
		super("SendInvitesService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		Bundle b = intent.getExtras();
		
		int id = b.getInt("requestCode");
		
		MinyanSchedule sched = MinyanSchedule.schedFromCursor(getContentResolver().query(
				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/"+ id),
				null, null, null, null
				));
		
		Cursor contactsToBeInvited = getContentResolver().query(
				MinyanMateContentProvider.CONTENT_URI_CONTACTS, null, 
				MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?", 
				new String[] { String.valueOf(id) }, null);
		
		SmsManager smsm = SmsManager.getDefault();
		
		while (contactsToBeInvited.moveToNext()) {
			String number = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.NUM);
			// TODO fire intent to log the invited recipient in the Goers table if the message was received
			smsm.sendTextMessage(number, null, sched.getInviteMessage(), null, null);
			SystemClock.sleep(1000);
		}
		
	}

}

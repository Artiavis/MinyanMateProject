package org.minyanmate.minyanmate.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class OnMinyanAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		Bundle b = intent.getExtras();
		int id = b.getInt("requestCode");

		Intent i = new Intent(context, SendInvitesService.class);
		i.putExtra("requestCode", id);
		
		WakefulIntentService.sendWakefulWork(context, SendInvitesService.class);
		
	}

}

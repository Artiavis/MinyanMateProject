package org.minyanmate.minyanmate.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class OnMinyanAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("OnMinyanAlarmReciever", "Inside OnMinyanAlarmReceiver");
		
		Bundle b = intent.getExtras();
		int id = b.getInt("scheduleId");
		
		Log.d("OnMinyanAlarmReceiver", "putting requestCode:" + id);

		Intent i = new Intent(context, SendInvitesService.class);
		i.putExtra("scheduleId", id);
		
		
		Log.d("OnMinyanAlarmReceiver", "Sending wakeful work");
		
		WakefulIntentService.sendWakefulWork(context, i);
		
	}

}

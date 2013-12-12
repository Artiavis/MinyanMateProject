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
		
		Log.i("OnMinyanAlarmReciever", "Inside OnMinyanAlarmReceiver");
		
		Bundle b = intent.getExtras();
		int scheduleId = b.getInt(SendInvitesService.SCHEDULE_ID);
        int requestCode = b.getInt(SendInvitesService.REQUEST_CODE);
		
		Log.d("OnMinyanAlarmReceiver", "putting scheduleId:" + scheduleId);

		Intent i = new Intent(context, SendInvitesService.class);
		i.putExtra(SendInvitesService.SCHEDULE_ID, scheduleId);
        i.putExtra(SendInvitesService.REQUEST_CODE, requestCode);
		
		Log.i("OnMinyanAlarmReceiver", "Sending wakeful work");
		
		WakefulIntentService.sendWakefulWork(context, i);
		
	}

}

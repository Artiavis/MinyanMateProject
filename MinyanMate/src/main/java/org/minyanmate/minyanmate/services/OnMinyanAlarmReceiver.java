package org.minyanmate.minyanmate.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.services.sms_services.SendSmsService;

public class OnMinyanAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.i("OnMinyanAlarmReciever", "Inside OnMinyanAlarmReceiver");
		
		Bundle b = intent.getExtras();
		int scheduleId = b.getInt(SendSmsService.SCHEDULE_ID);
        int requestCode = b.getInt(SendSmsService.REQUEST_CODE);
		
		Log.d("OnMinyanAlarmReceiver", "putting scheduleId:" + scheduleId);

		Intent i = new Intent(context, SendSmsService.class);
		i.putExtra(SendSmsService.SCHEDULE_ID, scheduleId);
        i.putExtra(SendSmsService.REQUEST_CODE, requestCode);
		
		Log.i("OnMinyanAlarmReceiver", "Sending wakeful work");
		
		WakefulIntentService.sendWakefulWork(context, i);
		
	}

}

package org.minyanmate.minyanmate.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable;

/**
 * A class which is automatically queries the {@link MinyanMateContentProvider}
 * for all Minyans marked as being active in {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#COLUMN_IS_ACTIVE}.
 */
public class OnBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("OnBootReceiver", "Inside OnBootReceiver");
		
		Cursor minyanSchedulesCursor = context.getContentResolver().query(
				MinyanMateContentProvider.CONTENT_URI_SCHEDULES,
				null, 
				MinyanPrayerSchedulesTable.COLUMN_IS_ACTIVE + "=?", new String[] { "1" },
				MinyanPrayerSchedulesTable.COLUMN_PRAYER_SCHEDULE_ID + " ASC");

		
		MinyanRegistrar.registerMinyanEvents(context, minyanSchedulesCursor);
        minyanSchedulesCursor.close();

	}

}

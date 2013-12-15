package org.minyanmate.minyanmate.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;

/**
 * A class which is automatically queries the {@link MinyanMateContentProvider}
 * for all Minyans marked as being active in {@link MinyanSchedulesTable#COLUMN_IS_ACTIVE}.
 */
public class OnBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("OnBootReceiver", "Inside OnBootReceiver");
		
		Cursor minyanSchedulesCursor = context.getContentResolver().query(
				MinyanMateContentProvider.CONTENT_URI_SCHEDULES,
				null, 
				MinyanSchedulesTable.COLUMN_IS_ACTIVE + "=?", new String[] { "1" }, 
				MinyanSchedulesTable.COLUMN_SCHEDULE_ID + " ASC");

		
		MinyanRegistrar.registerMinyanEvents(context, minyanSchedulesCursor);
        minyanSchedulesCursor.close();

	}

}

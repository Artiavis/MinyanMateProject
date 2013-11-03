package org.minyanmate.minyanmate.services;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A class which is automatically queries the {@link MinyanMateContentProvider}
 * for all Minyans marked as being active in {@link MinyanSchedulesTable#COLUMN_IS_ACTIVE}.
 */
public class RegisterMinyansOnBootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// TODO implement
	}

}

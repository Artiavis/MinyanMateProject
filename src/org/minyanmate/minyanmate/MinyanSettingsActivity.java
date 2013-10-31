package org.minyanmate.minyanmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanTimesTable;
import org.minyanmate.minyanmate.models.Prayer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class MinyanSettingsActivity extends FragmentActivity
	implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private int prayerId;
	private Prayer prayer;
	TextView timeTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_minyan_settings);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				prayerId = extras.getInt("prayerId");
			}
		}
		
		getSupportLoaderManager().initLoader(0, null, this);
		
		timeTextView = (TextView) findViewById(R.id.minyan_setting_timeTextView);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button by going back
		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
			Intent intent = new Intent(getBaseContext(), MinyanMateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MinyanSettingsActivity.this.finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public static String formatTimeTextView(Context context, int hour, int minute) {
		SimpleDateFormat format;
		if (DateFormat.is24HourFormat(context)) {
			format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		} else {
			format = new SimpleDateFormat("h:mm aa", Locale.getDefault());
		}
		Calendar time = Calendar.getInstance(Locale.getDefault());
		time.set(Calendar.HOUR_OF_DAY, hour);
		time.set(Calendar.MINUTE, minute);
		return format.format(time.getTime());
	}
	
	public void pickNewTime(View view) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.initialize(prayer.getId(), timeTextView, prayer.getHour(), prayer.getMinute());
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	
	public static class TimePickerFragment extends DialogFragment 
		implements TimePickerDialog.OnTimeSetListener {
		
		private int id;
		private int minute;
		private int hour;
		private TextView txt;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			ContentValues values = new ContentValues();
			values.put(MinyanTimesTable.COLUMN_PRAYER_HOUR, hourOfDay);
			values.put(MinyanTimesTable.COLUMN_PRAYER_MIN, minute);
			view.getContext().getContentResolver().update(
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
					values,
//					MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
					null, null
					);
			
		}
		
		public void initialize(int id, TextView txt, int hour, int minute) {
			this.minute = minute;
			this.hour = hour;
			this.id = id;
			this.txt = txt;
		}
		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = new CursorLoader(this, 
				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + this.prayerId), 
				null, null, null, 
				null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		cursor.moveToFirst();
		prayer = Prayer.prayerFromCursor(cursor);
		timeTextView.setText(formatTimeTextView(this, prayer.getHour(), prayer.getMinute()));
		
		setTitle(prayer.getDay() + " - " + prayer.getPrayerName());
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
}

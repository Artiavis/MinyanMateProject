package org.minyanmate.minyanmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.minyanmate.minyanmate.adapters.RemovableContactListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.ContactMatrix;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Provides access to general details about a weekly minyan event, and permits
 * changing the time and invite list. Is called from {@link MinyanScheduleListFragment}
 * and can subsequently call the Android contact picker or {@link PickMultipleContactsActivity}.
 *
 */
public class MinyanScheduleSettingsActivity extends FragmentActivity
	implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final int TIME_LOADER = 1;
	public static final int CONTACT_LOADER = 2;
	public final static int PICK_CONTACT = 10;
	public final static int PICK_MULTIPLE_CONTACTS = 20;
	
	private int prayerId;
	private MinyanSchedule prayer;
	TextView timeTextView;
	ListView contactList;
	
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
		
		getSupportLoaderManager().initLoader(TIME_LOADER, null, this); // for times
		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this); // for contacts
		// TODO for window
		
		timeTextView = (TextView) findViewById(R.id.minyan_setting_timeTextView);
		contactList = (ListView) findViewById(R.id.minyan_setting_contactsList);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button by going back
		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
			Intent intent = new Intent(getBaseContext(), MinyanMateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MinyanScheduleSettingsActivity.this.finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Formats hour in 0-23 format and minute into the current locale, specifically
	 * choosing between HH:mm and h:mm aa time formats.
	 * @param context
	 * @param hour
	 * @param minute
	 * @return formattedTimeString
	 */
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
	
	/**
	 * The onClick event handler to select a single Contact using Android's contact picker
	 * @param view
	 */
	public void pickNewContact(View view) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}
	
	/**
	 * The onClick event handler to select multiple contacts using the {@link PickMultipleContactsActivity} 
	 * activity.
	 * @param view
	 */
	public void pickNewGroup(View view) {
		// TODO stuff
	}
	
	/**
	 * The onClick event handler to edit the minyan start time. Creates a {@link TimePickerFragment}.
	 * @param view
	 */
	public void pickNewTime(View view) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.initialize(prayer.getId(), prayer.getHour(), prayer.getMinute(), prayer);
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	public void pickNewScheduleWindow(View view) {
		// TODO this
		
	}
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		
		switch(reqCode) {
		case (PICK_CONTACT) :
			if (resultCode == Activity.RESULT_OK) {
				
				Uri result = data.getData();
				saveContactData(result);
				
			}
			break;
			
		case (PICK_MULTIPLE_CONTACTS):
			if (resultCode == Activity.RESULT_OK) {
				
				// do stuff
			}
		}
	}
	
	/**
	 * Given a Uri for a contact, confirm the associated contact has a phone number,
	 * and if so, save the result.
	 * @param data
	 */
	private void saveContactData(Uri data) {
		String lookUpKey = null;
		
		lookUpKey = data.getPathSegments().get(2);
		Cursor temp = getContentResolver().query(Phone.CONTENT_URI, null, Phone.LOOKUP_KEY + "=?", 
				new String[] { lookUpKey }, null);
		
		if (temp.moveToFirst()) {
			if ( temp.getString(temp.getColumnIndex(Phone.NUMBER)) != null)
			{
				// Save result
				ContentValues values = new ContentValues();
				values.put(MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID, prayerId);
				values.put(MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY, lookUpKey);
				
				if ( getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_CONTACTS, values) != null)
					Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
			} else // this code doesn't appear to be reachable, either a phone uri exists or it doesn't
				Toast.makeText(this, "Contact has no phone number! Not added!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * A DialogFragment with a Time Picker, used to update the time of a schedule
	 * when it calls back to onTimeSet
	 */
	public static class TimePickerFragment extends DialogFragment 
		implements TimePickerDialog.OnTimeSetListener {
		
		private int id;
		private int minute;
		private int hour;
		private MinyanSchedule prayer;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		
		/**
		 * The callback from {@link TimePickerDialog.OnTimeSetListener}, pushes
		 * changes back to the {@link MinyanMateContentProvider}.
		 */
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			ContentValues values = new ContentValues();
			values.put(MinyanSchedulesTable.COLUMN_PRAYER_HOUR, hourOfDay);
			values.put(MinyanSchedulesTable.COLUMN_PRAYER_MIN, minute);
			
			long windowLength = prayer.getSchedulingWindowLength();
			Cursor nextSchedule = view.getContext().getContentResolver().query(
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + (id + 1)), 
					null, null, null, null);
			
			if (nextSchedule.moveToFirst() ) {
				MinyanSchedule nextSched = MinyanSchedule.prayerFromCursor(nextSchedule);
				long endTime = prayer.getHour() * 3600 + prayer.getMinute() * 60;
				long startTime = (nextSched.getPrayerNum() == 1 ? 1 : 0)*24*3600 
						+ nextSched.getHour()*3600 + nextSched.getMinute() * 60;
				
				if (startTime - windowLength > endTime) {
					view.getContext().getContentResolver().update(
							Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
							values,
//							MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
							null, null
							);
				} else {
					Toast.makeText(view.getContext(), "Failed to save new time! There "
							+ "may be another minyan scheduled to soon!", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			
			
			
			view.getContext().getContentResolver().update(
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
					values,
//					MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
					null, null
					);
			
		}
		
		public void initialize(int id, int hour, int minute, MinyanSchedule sched) {
			this.minute = minute;
			this.hour = hour;
			this.id = id;
			this.prayer = sched;
		}
		
	}

	public static class WindowSchedulePickerFragent extends DialogFragment 
	implements TimePickerDialog.OnTimeSetListener {
	
	private int id;
	private int minute;
	private int hour;
	private MinyanSchedule prayer;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new TimePickerDialog(getActivity(), this, hour, minute,
				false);
	}
	
	/**
	 * The callback from {@link WindowSchedulePickerFragent.OnTimeSetListener}, pushes
	 * changes back to the {@link MinyanMateContentProvider}.
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		ContentValues values = new ContentValues();
		long windowLength = 3600 * hourOfDay + 60*minute;
		values.put(MinyanSchedulesTable.COLUMN_SCHEDULE_WINDOW, 3600*hourOfDay + 60*minute);
		
		if (id != 1) {
			Cursor previousSchedule = view.getContext().getContentResolver().query(
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + (id - 1)), 
					null, null, null, null);
			
			previousSchedule.moveToFirst();
			MinyanSchedule sched = MinyanSchedule.prayerFromCursor(previousSchedule);
			long absEndTime = (sched.getHour() * 3600 + sched.getMinute() * 60 + 30*60);
			long startTime = (sched.getPrayerNum() == 3 ? 1 : 0)*24*60*60 + prayer.getHour() * 3600 + sched.getMinute() * 60;
			// check whether there's a sufficient amount of time between this minyan and the previous one
			if (  startTime - windowLength > absEndTime) {
				view.getContext().getContentResolver().update(
						Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
						values,
//						MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
						null, null
						);
			} else {
				Toast.makeText(view.getContext(), "Failed to save new window! There may "
						+ "be a minyan scheduled to recently!", Toast.LENGTH_SHORT).show();
				return;
			}
		} 
		
		view.getContext().getContentResolver().update(
				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
				values,
//				MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
				null, null
				);
	}
	
	public void initialize(int id, int hour, int minute, MinyanSchedule sched) {
		this.minute = minute;
		this.hour = hour;
		this.id = id;
		this.prayer = sched;
	}
	
}
	

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		// TODO for window
		
		switch (id) {
		case TIME_LOADER:
			return new CursorLoader(this, 
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + this.prayerId), 
					null, null, null, 
					null);
			
		case CONTACT_LOADER:
			return new CursorLoader(this,
					MinyanMateContentProvider.CONTENT_URI_CONTACTS,
					new String[] { MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID, 
						MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY },
					MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?", 
					new String[] { String.valueOf(this.prayerId) }, null);
		}

		return null;
	}

	/**
	 * When the CursorLoader finishes loading the requested data from the
	 * {@link MinyanMateContentProvider}, populate
	 * the page elements.
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		// TODO for window
		
		switch( loader.getId()) {
		
		case TIME_LOADER:
			cursor.moveToFirst();
			prayer = MinyanSchedule.prayerFromCursor(cursor);
			timeTextView.setText(formatTimeTextView(this, prayer.getHour(), prayer.getMinute()));
			setTitle(prayer.getDay() + " - " + prayer.getPrayerName());
		
			break;
			
		case CONTACT_LOADER:
			
			CursorAdapter adapter = new RemovableContactListAdapter(this, cursor, prayerId, false);
			contactList.setAdapter(adapter);
			
			break;
		
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		switch (loader.getId()){
		case TIME_LOADER:
			
			break;
		
		case CONTACT_LOADER:
			contactList.setAdapter(null);
			break;
			
			default:
				
		}
	}
}

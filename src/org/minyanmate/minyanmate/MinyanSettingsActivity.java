package org.minyanmate.minyanmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.minyanmate.minyanmate.adapters.RemovableContactListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.ContactMatrix;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanTimesTable;
import org.minyanmate.minyanmate.models.Prayer;

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
 * changing the time and invite list. Is called from {@link MinyanListFragment}
 * and can subsequently call the Android contact picker or {@link PickMultipleContactsActivity}.
 *
 */
public class MinyanSettingsActivity extends FragmentActivity
	implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final int TIME_LOADER = 1;
	public static final int CONTACT_LOADER = 2;
	public final static int PICK_CONTACT = 10;
	public final static int PICK_MULTIPLE_CONTACTS = 20;
	
	private int prayerId;
	private Prayer prayer;
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
			MinyanSettingsActivity.this.finish();
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
		// TODO stuff
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
		newFragment.initialize(prayer.getId(), timeTextView, prayer.getHour(), prayer.getMinute());
		newFragment.show(getSupportFragmentManager(), "timePicker");
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
				values.put(MinyanContactsTable.COLUMN_MINYAN_TIME_ID, prayerId);
				values.put(MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY, lookUpKey);
				
				getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_CONTACTS, values);
				
				// Signal success
				Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
			} else // this code doesn't appear to be reachable, either a phone uri exists or it doesn't
				Toast.makeText(this, "Contact has no phone number! Not added!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * A DialogFragment with a Time Picker, used to update the time of a prayer
	 * when it calls back to onTimeSet
	 */
	public static class TimePickerFragment extends DialogFragment 
		implements TimePickerDialog.OnTimeSetListener {
		
		private int id;
		private int minute;
		private int hour;
		
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
		}
		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		switch (id) {
		case TIME_LOADER:
			return new CursorLoader(this, 
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + this.prayerId), 
					null, null, null, 
					null);
			
		case CONTACT_LOADER:
			return new CursorLoader(this,
					MinyanMateContentProvider.CONTENT_URI_CONTACTS,
					new String[] { MinyanContactsTable.COLUMN_MINYAN_TIME_ID, 
						MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY },
					MinyanContactsTable.COLUMN_MINYAN_TIME_ID + "=?", 
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
		
		switch( loader.getId()) {
		
		case TIME_LOADER:
			cursor.moveToFirst();
			prayer = Prayer.prayerFromCursor(cursor);
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

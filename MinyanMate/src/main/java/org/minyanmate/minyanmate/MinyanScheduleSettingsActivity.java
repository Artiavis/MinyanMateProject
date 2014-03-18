package org.minyanmate.minyanmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.minyanmate.minyanmate.adapters.RemovableContactListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable;
import org.minyanmate.minyanmate.dialogs.ScheduleTimePickerFragment;
import org.minyanmate.minyanmate.dialogs.ScheduleWindowPickerFragent;
import org.minyanmate.minyanmate.dialogs.TermsOfService;
import org.minyanmate.minyanmate.models.FullMinyanSchedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Provides access to general details about a weekly minyan event, and permits
 * changing the time and invite list. Is called from {@link MinyanScheduleListFragment}
 * and can subsequently call the Android contact picker
 *
 */
public class MinyanScheduleSettingsActivity extends FragmentActivity
	implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final int TIME_LOADER = 1;
	public static final int CONTACT_LOADER = 2;
	public final static int PICK_CONTACT = 10;
	
	private int scheduleId;
	private FullMinyanSchedule schedule;
	
	private TextView timeTextView;
	private TextView windowTextView;
	private ListView contactList;

    public MinyanScheduleSettingsActivity() {
        super();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_minyan_settings);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				scheduleId = extras.getInt("prayerId");
			}
		} else {
            scheduleId = savedInstanceState.getInt("prayerId");
            // TODO make parcelable and retreive
            //schedule = (MinyanSchedule) savedInstanceState.get("schedule");
        }
		
		getSupportLoaderManager().initLoader(TIME_LOADER, null, this); // for times
		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this); // for contacts
		
		timeTextView = (TextView) findViewById(R.id.minyan_setting_timeTextView);
		windowTextView = (TextView) findViewById(R.id.minyan_setting_scheduleWindowTextView);
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
			
        case R.id.action_terms_of_service:
        	TermsOfService.showTerms(this);
            return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("scheduleId", scheduleId);
        // TODO create parcelable MinyanSchedule and put here
//        savedInstanceState.putParcelable("schedule", schedule);
    }
	
	
	/**
	 * Formats hour in 0-23 format and minute into the current locale, specifically
	 * choosing between HH:mm and h:mm aa time formats, for displaying event time.
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
		return dateFormatToString(format, hour, minute);
	}
	
	/**
	 * Formats hour in 0-23 and minute in H:mm format. Does not need to respect locality
	 * because it is only used to display a countdown. 
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static String formatWindowTextView(int hour, int minute) {
		SimpleDateFormat format = new SimpleDateFormat("H:mm");
		return dateFormatToString(format, hour, minute);
		
	}
	
	private static String dateFormatToString(SimpleDateFormat format, int hour, int minute) {
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
        intent.setType(Phone.CONTENT_TYPE);
		startActivityForResult(intent, PICK_CONTACT);
	}
	
	/**
	 * The onClick event handler to edit a custom message
	 * activity.
	 * @param view
	 */
	public void editCustomMessage(View view) {
        final View v = view;
        final int scheduleId = this.scheduleId;
        final String previousInviteMessage = schedule.getInviteMessage();
        final String prayerName = schedule.getPrayerName();
        final int prayerHour = schedule.getHour();
        final int prayerMinute = schedule.getMinute();

        View customMessageView = LayoutInflater.from(this).inflate(R.layout.fragment_custom_message, null);

        final EditText input = (EditText) customMessageView.findViewById(R.id.customMessageEditText);
        final TextView charactersLeftTextView = (TextView) customMessageView.findViewById(R.id.customMessageTextLength);
        final TextView msgPreviewTextView = (TextView) customMessageView.findViewById(R.id.customMessagePreview);

        // Initizlize the saved result
        input.setText(previousInviteMessage, TextView.BufferType.EDITABLE);
        // Initialize the preview of the full mesesage
        msgPreviewTextView.setText("Preview: " + FullMinyanSchedule.formatInviteMessage(v.getContext(), input.getText().toString(),
                prayerName, prayerHour, prayerMinute));
        // Initialize the character counter
        charactersLeftTextView.setText(
                FullMinyanSchedule.SCHEDULE_MESSAGE_SIZE_LIMIT - input.getText().length() + " characters left");

        // When editing the message, update the preview and the character limit count
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                charactersLeftTextView.setText(
                        FullMinyanSchedule.SCHEDULE_MESSAGE_SIZE_LIMIT - input.getText().length() + " characters left");
                msgPreviewTextView.setText("Preview: " + FullMinyanSchedule.formatInviteMessage(v.getContext(),
                        input.getText().toString(), prayerName, prayerHour, prayerMinute));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        final AlertDialog alert = new AlertDialog.Builder(this)
                .setView(customMessageView)
                .setTitle("Modify Custom Message")
                .setMessage("You can add a brief foreword of at most " +
                        FullMinyanSchedule.SCHEDULE_MESSAGE_SIZE_LIMIT + " characters below.")

                .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        String msg = input.getText().toString();

                        if (msg.length() <= FullMinyanSchedule.SCHEDULE_MESSAGE_SIZE_LIMIT) {
                            ContentValues values = new ContentValues();
                            values.put(MinyanPrayerSchedulesTable.COLUMN_SCHEDULE_MESSAGE, msg);

                            getContentResolver().update(MinyanMateContentProvider.CONTENT_URI_SCHEDULES, values,
                                    MinyanPrayerSchedulesTable.COLUMN_PRAYER_SCHEDULE_ID + "=?", new String[]{Integer.toString(scheduleId)});
                        } else {
                            Toast.makeText(v.getContext(), "Could not save your message! It was too long!", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                })

                .create();

        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        alert.show();
	}
	
	/**
	 * The onClick event handler to edit the minyan start time. Creates a {@link ScheduleTimePickerFragment} which
	 * then saves its results using this context.
	 * @param view
	 */
	public void pickNewTime(View view) {
		ScheduleTimePickerFragment newFragment = new ScheduleTimePickerFragment();
		newFragment.initialize(schedule.getId(), schedule.getHour(), schedule.getMinute(), schedule);
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	public void pickNewScheduleWindow(View view) {
		ScheduleWindowPickerFragent newFragment = new ScheduleWindowPickerFragent();
		newFragment.initialize(schedule.getId(), schedule.getSchedulingWindowHours(), 
				schedule.getSchedulingWindowMinutes(), schedule);
		newFragment.show(getSupportFragmentManager(), "windowPicker");
	}
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		
		switch(reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri result = data.getData();
                    saveContactData(result);

                }
                break;

            default:
                break;
        }
	}
	
	/**
	 * Given a Uri for a contact, confirm the associated contact has a phone number,
	 * and if so, save the result.
	 * @param data
	 */
	private void saveContactData(Uri data) {
		String phoneNumberId = data.getLastPathSegment();
        Cursor temp = getContentResolver().query(Phone.CONTENT_URI, null, Phone._ID + "=?",
				new String[] { phoneNumberId }, null);
		
		if (temp.moveToFirst()) {
			if ( temp.getString(temp.getColumnIndex(Phone.NUMBER)) != null)
			{
				// Save result
				ContentValues values = new ContentValues();
				values.put(MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID, scheduleId);
				values.put(MinyanContactsTable.COLUMN_PHONE_NUMBER_ID, phoneNumberId);
				
				if ( getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_CONTACTS, values) != null)
					Toast.makeText(this, "Added successfully!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
			} else // this code doesn't appear to be reachable, either a phone uri exists or it doesn't
				Toast.makeText(this, "Contact has no phone number! Not added!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failed to save contact!", Toast.LENGTH_SHORT).show();
		}

        temp.close();
	}
	

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		switch (id) {
		case TIME_LOADER:
			return new CursorLoader(this, 
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + this.scheduleId),
					null, null, null, 
					null);
			
		case CONTACT_LOADER:
			return new CursorLoader(this,
					MinyanMateContentProvider.CONTENT_URI_CONTACTS,
					new String[] { MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID, 
						MinyanContactsTable.COLUMN_PHONE_NUMBER_ID},
					MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + " = " + this.scheduleId,
					null, null);
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
			schedule = FullMinyanSchedule.scheduleFromCursor(cursor);
            String formattedPrayerTime = formatTimeTextView(this, schedule.getHour(), schedule.getMinute());
            timeTextView.setText(schedule.getPrayerName() + " begins at " + formattedPrayerTime);
			setTitle(schedule.getDay() + " - " + schedule.getPrayerName());
		
			int windowHours = schedule.getSchedulingWindowHours();
			int windowMinutes = schedule.getSchedulingWindowMinutes();

            String formattedTimeInAdvance = formatWindowTextView(windowHours, windowMinutes);
            windowTextView.setText("Send invites " + formattedTimeInAdvance + " hours beforehand");
			
			break;
			
		case CONTACT_LOADER:
			CursorAdapter adapter = new RemovableContactListAdapter(this, cursor,
                    new RemovableContactListAdapter.DistinctContactCallbacks());
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

package org.minyanmate.minyanmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.minyanmate.minyanmate.adapters.ParticipantsExpandableListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.models.MinyanGoer;
import org.minyanmate.minyanmate.services.SendInvitesService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

class ActiveMinyanFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor>{
	
	private static final int EVENT = 1;
	private static final int PARTICIPANTS = 2;

    private static final int MENUITEM_MARK_ATTENDING = 1;
    private static final int MENUITEM_MARK_AWAITING = 2;
    private static final int MENUITEM_MARK_NOT_ATTENDING = 3;

	private ParticipantsExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	
	private int mEventId = 0;
    private int mScheduleId = 0;
	
	
	public ActiveMinyanFragment() {	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_active_minyan, container, false);

        // LoaderManager not guaranteed to run before onActivityResult, therefore restore state
        mEventId = savedInstanceState.getInt("mEventId");
        mScheduleId = savedInstanceState.getInt("mScheduleId");

		getLoaderManager().initLoader(EVENT, null, this);
		getLoaderManager().initLoader(PARTICIPANTS, null, this);
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.activeMinyanParticipantsList);
		
		HashMap<String, List<MinyanGoer>> map = new HashMap<String, List<MinyanGoer>>();
		List<String> list = new ArrayList<String>();
		listAdapter = new ParticipantsExpandableListAdapter(getActivity(), list, map);
		
		expListView.setAdapter(listAdapter);
        expListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
                ExpandableListView.ExpandableListContextMenuInfo info=
                        (ExpandableListView.ExpandableListContextMenuInfo)menuInfo;

                if (ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
                {
                    int groupNum = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                    Log.d("Group Number in onCreateContextMenu", String.valueOf(groupNum));

                    menu.setHeaderTitle("Move participant");

                    switch (groupNum) {
                        case 0: // group 0 => marked as attending
                            menu.add(0, MENUITEM_MARK_AWAITING, 1, "Mark as awaiting response");
                            menu.add(0, MENUITEM_MARK_NOT_ATTENDING, 2, "Mark as not attending");
                            break;
                        case 1: // group 1 => marked as awaiting response
                            menu.add(0, MENUITEM_MARK_ATTENDING, 1, "Mark as attending");
                            menu.add(0, MENUITEM_MARK_NOT_ATTENDING, 2, "Mark as not attending");
                            break;

                        case 2: // group 2 => marked as not attending
                            menu.add(0, MENUITEM_MARK_ATTENDING, 1, "Mark as attending");
                            menu.add(0, MENUITEM_MARK_AWAITING, 2, "Mark as awaiting response");
                            break;

                        default:
                            break;
                    }
                }
            }
        });

		return rootView;
	}

    @Override
    public void onSaveInstanceState(Bundle screenState) {
        super.onSaveInstanceState(screenState);
        screenState.putInt("mEventId", mEventId);
        screenState.putInt("mScheduleId", mScheduleId);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();

        int groupNum = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childNum = ExpandableListView.getPackedPositionChild(info.packedPosition);
        Log.d("Group Number in onContextItemSelected", String.valueOf(groupNum));
        Log.d("Child Number in onContextItemSelected", String.valueOf(childNum));

        MinyanGoer goer = (MinyanGoer) listAdapter.getChild(groupNum, childNum);
        Log.d("Goer", String.valueOf(goer));

        ContentValues values = new ContentValues();
        values.put(MinyanGoersTable.COLUMN_GOER_ID, goer.getMinyanGoerId());

        switch (item.getItemId()) {
            case MENUITEM_MARK_ATTENDING:
                values.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.ATTENDING));
                break;

            case MENUITEM_MARK_NOT_ATTENDING:
                values.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.NOT_ATTENDING));
                break;

            case MENUITEM_MARK_AWAITING:
                values.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE));
                break;

            default:
                break;
        }

        getActivity().getContentResolver().update(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS,
                values, MinyanGoersTable.COLUMN_GOER_ID + "=?", new String[]{String.valueOf(goer.getMinyanGoerId())});

        return super.onContextItemSelected(item);
    }
	
	public void addUninvited() {
		// add an uninvited minyangoer to the table

        final int eventId = mEventId;
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
            .setView(input)
            .setTitle("Count a Non-Contact")
            .setMessage("Write down the name of someone you didn't invite who you want to count.")

            .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    String name = input.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(MinyanGoersTable.COLUMN_DISPLAY_NAME, name);
                    values.put(MinyanGoersTable.COLUMN_IS_INVITED, 0);
                    values.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, eventId);
                    values.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.ATTENDING));

                    getActivity().getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, values);
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

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        switch(reqCode) {
            case (MinyanScheduleSettingsActivity.PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK && mEventId > 0 && mScheduleId > 0) {

                    Uri result = data.getData();
                    String phoneNumberId = result.getLastPathSegment();

                    Intent i = new Intent(getActivity(), SendInvitesService.class);
                    i.putExtra(SendInvitesService.REQUEST_CODE, SendInvitesService.SEND_INVITE_TO_CONTACT);
                    i.putExtra(SendInvitesService.EVENT_ID, mEventId);
                    i.putExtra(SendInvitesService.SCHEDULE_ID, mScheduleId);
                    i.putExtra(SendInvitesService.PHONE_NUMBER_ID, Long.parseLong(phoneNumberId));
                    WakefulIntentService.sendWakefulWork(getActivity(), i);
                }
                else {
                    Toast.makeText(getActivity(), "Failed to invite contact!", Toast.LENGTH_SHORT).show();
                    Log.d("onActivityResult failed", "Event Id: " + mEventId);
                    Log.d("onActivityResult failed", "Schedule Id: " + mScheduleId);
                }
                break;

            default:
                break;
        }
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		CursorLoader cursorLoader = null;
		String query;
		switch (id) {

		case EVENT:
			
			cursorLoader = new CursorLoader(getActivity(),
					MinyanMateContentProvider.CONTENT_URI_EVENTS, null, 
					MinyanEventsTable.QUERY_LATEST_EVENT, null, null);
			break;
			
		case PARTICIPANTS:
			
			cursorLoader = new CursorLoader(getActivity(),
					MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS,
					null, MinyanGoersTable.QUERY_LATEST_GOERS, null, null);
			
			break;		
		}
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		switch (loader.getId()) {
		case EVENT:		
			// TODO fix? on first app load, this table is empty so the cursor result will be empty
			if (cursor.moveToFirst()) {
				final long scheduleTime = cursor.getLong(cursor.getColumnIndex(MinyanEventsTable.COLUMN_MINYAN_SCHEDULE_TIME));
                final long startTime = cursor.getLong(cursor.getColumnIndex(MinyanEventsTable.COLUMN_MINYAN_START_TIME));
                final long endTime = cursor.getLong(cursor.getColumnIndex(MinyanEventsTable.COLUMN_MINYAN_END_TIME));

				Calendar cal = new GregorianCalendar();
				cal.setTimeInMillis(startTime);
				int minute = cal.get(Calendar.MINUTE);
				int hour = cal.get(Calendar.HOUR_OF_DAY);

                String day = cursor.getString(cursor.getColumnIndex(MinyanEventsTable.COLUMN_DAY_NAME));
                String prayerName = cursor.getString(cursor.getColumnIndex(MinyanEventsTable.COLUMN_PRAYER_NAME));
                mEventId = cursor.getInt(cursor.getColumnIndex(MinyanEventsTable.COLUMN_EVENT_ID));
                mScheduleId = cursor.getInt(cursor.getColumnIndex(MinyanEventsTable.COLUMN_MINYAN_SCHEDULE_ID));

				String formattedTime = MinyanScheduleSettingsActivity.formatTimeTextView(getActivity(), hour, minute);
				TextView timeTextView = (TextView) getActivity().findViewById(R.id.activeMinyanTime);
				timeTextView.setText(day + prayerName + " begins at " + formattedTime);

                // Initialize button state and callbacks
                Button addToCountBtn = (Button) getActivity().findViewById(R.id.addUninvitedPersonButton);
                Button invToMinyanBtn = (Button) getActivity().findViewById(R.id.inviteContactToMinyanButton);

                // If minyan info is old, don't even bother enabling or setting callbacks
                long currentTime = System.currentTimeMillis();
                if (scheduleTime < currentTime && currentTime < endTime) {

                    addToCountBtn.setEnabled(true);
                    addToCountBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long currentTime = System.currentTimeMillis();
                            if (scheduleTime < currentTime && currentTime < endTime) {
                                addUninvited();
                            }
                            else
                                Toast.makeText(view.getContext(), "Minyan is no longer active!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    invToMinyanBtn.setEnabled(true);
                    invToMinyanBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            long currentTime = System.currentTimeMillis();
                            if (scheduleTime < currentTime && currentTime < endTime) {
                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                                startActivityForResult(intent, MinyanScheduleSettingsActivity.PICK_CONTACT);
                            }
                            else
                                Toast.makeText(view.getContext(), "Minyan is no longer active!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
			}

			break;
			
		case PARTICIPANTS:
			
			List<String> categories = new ArrayList<String>();
			categories.add(InviteStatus.ATTENDING.toString());
			categories.add(InviteStatus.AWAITING_RESPONSE.toString());			
			categories.add(InviteStatus.NOT_ATTENDING.toString());

			
			HashMap<String, List<MinyanGoer>> goers = new HashMap<String, List<MinyanGoer>>();
			for (String cat : categories) 
				goers.put(cat, new ArrayList<MinyanGoer>());
			
			while (cursor.moveToNext()) {
				MinyanGoer goer = MinyanGoer.cursorToMinyanGoer(cursor);
				goers.get(goer.getInviteStatus().toString()).add(goer);
			}


			listAdapter.setListDataHeader(categories);
			listAdapter.setDataChildren(goers);
			listAdapter.notifyDataSetChanged();
			
			break;
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		loader = null; 
	}
}

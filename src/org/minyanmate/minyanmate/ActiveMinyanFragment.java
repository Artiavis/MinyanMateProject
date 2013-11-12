package org.minyanmate.minyanmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.adapters.ParticipantsExpandableListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.models.MinyanGoer;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ActiveMinyanFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor>{
	
	private static final int EVENT = 1;
	private static final int PARTICIPANTS = 2;
	
	ParticipantsExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	
	
	public ActiveMinyanFragment() {	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_active_minyan, container, false);

		getLoaderManager().initLoader(EVENT, null, this);
		getLoaderManager().initLoader(PARTICIPANTS, null, this);
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.activeMinyanParticipantsList);
		HashMap<String, List<MinyanGoer>> map = new HashMap<String, List<MinyanGoer>>();
		List<String> list = new ArrayList<String>();
		listAdapter = new ParticipantsExpandableListAdapter(getActivity(), list, map);
		
		expListView.setAdapter(listAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		CursorLoader cursorLoader = null;
		String query;
		switch (id) {
		
		case EVENT:
			query = MinyanGoersTable.COLUMN_ID + "= (SELECT MAX(" 
							+ MinyanGoersTable.COLUMN_ID + ") FROM " 
							+ MinyanGoersTable.TABLE_MINYAN_INVITEES + ")";
			
			cursorLoader = new CursorLoader(getActivity(),
					MinyanMateContentProvider.CONTENT_URI_EVENTS, null, 
					query, null, null);
			break;
			
		case PARTICIPANTS:
			query = MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + "= (SELECT MAX(" 
					+ MinyanGoersTable.COLUMN_MINYAN_EVENT_ID + ") FROM " 
					+ MinyanGoersTable.TABLE_MINYAN_INVITEES + ")";
			
			cursorLoader = new CursorLoader(getActivity(),
					MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS,
					null, query, null, null);
			
			break;		
		}
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		
		switch (loader.getId()) {
		case EVENT:		
			cursor.moveToFirst();
			long startTime = cursor.getLong(cursor.getColumnIndex(MinyanEventsTable.COLUMN_MINYAN_START_TIME));
			int hour = (int) (startTime % (24*60*60));
			int minute = (int) (startTime % (60*60));
			
			String formattedTime = MinyanScheduleSettingsActivity.formatTimeTextView(getActivity(), hour, minute);
			
			TextView timeTextView = (TextView) getActivity().findViewById(R.id.activeMinyanTime);
			timeTextView.setText(formattedTime);
			
			break;
			
		case PARTICIPANTS:
			
			List<String> categories = new ArrayList<String>();
			categories.add(InviteStatus.ATTENDING.toString());
			categories.add(InviteStatus.AWAITING_RESPONSE.toString());			
			categories.add(InviteStatus.NOT_ATTENDING.toString());

			
			HashMap<String, List<MinyanGoer>> goers = new HashMap<String, List<MinyanGoer>>();
			for (String cat : categories) goers.put(cat, new ArrayList<MinyanGoer>());
			
			while (cursor.moveToNext()) {
				MinyanGoer goer = MinyanGoer.cursorToMinyanGoer((MatrixCursor) cursor);
				goers.get(goer.getInviteStatus().toString()).add(goer);
			}
			
			listAdapter.setListDataHeader(categories);
			listAdapter.setDataChildren(goers);
			
			break;
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		// TODO Auto-generated method stub
		
	}
}

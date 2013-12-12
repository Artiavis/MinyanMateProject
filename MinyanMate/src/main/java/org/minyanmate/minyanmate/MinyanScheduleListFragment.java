package org.minyanmate.minyanmate;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.minyanmate.minyanmate.adapters.ScheduleExpandableListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MinyanScheduleListFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor> {
	
	public MinyanScheduleListFragment() {
	}
	
	private ScheduleExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	
	private List<String> listDataHeader;
	private HashMap<String, List<MinyanSchedule>> listDataChild;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_minyan_list, container, false);
		

		Context context = getActivity();
		getLoaderManager().initLoader(0, null, this);
		
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.minyanList);
		
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<MinyanSchedule>>();
		listAdapter = new ScheduleExpandableListAdapter(context, listDataHeader, listDataChild);
		
		expListView.setAdapter(listAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), 
				MinyanMateContentProvider.CONTENT_URI_SCHEDULES, null, null, null,
				MinyanSchedulesTable.COLUMN_DAY_NUM + ", " 
				+ MinyanSchedulesTable.COLUMN_PRAYER_HOUR + ", "
				+ MinyanSchedulesTable.COLUMN_PRAYER_MIN);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		
		List<MinyanSchedule> prayerTimes = MinyanSchedule.cursorToScheduleList(data);
		listDataChild = new HashMap<String, List<MinyanSchedule>>();
		listDataHeader = new ArrayList<String>();
		for(MinyanSchedule prayer : prayerTimes) {
			// if new Day, add it to headers and create a new map entry
			if( !listDataHeader.contains(prayer.getDay())) {
				listDataHeader.add(prayer.getDay());
				List<MinyanSchedule> temp = new ArrayList<MinyanSchedule>();
				temp.add(prayer);
				listDataChild.put(prayer.getDay(), temp);
				
			} else // else it already exists and just add to entry
			{
				listDataChild.get(prayer.getDay()).add(prayer);
			}
		}
		listAdapter.setDataChildren(listDataChild);
		listAdapter.setListDataHeader(listDataHeader);
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		loader = null;
		
	}

}

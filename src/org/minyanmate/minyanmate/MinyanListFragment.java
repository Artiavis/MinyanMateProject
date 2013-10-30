package org.minyanmate.minyanmate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.adapters.ExpandableListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanTimesTable;
import org.minyanmate.minyanmate.models.Prayer;

import android.content.ContentResolver;
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

public class MinyanListFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor> {
	
	public MinyanListFragment() {
	}
	
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	
	List<String> listDataHeader;
	HashMap<String, List<Prayer>> listDataChild;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_minyan_list, container, false);
		
		// TODO why is this null?
		//Context context = container.getContext();
		Context context = getActivity();
		getLoaderManager().initLoader(0, null, this);
		
		
//		adapter = new ExpandablePrayerTimesListAdapter(null, getActivity());
		expListView = (ExpandableListView) rootView.findViewById(R.id.minyanList);
		
		// TODO define and obtain the cursor for these data
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<Prayer>>();
		listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
		
		expListView.setAdapter(listAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), 
				MinyanMateContentProvider.CONTENT_URI_TIMES, null, null, null, 
				MinyanTimesTable.COLUMN_DAY_NUM + ", " 
				+ MinyanTimesTable.COLUMN_PRAYER_HOUR + ", "
				+ MinyanTimesTable.COLUMN_PRAYER_MIN);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		
		List<Prayer> prayerTimes = Prayer.cursorToPrayerList(data);
		listDataChild = new HashMap<String, List<Prayer>>();
		listDataHeader = new ArrayList<String>();
		for(Prayer prayer : prayerTimes) {
			// if new Day, add it to headers and create a new map entry
			if( !listDataHeader.contains(prayer.getDay())) {
				listDataHeader.add(prayer.getDay());
				List<Prayer> temp = new ArrayList<Prayer>();
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

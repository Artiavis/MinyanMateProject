package org.minyanmate.minyanmate;

import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.adapters.ExpandableListAdapter;
import org.minyanmate.minyanmate.adapters.ExpandablePrayerTimesListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;

public class MinyanListFragment extends Fragment implements
	LoaderManager.LoaderCallbacks<Cursor> {
	
	private ExpandablePrayerTimesListAdapter adapter;
	
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(
				R.layout.fragment_minyan_list, container, false);
		Context context = container.getContext();
		
		adapter = new ExpandablePrayerTimesListAdapter(null, getActivity());
		expListView = (ExpandableListView) rootView.findViewById(R.id.minyanList);
		
		// TODO define and obtain the cursor for these data
		listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
		
		expListView.setAdapter(listAdapter);
		
		return rootView;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = new CursorLoader(getActivity(), 
				MinyanMateContentProvider.CONTENT_URI_TIMES, null, null, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.setGroupCursor(data);
		
//		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// no more data, delete everything
		adapter.setGroupCursor(null);
	}

}

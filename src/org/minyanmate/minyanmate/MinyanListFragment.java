package org.minyanmate.minyanmate;

import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.views.ExpandableListAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class MinyanListFragment extends Fragment {
	
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
		
		expListView = (ExpandableListView) rootView.findViewById(R.id.minyanList);
		
		// TODO define and obtain the cursor for these data
		listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);
		
		expListView.setAdapter(listAdapter);
		
		return rootView;
	}

}

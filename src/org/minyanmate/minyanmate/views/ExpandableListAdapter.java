package org.minyanmate.minyanmate.views;

import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

// http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/
public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> _listDataHeader;
	private HashMap<String, List<String>> _listDataChild;
	
	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String childText = (String) getChild(groupPosition, childPosition);
		
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl.inflate(R.layout.minyan_time, null);
		}
		
		// TODO bind events to textbox and to checkbox
		TextView txtListChild = (TextView) convertView.findViewById(R.id.minyanTimeTextview);
		
		
		txtListChild.setText(childText);
		return convertView;
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl.inflate(R.layout.minyan_day, null);
		}
		
		TextView minyanDay = (TextView) convertView.findViewById(R.id.minyanDayHeader);
		minyanDay.setText(headerTitle);
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}

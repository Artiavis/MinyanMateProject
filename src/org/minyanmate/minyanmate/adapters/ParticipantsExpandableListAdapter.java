package org.minyanmate.minyanmate.adapters;

import java.util.HashMap;
import java.util.List;

import org.minyanmate.minyanmate.models.MinyanGoer;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class ParticipantsExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> _listDataHeader;
	private HashMap<String, List<MinyanGoer>> _listDataChild;
	
	public ParticipantsExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<MinyanGoer>> listChildData) {
		this.context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}
	
	public void setListDataHeader(List<String> newHeaders) {
		this._listDataHeader = newHeaders;
	}
	
	public void setDataChildren(HashMap<String, List<MinyanGoer>> newkidsontheblock) {
		this._listDataChild = newkidsontheblock;
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}

package org.minyanmate.minyanmate.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.minyanmate.minyanmate.MinyanScheduleSettingsActivity;
import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.HashMap;
import java.util.List;

// http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

/**
 * An adapter for generating headers and children for an 
 * {@link android.widget.ExpandableListView} using a list of {@link MinyanSchedule}s.
 * @author Jeff
 *
 */
public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> _listDataHeader;
	private HashMap<String, List<MinyanSchedule>> _listDataChild;
	
	public ScheduleExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<MinyanSchedule>> listChildData) {
		
		this.context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		
	}
	
	public void setListDataHeader(List<String> newHeaders) {
		this._listDataHeader = newHeaders;
	}
	
	public void setDataChildren(HashMap<String, List<MinyanSchedule>> newkidsontheblock) {
		this._listDataChild = newkidsontheblock;
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
		final MinyanSchedule childPrayer = (MinyanSchedule) getChild(groupPosition, childPosition);
		
		if (convertView == null) {
			LayoutInflater infl = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infl.inflate(R.layout.minyan_time, null);
		}
		
		TextView txtListChild = (TextView) convertView.findViewById(R.id.minyanTimeTextview);
		CheckBox chkBox = (CheckBox) convertView.findViewById(R.id.minyanTimeCheckbox);

		
		txtListChild.setText(childPrayer.getPrayerName() + " - " +
				MinyanScheduleSettingsActivity.formatTimeTextView(context, 
				childPrayer.getHour(), childPrayer.getMinute()));
		chkBox.setChecked(childPrayer.isActive());
		
		
		txtListChild.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, MinyanScheduleSettingsActivity.class);
				intent.putExtra("prayerId", childPrayer.getId());
				context.startActivity(intent);
				
			}
		});
		
		chkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Try and cancel/initiate alarm
				
				ContentValues values = new ContentValues();
				values.put(MinyanSchedulesTable.COLUMN_IS_ACTIVE, ((CheckBox) v).isChecked() ? 1 : 0);
				
				int updateCount = context.getContentResolver().update(
						Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + childPrayer.getId()),
						values,
//						MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
						null, null
						);
			}
			});
		
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

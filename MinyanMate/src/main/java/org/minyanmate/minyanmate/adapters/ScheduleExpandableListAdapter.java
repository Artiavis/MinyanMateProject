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
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.HashMap;
import java.util.List;

// http://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/

/**
 * An adapter for generating headers and children for an 
 * {@link android.widget.ExpandableListView} using a list of {@link org.minyanmate.minyanmate.models.FullMinyanSchedule}s.
 * @author Jeff
 *
 */
public class ScheduleExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private List<String> _listDataHeader;
	private HashMap<String, List<MinyanSchedule>> _listDataChild;
    private ScheduleAdapterCallbacks callbacks;
	
	public ScheduleExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<MinyanSchedule>> listChildData,
            ScheduleAdapterCallbacks scheduleAdapterCallbacks) {
		
		this.context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.callbacks = scheduleAdapterCallbacks;
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

        // If this is a top-level view, give it the ability to have children
		if (callbacks instanceof ScheduleListAdapterCallbacks) {
            txtListChild.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    callbacks.onClickTextView(context, childPrayer.getId());
                }
            });
        }

		
		chkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                callbacks.onCheck(context, ((CheckBox) v).isChecked(), childPrayer.getId());
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

    /**
     * An interface to pass to the adapter so that it can switch on its behavior in different
     * contexts without repeating boilerplate or having redundant files. If the adapter is being
     * used for {@link org.minyanmate.minyanmate.ContactManagerActivity}, then it should only
     * be used for checkboxes, but not for textviews. If the adapter is being used for
     * {@link org.minyanmate.minyanmate.MinyanScheduleListFragment}, it should have both.
     */
    public static interface ScheduleAdapterCallbacks {
        public void onClickTextView(Context c, int prayerId);
        public void onCheck(Context c, boolean isChecked, int prayerId);
    }

    public static class ScheduleListAdapterCallbacks implements ScheduleAdapterCallbacks {

        @Override
        public void onClickTextView(Context c, int prayerId) {
            Intent intent = new Intent(c, MinyanScheduleSettingsActivity.class);
            intent.putExtra("prayerId", prayerId);
            c.startActivity(intent);
        }

        @Override
        public void onCheck(Context c, boolean isChecked, int prayerId) {
            // Try and cancel/initiate alarm

            ContentValues values = new ContentValues();
            values.put(MinyanPrayerSchedulesTable.COLUMN_IS_ACTIVE, isChecked ? 1 : 0);

            int updateCount = c.getContentResolver().update(
                    Uri.parse(MinyanMateContentProvider.CONTENT_URI_SCHEDULES + "/" + prayerId),
                    values,
//						MinyanTimesTable.COLUMN_EVENT_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
                    null, null
            );
        }
    }

    public static class ContactScheduleAdapterCallbacks implements ScheduleAdapterCallbacks {

        private int phoneNumberId;

        public ContactScheduleAdapterCallbacks(int phoneNumberId) {
            this.phoneNumberId = phoneNumberId;
        }

        @Override
        public void onClickTextView(Context c, int prayerId) {
            return;
        }

        @Override
        public void onCheck(Context c, boolean isChecked, int prayerId) {

            /*
            * If isChecked, insert a new record. Otherwise, delete a record.
            */

            if (isChecked) {


                ContentValues values = new ContentValues();
                values.put(MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID, prayerId);
                values.put(MinyanContactsTable.COLUMN_PHONE_NUMBER_ID, phoneNumberId);
                c.getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_CONTACTS, values);

            } else {

                c.getContentResolver().delete(MinyanMateContentProvider.CONTENT_URI_CONTACTS,
                        MinyanContactsTable.COLUMN_PHONE_NUMBER_ID + "=?"
                                + " and " + MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?",
                        new String[] { Integer.toString(phoneNumberId), Integer.toString(prayerId) });

            }


        }
    }

}

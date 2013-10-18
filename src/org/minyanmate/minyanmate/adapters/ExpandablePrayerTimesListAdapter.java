package org.minyanmate.minyanmate.adapters;

import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanTimesTable;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

public class ExpandablePrayerTimesListAdapter extends CursorTreeAdapter {

	private LayoutInflater inflater;
	private Context context;
	
	public ExpandablePrayerTimesListAdapter(Cursor cursor, Context context) {
		super(cursor, context);
		
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	protected void bindChildView(View view, Context context, Cursor cursor,
			boolean isLastChild) {
		
		TextView time = (TextView) view.findViewById(R.id.minyanTimeTextview);
		CheckBox checkBox = (CheckBox) view.findViewById(R.id.minyanTimeCheckbox);
		
		time.setText(cursor.getString(cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_HOUR
				+ ":" + cursor.getColumnIndex(MinyanTimesTable.COLUMN_PRAYER_MIN))));
		
		checkBox.setChecked(cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_IS_ACTIVE)) 
				== 1 ? true : false);
		
		// TODO register some events
	}

	@Override
	protected void bindGroupView(View view, Context context, Cursor cursor,
			boolean isExpanded) {
		
		TextView day = (TextView) view.findViewById(R.id.minyanDayHeader);
		
		day.setText(cursor.getInt(cursor.getColumnIndex(MinyanTimesTable.COLUMN_DAY)));
	}

	
	@Override
	protected Cursor getChildrenCursor(Cursor groupCursor) {
		
		Cursor itemCursor = getGroup(groupCursor.getPosition()); // what does this do?
		
		// TODO provide the right projection and selection
		CursorLoader cursorLoader = new CursorLoader(this.context, 
				MinyanMateContentProvider.CONTENT_URI_CONTACTS, 
				null, null, null, null); 
		
		Cursor childCursor = null;
		
		try {
			childCursor = cursorLoader.loadInBackground();
			childCursor.moveToFirst();
		} catch (Exception e) {
			
		}
		
		return childCursor;
	}

	@Override
	protected View newChildView(Context context, Cursor cursor,
			boolean isLastChild, ViewGroup parent) {
		final View view = inflater.inflate(R.layout.minyan_time, parent, false);
		return view;
	}

	@Override
	protected View newGroupView(Context context, Cursor cursor,
			boolean isExpanded, ViewGroup parent) {
		final View view = inflater.inflate(R.layout.minyan_day, parent, false);
		return view;
	}

}

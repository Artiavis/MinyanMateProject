package org.minyanmate.minyanmate.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanSchedulesTable;

/**
 * A DialogFragment with a Time Picker, used to update the time of a schedule
 * when it calls back to onTimeSet
 */
public class ScheduleTimePickerFragment extends AbstractSchedulePickerDialog {
	

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}
	
	/**
	 * The callback from {@link TimePickerDialog.OnTimeSetListener}, pushes
	 * changes back to the {@link MinyanMateContentProvider}.
	 */
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		/* 
		 * Need to check both ahead and behind. Can't move this schedule too far forward
		 * or it would interfere with the next event, and can't move this schedule too far
		 * backwards or it would interfere with the previous event.
		 */
		
		ContentValues values = new ContentValues();
		values.put(MinyanSchedulesTable.COLUMN_PRAYER_HOUR, hourOfDay);
		values.put(MinyanSchedulesTable.COLUMN_PRAYER_MIN, minute);
		
		saveSelection(view.getContext(), hourOfDay, minute, 
				schedule.getSchedulingWindowLength(), values);
		
		
//		long windowLength = this.schedule.getSchedulingWindowLength();
//		Cursor nextSchedule = view.getContext().getContentResolver().query(
//				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + (id + 1)), 
//				null, null, null, null);
//		
//		if (nextSchedule.moveToFirst() ) {
//			MinyanSchedule nextSched = MinyanSchedule.schedFromCursor(nextSchedule);
//			long endTime = hourOfDay * 3600 + minute * 60;
//			long startTime = (nextSched.getPrayerNum() == 1 ? 1 : 0)*24*3600 
//					+ nextSched.getHour()*3600 + nextSched.getMinute() * 60;
//			
//			if (startTime - windowLength > endTime) {
//				view.getContext().getContentResolver().update(
//						Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
//						values,
////						MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
//						null, null
//						);
//			} else {
//				Toast.makeText(view.getContext(), "Failed to save new time! There "
//						+ "may be another minyan scheduled to soon!", Toast.LENGTH_SHORT).show();
//				return;
//			}
//		}
//		
//		
//		view.getContext().getContentResolver().update(
//				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
//				values,
////				MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
//				null, null
//				);
//		
	}
	

	
}
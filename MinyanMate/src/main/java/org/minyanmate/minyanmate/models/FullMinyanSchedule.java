package org.minyanmate.minyanmate.models;

import android.content.Context;
import android.database.Cursor;

import org.minyanmate.minyanmate.MinyanScheduleSettingsActivity;
import org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A model class containing the attributes of objects from the 
 * {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} table. Contains
 * {@link FullMinyanSchedule#scheduleFromCursor(Cursor)} and {@link FullMinyanSchedule#cursorToScheduleList(Cursor)}
 * static helper methods to pull Prayer objects from the cursors.
 */
public class FullMinyanSchedule extends MinyanSchedule {

    public static final String RESPONSE_API_INSTRUCTIONS = ". Can you come? Please " +
            "respond \"accept\" or \"decline\". Thank you.";
    public static final int SCHEDULE_MESSAGE_SIZE_LIMIT = 59;

    private int dayNum;
    private int prayerNum;
    private long scheduleWindowLength;
	private String inviteMessage;
	
	public FullMinyanSchedule(int id, String day, int dayNum,
                              int prayerNum, String prayerName, long winLen, int hour, int minute,
                              boolean isActive, String msg) {
		this(day, dayNum, prayerNum, prayerName, winLen, hour, minute, isActive, msg);
		this._id = id;
	}
	
	public FullMinyanSchedule(String day, int dayNum, int prayerNum, String prayerName, long winLen, int hour,
                              int minute, boolean isActive, String msg) {
        super(prayerName, hour, minute, isActive, day);
        this.dayNum = dayNum;
		this.prayerNum = prayerNum;
        this.scheduleWindowLength = winLen;
        this.inviteMessage = msg;
	}

    /**
     * A helper method for formatting the final content of the SMS to be sent to an
     * {@link org.minyanmate.minyanmate.models.InvitedMinyanGoer}.
     * @param context a context passed to
     *      {@link org.minyanmate.minyanmate.MinyanScheduleSettingsActivity#formatTimeTextView(android.content.Context, int, int)}
     *                to facilitate time localization
     * @param userCustomMsg a user's personalized custom message to be prepended to the default message
     * @param prayerName the name of the prayer, see
     *      {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#COLUMN_PRAYER_NAME}
     * @param prayerHour the hour of the prayer, see
     *                   {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#COLUMN_PRAYER_HOUR}
     * @param prayerMinute the minute of the prayer, see
     *                  {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#COLUMN_PRAYER_MIN}
     * @return a String with the formatted final message
     */
    public static String formatInviteMessage(Context context, String userCustomMsg, String prayerName, int prayerHour, int prayerMinute) {
        String truncatedUserCustomMsg = userCustomMsg.substring(0, Math.min(userCustomMsg.length(),
                SCHEDULE_MESSAGE_SIZE_LIMIT));

        return truncatedUserCustomMsg + (truncatedUserCustomMsg.trim().length() > 0 ? " " : "")
                + prayerName +  " will be at " +
                MinyanScheduleSettingsActivity.formatTimeTextView(context, prayerHour, prayerMinute)
                + RESPONSE_API_INSTRUCTIONS;
    }

    public int getPrayerNum() {
		return prayerNum;
	}

    public int getDayNum() {
		return dayNum;
	}

    public long getSchedulingWindowLength() {
		return scheduleWindowLength;
	}
	
	public int getSchedulingWindowHours() {
		return (int) TimeUnit.MILLISECONDS.toHours(scheduleWindowLength);
	}
	
	public int getSchedulingWindowMinutes() {
		return (int) TimeUnit.MILLISECONDS.toMinutes(scheduleWindowLength) % 60;
	}

    public String getInviteMessage() {
		return inviteMessage;
	}
	
	/**
	 * A helper function to extract the columns from a cursor over the 
	 * {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} and returns a new {@link FullMinyanSchedule} object
	 * based upon it. 
	 * <p>
	 * @param cursor a cursor over the {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} table
	 * which is already moved to a specific row. 
	 * @return prayer, a new {@link FullMinyanSchedule} object
	 */
	public static FullMinyanSchedule scheduleFromCursor(Cursor cursor) {
		
		int id = cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_PRAYER_SCHEDULE_ID));
		int dayNum = cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_DAY_NUM));
		long winLen = cursor.getLong(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_SCHEDULE_WINDOW));
		int hour = cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_PRAYER_HOUR));
		int min = cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_PRAYER_MIN));
		int prayerNum = cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_PRAYER_NUM));
		boolean isActive = (cursor.getInt(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_IS_ACTIVE)) == 1);
		String dayName = cursor.getString(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_DAY_NAME));
		String prayerName = cursor.getString(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_PRAYER_NAME));
		String invMsg = cursor.getString(cursor.getColumnIndex(MinyanPrayerSchedulesTable.COLUMN_SCHEDULE_MESSAGE));
		
		return new FullMinyanSchedule(id, dayName, dayNum, prayerNum, prayerName, winLen,hour, min, isActive, invMsg);
	}


	/**
	 * Given a cursor over a set of multiple results from the 
	 * {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} table, iterate over the cursor using
	 * {@link FullMinyanSchedule#scheduleFromCursor(Cursor)} to generate a list of {@link FullMinyanSchedule} objects.
	 * <p>
	 * @param cursor a new cursor over the {@link org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable#TABLE_MINYAN_PRAYER_SCHEDULES} table.
	 * @return prayerList, a list of new {@link FullMinyanSchedule} objects
	 */
	public static List<MinyanSchedule> cursorToScheduleList(Cursor cursor) {
		
		List<MinyanSchedule> prayerList = new ArrayList<MinyanSchedule>();
		while(cursor.moveToNext()) {
			prayerList.add(scheduleFromCursor(cursor));
		}
		
		return prayerList;
	}
}

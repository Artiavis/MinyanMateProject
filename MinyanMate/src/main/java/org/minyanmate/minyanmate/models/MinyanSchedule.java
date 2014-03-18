package org.minyanmate.minyanmate.models;

/**
 * Created by Jeff on 3/17/14.
 */
public class MinyanSchedule {

    protected int _id;
    protected String day;
    protected String prayerName;
    protected int hour;
    protected int minute;
    protected boolean isActive;

    public MinyanSchedule(String prayerName, int hour, int minute, boolean isActive, String day) {
        this.prayerName = prayerName;
        this.hour = hour;
        this.minute = minute;
        this.isActive = isActive;
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getId() {
        return _id;
    }

    public String getPrayerName() {
        return prayerName;
    }


}

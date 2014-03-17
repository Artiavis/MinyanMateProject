package org.minyanmate.minyanmate;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ExpandableListView;

import org.minyanmate.minyanmate.adapters.ScheduleExpandableListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanPrayerSchedulesTable;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class acts in two purposes. Its first purpose is to allow direct management of an
 * individual contact that has already been added to the database. In this case, the contact
 * will be queried from the database and pre-populated into the expandable list. Its second purpose
 * is to act as the final step in a "wizard" to add a new contact to multiple schedules at once.
 *
 * To this end, the class should check its Intent bundles for information about whether it's
 * acting in the first capacity or the second.
 *
 * Created by Jeff on 3/17/14.
 */
public class ContactManagerActivity extends FragmentActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    public ContactManagerActivity() {
        super();
    }

    private ExpandableListView expListView;
    private List<String> listDataHeader;

    private HashMap<String, List<MinyanSchedule>> listDataChild;
    private ScheduleExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_minyan_list);


        getSupportLoaderManager().initLoader(0, null, this);

        expListView = (ExpandableListView) findViewById(R.id.minyanList);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<MinyanSchedule>>();
        listAdapter = new ScheduleExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);
    }

    // TODO replace this with the joined SCHEDULE/CONTACT matrix
    // TODO enable the adapter to have dynamic callbacks

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        CursorLoader cursorLoader = new CursorLoader(this,
                MinyanMateContentProvider.CONTENT_URI_SCHEDULES, null, null, null,
                MinyanPrayerSchedulesTable.COLUMN_DAY_NUM + ", "
                        + MinyanPrayerSchedulesTable.COLUMN_PRAYER_HOUR + ", "
                        + MinyanPrayerSchedulesTable.COLUMN_PRAYER_MIN);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        List<MinyanSchedule> prayerTimes = MinyanSchedule.cursorToScheduleList(data);
        listDataChild = new HashMap<String, List<MinyanSchedule>>();
        listDataHeader = new ArrayList<String>();
        for(MinyanSchedule prayer : prayerTimes) {
            // if new Day, add it to headers and create a new map entry
            if( !listDataHeader.contains(prayer.getDay())) {
                listDataHeader.add(prayer.getDay());
                List<MinyanSchedule> temp = new ArrayList<MinyanSchedule>();
                temp.add(prayer);
                listDataChild.put(prayer.getDay(), temp);

            } else // else it already exists and just add to entry
            {
                listDataChild.get(prayer.getDay()).add(prayer);
            }
        }
        listAdapter.setDataChildren(listDataChild);
        listAdapter.setListDataHeader(listDataHeader);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;

    }
}

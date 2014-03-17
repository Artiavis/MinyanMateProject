package org.minyanmate.minyanmate;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.minyanmate.minyanmate.adapters.RemovableContactListAdapter;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;

/**
 * Created by Jeff on 3/14/14.
 */

// TODO this should inherit the fragment used inside MinyanScheduleSettingsActivity

public class ContactManagerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Codes for LoaderManager
    public static final int CONTACT_LOADER = 2;

    private ListView contactList;

    public ContactManagerFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_manage_contacts, container, false);

        // Use this to declare the use of a custom menu, thus using onCreateOptionsMenu
        setHasOptionsMenu(true);

        // Initialize the LoaderManager
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);

        contactList = (ListView) rootView.findViewById(R.id.minyan_setting_contactsList);

        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {


        // First inflate the new menu options, and only afterwards inflate the base menu
        // Diagram below, reference http://stackoverflow.com/a/4954800/1993865
        /*
            Menu:    ()
            inflate new: ( new )
            inflate base: ( new, old)
        */
        menuInflater.inflate(R.menu.contacts_more_options, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.moreOptsMenu_AddNewContact:
                // TODO implement this
                return true;

            default:
                break;
        }

        return false;
    }

    // TODO implement these callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {

        switch (id) {
            case CONTACT_LOADER:

                return new CursorLoader(getActivity(),
                    MinyanMateContentProvider.CONTENT_URI_CONTACTS,
                        new String[] { MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID,
                                MinyanContactsTable.COLUMN_PHONE_NUMBER_ID},
                    null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        switch(cursorLoader.getId()) {

            case CONTACT_LOADER:
                // FIXME do this

                CursorAdapter adapter = new RemovableContactListAdapter(getActivity(), cursor);
                contactList.setAdapter(adapter);
                break;

            default:

                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}

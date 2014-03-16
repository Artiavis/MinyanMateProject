package org.minyanmate.minyanmate;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by Jeff on 3/14/14.
 */

// TODO this should inherit the fragment used inside MinyanScheduleSeettingsActivity

public class ContactManagerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use this to declare the use of a custom menu, thus using onCreateOptionsMenu
        setHasOptionsMenu(true);
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

    // TODO implement these callbacks
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}

package org.minyanmate.minyanmate;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PickMultipleContactsActivity extends ListActivity 
	implements OnClickListener {
	
	
	   // List variables
    public String[] Contacts = {};
    public int[] to = {};
    public ListView myListView;

    Button save_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_multiple_contacts);

        // Initializing the buttons according to their ID
        save_button = (Button)findViewById(R.id.pick_multiple_contacts_doneButton);

        // Defines listeners for the buttons
        save_button.setOnClickListener(this);

        Cursor mCursor = getContacts();
        startManagingCursor(mCursor);

        ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, mCursor,
                                                      Contacts = new String[] {ContactsContract.Contacts.DISPLAY_NAME },
                                                      to = new int[] { android.R.id.text1 });
        setListAdapter(adapter);
        myListView = getListView();
        myListView.setItemsCanFocus(false);
        myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    } 

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                                        ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    public void onClick(View src) {
        Intent i;
        switch (src.getId())
        {
        case R.id.pick_multiple_contacts_doneButton:

            int checked_Names_Counter = 0;

            // Goes over the list of contacts and checks which were checked
            for (int j = 0; j < myListView.getCount(); j++)
            {
                if (myListView.isItemChecked(j) == true)
                {   
                    Cursor cur = getContacts();
                    ContentResolver contect_resolver = getContentResolver();
                    cur.moveToFirst();

                    /**
                    * Here I tried to compare the IDs but each list has different IDs so it didn't really help me...
                    // Converts the current checked name ID into a String
                    String Checked_ID = String.valueOf(myListView.getCheckedItemIds()[checked_Names_Counter]);

                    // Checks if the current checked ID matches the cursors ID, if not move the cursor to the next name
                    while (Checked_ID != cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID)))
                    {
                        cur.moveToNext();
                    }
                    */

                    /**
                    * Here I tried to compare the names, even though it's not a good pratice, and it didn't work either...
                    String Checked_Name = myListView.getAdapter().getItem(checked_Names_Counter).toString();

                    // Checks if the current checked ID matches the cursors ID, if not move the cursor to the next name
                    while (Checked_Name != cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)))
                    {
                        cur.moveToNext();
                    }
                    */
                    String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    String name = "";
                    String no = "";

                    Cursor phoneCur = contect_resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);


                    name = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    no = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    id = null;
                    name = null;
                    no = null;
                    phoneCur = null;
                    checked_Names_Counter++;

                }
            }

            // Goes back to the Manage Groups screen
            i = new Intent(this, MinyanSettingsActivity.class);
            startActivity(i);
            break;  
    }

  }

}

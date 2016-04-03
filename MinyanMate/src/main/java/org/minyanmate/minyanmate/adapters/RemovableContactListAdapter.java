package org.minyanmate.minyanmate.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import org.minyanmate.minyanmate.ContactManagerActivity;
import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.ContactMatrix;
import org.minyanmate.minyanmate.database.MinyanContactsTable;


/**
 * An adapter for a {@link android.widget.ListView} for a list of contacts
 * with QuickContactBadges (with photos), names, and a button for removing
 * said contact from the list (and database).
 * @author Jeff
 *
 */
public class RemovableContactListAdapter extends CursorAdapter {

    private final RemovableContactCallbacks removableContactCallbacks;

    public RemovableContactListAdapter(Context context, Cursor c, RemovableContactCallbacks removableContactCallbacks) {
		super(context, c, false);
        this.removableContactCallbacks = removableContactCallbacks;
    }



	@Override
	public void bindView(View view, Context context,
			Cursor cur) {
		
		final Context c = context;
		
		QuickContactBadge badge = (QuickContactBadge) view.findViewById(R.id.removableContactBadge);
		TextView nameText = (TextView) view.findViewById(R.id.removableContactName);
		ImageButton imgButton = (ImageButton) view.findViewById(R.id.removableRemoveButton);

        final String name = cur.getString(ContactMatrix.DISPLAY_NAME);
		final long contactId = cur.getLong(ContactMatrix.CONTACT_ID);
		final String lookUpKey = cur.getString(ContactMatrix.LOOKUP_KEY);
        final int phoneNumberId = cur.getInt(ContactMatrix.PHONE_NUMBER_ID);
        final int minyanScheduleId = cur.getInt(ContactMatrix.CONTACT_SCHEDULE_ID);
		Uri contactUri = Contacts.getLookupUri(contactId, lookUpKey);

		nameText.setText(name);
		badge.assignContactUri(contactUri);
		
		if (null == (cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI)))
			badge.setImageResource(R.drawable.social_person_light);
		else {
			Uri imageUri = Uri.parse(cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI));
			badge.setImageURI(imageUri);
		}

        /*
        *  If removableContactsCallbacks is an instance of IndistinctContactsCallbacks,
        *  it's because it's meant to make the contact name have a callback.
        */
        if (removableContactCallbacks instanceof IndistinctContactCallbacks) {
            nameText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    removableContactCallbacks.onClickTextView(c, phoneNumberId, name );
                }
            });
        }

        /*
        * Need to set a callback on the ImageButton. Either the View being generated represents
        * a multitude of contact instances, in which case they should all be deleted; or the View being
        * generated represents only a single instance of a single contact, in which case only that
        * one should be deleted.
        */
		imgButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked

                            removableContactCallbacks.delete(c,
                                    Integer.toString(phoneNumberId), String.valueOf(minyanScheduleId));

				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(c);
				builder.setMessage("Are you sure you want to delete this contact?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
			}
		});
	}
	
	@Override
	public View newView(Context context, Cursor cur,
			ViewGroup viewGroup) {
		return LayoutInflater.from(context).
				inflate(R.layout.fragment_removable_contact, viewGroup, false);
	}

    /**
     * An interface to pass into this adapter so that it can have different behavior when used in
     * different contexts. (This eliminates having redundant files and boilerplate code.) There
     * are two callbacks: {@link #delete(android.content.Context, String, String)}, which is called
     * when the ImageButton is pressed, to delete either a single person from a single schedule,
     * or a single person from many schedules; and {@link #onClickTextView(android.content.Context, int, String)},
     * which is called when the TextView.
     */
    public interface RemovableContactCallbacks {

        void onClickTextView(Context c, int phoneNumberId, String displayName);
        void delete(Context c, String phoneNumberId, String scheduleId);
    }

    /**
     * This implementation is called for {@link org.minyanmate.minyanmate.MinyanScheduleSettingsActivity}
     * and it deletes only a single contact from a single schedule. It isn't meant for TextViews.
     */
    static public class DistinctContactCallbacks implements RemovableContactCallbacks {

        @Override
        public void onClickTextView(Context c, int phoneNumberId, String displayName) {
        }

        @Override
        public void delete(Context c, String phoneNumberId, String scheduleId) {
            c.getContentResolver().delete(MinyanMateContentProvider.CONTENT_URI_CONTACTS,
                    MinyanContactsTable.COLUMN_PHONE_NUMBER_ID + "=?"
                            + " and " + MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?",
                    new String[] { phoneNumberId, scheduleId });
        }
    }

    static public class IndistinctContactCallbacks implements RemovableContactCallbacks {

        @Override
        public void onClickTextView(Context c, int phoneNumberId, String displayName) {
            Intent intent = new Intent(c, ContactManagerActivity.class);
            intent.putExtra(ContactManagerActivity.PHONE_ID, phoneNumberId);
            intent.putExtra(ContactManagerActivity.CONTACT_NAME, displayName);
            c.startActivity(intent);
        }

        @Override
        public void delete(Context c, String phoneNumberId, String scheduleId) {
            c.getContentResolver().delete(MinyanMateContentProvider.CONTENT_URI_CONTACTS,
                    MinyanContactsTable.COLUMN_PHONE_NUMBER_ID + "=?",
                    new String[] { phoneNumberId });
        }
    }
}

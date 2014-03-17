package org.minyanmate.minyanmate.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
		
		long contactId = cur.getLong(ContactMatrix.CONTACT_ID);
		final String lookUpKey = cur.getString(ContactMatrix.LOOKUP_KEY);
        final String phoneNumberId = cur.getString(ContactMatrix.PHONE_NUMBER_ID);
        final int scheduleId = cur.getInt(ContactMatrix.SCHEDULE_ID);
		Uri contactUri = Contacts.getLookupUri(contactId, lookUpKey);

		nameText.setText(cur.getString(ContactMatrix.DISPLAY_NAME));
		badge.assignContactUri(contactUri);
		
		if (null == (cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI)))
			badge.setImageResource(R.drawable.social_add_person_light);
		else {
			Uri imageUri = Uri.parse(cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI));
			badge.setImageURI(imageUri);
		}

		imgButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked

                            removableContactCallbacks.delete(c, phoneNumberId, String.valueOf(scheduleId));

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

    static public interface RemovableContactCallbacks {

        public void onClick();
        public void delete(Context c, String phoneNumberId, String scheduleId);
    }

    static public class DistinctContactCallbacks implements RemovableContactCallbacks {

        @Override
        public void onClick() {
            return;
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

        // TODO implement an interface for starting {@link ContactManagerActivity}
        @Override
        public void onClick() {

        }

        @Override
        public void delete(Context c, String phoneNumberId, String scheduleId) {
            c.getContentResolver().delete(MinyanMateContentProvider.CONTENT_URI_CONTACTS,
                    MinyanContactsTable.COLUMN_PHONE_NUMBER_ID + "=?",
                    new String[] { phoneNumberId });
        }
    }
}

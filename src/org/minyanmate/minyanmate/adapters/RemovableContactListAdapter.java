package org.minyanmate.minyanmate.adapters;

import org.minyanmate.minyanmate.R;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider.ContactMatrix;
import org.minyanmate.minyanmate.database.MinyanContactsTable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.TextView;


/**
 * An adapter for a {@link android.widget.ListView} for a list of contacts
 * with QuickContactBadges (with photos), names, and a button for removing
 * said contact from the list (and database).
 * @author Jeff
 *
 */
public class RemovableContactListAdapter extends CursorAdapter {

	private int prayerId;
	
	public RemovableContactListAdapter(Context context, Cursor c,
			int prayerId, boolean autoRequery) {
		super(context, c, autoRequery);
		this.prayerId = prayerId;
	}

	@Override
	public void bindView(View view, Context context,
			Cursor cur) {
		
		final Context c = context;
		
		QuickContactBadge badge = (QuickContactBadge) view.findViewById(R.id.removableContactBadge);
		TextView nameText = (TextView) view.findViewById(R.id.removableContactName);
		ImageButton imgButton = (ImageButton) view.findViewById(R.id.removableRemoveButton);
		
		long contactId = cur.getLong(ContactMatrix.ID);
		final String lookUpKey = cur.getString(ContactMatrix.KEY);
		Uri thumbnailPhotoUri = Contacts.getLookupUri(contactId, lookUpKey);
		
		nameText.setText(cur.getString(ContactMatrix.NAME));						
		badge.assignContactUri(thumbnailPhotoUri);
		
		if (null == (cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI)))
			badge.setImageResource(R.drawable.add_contact);
		else {
			Uri imageuri = Uri.parse(cur.getString(ContactMatrix.THUMBNAIL_PHOTO_URI));
			badge.setImageURI(imageuri);
		}

		imgButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				c.getContentResolver().delete(MinyanMateContentProvider.CONTENT_URI_CONTACTS, 
						MinyanContactsTable.COLUMN_CONTACT_LOOKUP_KEY + "=?"
						+ " and " + MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?", 
						new String[] { lookUpKey, String.valueOf(prayerId) });
			}
		});
	}
	
	@Override
	public View newView(Context context, Cursor cur,
			ViewGroup viewGroup) {
		return LayoutInflater.from(context).
				inflate(R.layout.fragment_removable_contact, viewGroup, false);
	}
}

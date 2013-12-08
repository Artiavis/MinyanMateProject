package org.minyanmate.minyanmate.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import org.minyanmate.minyanmate.R;

public class TermsOfService {
	
	public static void showTerms(Context c) {
		new AlertDialog.Builder(c)
			.setTitle("Terms of Service")
			.setMessage(Html.fromHtml(c.getString(R.string.terms_of_service)))
			.setIcon(R.drawable.ic_launcher)
			.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// do nothing
				}
			})
			.create().show();
	}
}

package org.minyanmate.minyanmate.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;

import static org.minyanmate.minyanmate.database.MinyanGoersTable.COLUMN_IS_INVITED;
import static org.minyanmate.minyanmate.database.MinyanGoersTable.QUERY_LATEST_GOERS;

public class OnMessageParticipantsSpinnerItemClicked implements android.widget.AdapterView.OnItemSelectedListener {

    private static final int SELECT = 0;
    private static final int ATTENDING = 1;
    private static final int ATTENDING_AWAITING = 2;
    private static final int AWAITING = 3;
    private static final int ALL = 4;

    private Dialog dialog;

    public OnMessageParticipantsSpinnerItemClicked(Dialog d) {
        dialog = d;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

        String whoToSelect = " AND " + COLUMN_IS_INVITED + "=1";

        switch (pos) {

            case SELECT:
                return;

            default:

            case ATTENDING:
                whoToSelect += " AND " + MinyanGoersTable.COLUMN_INVITE_STATUS + "=" + InviteStatus.toInteger(InviteStatus.ATTENDING);
                break;
            case ATTENDING_AWAITING:
                whoToSelect += " AND " + MinyanGoersTable.COLUMN_INVITE_STATUS + " IN (" +
                        InviteStatus.toInteger(InviteStatus.ATTENDING) + "," +
                        InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE)
                        + ")";
                break;

            case AWAITING:
                whoToSelect += " AND " + MinyanGoersTable.COLUMN_INVITE_STATUS + "=" + InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE);
                break;

            case ALL:
                break;
        }

        dialog.dismiss();
        String query = QUERY_LATEST_GOERS + whoToSelect;
        Cursor cursorOfParticipants = adapterView.getContext().getContentResolver().query(
                MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS,
                null, query, null, null);

        StringBuilder stringBuilder = new StringBuilder("smsto:");
        while (cursorOfParticipants.moveToNext()) {
            stringBuilder.append(cursorOfParticipants.getString(MinyanMateContentProvider.GoerMatrix.PHONE_NUMBER));
//            if ( !cursorOfParticipants.isLast()) // apparently isn't necessary?
            stringBuilder.append(";");
        }
        cursorOfParticipants.close();
        String msg = stringBuilder.toString();

        Log.i("Spinner Sending to: ", msg);

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(msg));
        smsIntent.putExtra("exit_on_sent", true);
        view.getContext().startActivity(smsIntent);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

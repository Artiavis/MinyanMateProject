package org.minyanmate.minyanmate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;

public class UserParticipationPopupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null)
                showPopup(extras.getInt("eventId"));
            else
                finish();
        }
        else
            finish();
    }

    private void showPopup(int eventId) {

        final int id = eventId;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Minyan Mate Headcount");
        builder.setMessage("A minyan is being scheduled. Would you like to be counted for it?");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                ContentValues inviteValues = new ContentValues();
                inviteValues.put(MinyanGoersTable.COLUMN_DISPLAY_NAME, "Me");
                inviteValues.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.ATTENDING));
                inviteValues.put(MinyanGoersTable.COLUMN_IS_INVITED, 0);
                inviteValues.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, id);
                getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, inviteValues);

                finish();
            }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void createUserParticipationPopup(int eventId, Context context) {
        Intent intent = new Intent(context, UserParticipationPopupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", eventId);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

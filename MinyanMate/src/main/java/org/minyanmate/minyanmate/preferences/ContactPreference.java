package org.minyanmate.minyanmate.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.provider.ContactsContract;
import android.util.AttributeSet;


public class ContactPreference extends RingtonePreference {

    public ContactPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ContactPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContactPreference(Context context) {
        super(context);
    }

    public interface OnContactPickedListener {
        void onContactPicked(Uri uri);
    }

    private OnContactPickedListener mContactPickedListener;



    public void setOnContactPickedListener(OnContactPickedListener listener) {
        if (listener != null) {
            mContactPickedListener = listener;
        }
    }

    @Override
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        ringtonePickerIntent.setAction(Intent.ACTION_PICK);
        ringtonePickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        ringtonePickerIntent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (super.onActivityResult(requestCode, resultCode, data)) {
            if (data != null) {
                Uri uri = data.getData();
                if (callChangeListener(uri != null ? uri.toString() : "")) {
                    onSaveRingtone(uri);
                }
            }
            return true;
        }
        return false;
    }

    protected void onSaveRingtone(Uri uri) {
        super.onSaveRingtone(uri);
        if (mContactPickedListener != null) {
            mContactPickedListener.onContactPicked(uri);
        }
    }

}

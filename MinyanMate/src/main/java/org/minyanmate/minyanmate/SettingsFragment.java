package org.minyanmate.minyanmate;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.ContactsContract;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        ListPreference timeZoneList = (ListPreference) findPreference(getString(R.string.timezonePreference));

        String[] regionList = getResources().getStringArray(R.array.minimal_timezones_list);
        String[] regionNames = new String[regionList.length];
        for (int i=0; i < regionList.length; i++) {

            if (!regionList[i].matches(".*/.*"))
                continue;

            regionNames[i] = regionList[i].replaceAll("_", " ");
        }

        timeZoneList.setEntries(regionNames);
        timeZoneList.setEntryValues(regionList);
    }

    private void initializeNotificationForwardingSummary() {
        String contactUriString = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(
                getActivity().getString(R.string.forwardContactPreference),"");
        Uri contactUri = Uri.parse(contactUriString);
        RingtonePreference forwardPreference = (RingtonePreference) findPreference(getString(R.string.forwardContactPreference));

        Cursor phoneContacts = getActivity().getContentResolver().query(contactUri,
               null, null, null, null);

        if (phoneContacts != null) {
            if (phoneContacts.moveToFirst()) {
                String name = phoneContacts.getString(phoneContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                forwardPreference.setSummary("You have selected " + name + " to receieve automatic messages.");
            }
            phoneContacts.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        initializeNotificationForwardingSummary();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.i("Inside SettingsFragment", "Inside onSharedPreferenceChanged");

        Log.i("Inside SettingsFragment", "Key: " + key);

    }
}

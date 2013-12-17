package org.minyanmate.minyanmate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

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

    @Override
    public void onResume() {
        super.onResume();
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

    }
}

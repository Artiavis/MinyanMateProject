package org.minyanmate.minyanmate;


import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity{

    public SettingsActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


}

package org.minyanmate.minyanmate;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.minyanmate.minyanmate.dialogs.TermsOfService;

import java.util.Locale;
import java.util.TimeZone;

public class MinyanMateActivity extends FragmentActivity implements
		ActionBar.TabListener {

    /**
	 * The {@link ViewPager} that will host the section contents.
	 */
    private ViewPager mViewPager;

    public MinyanMateActivity() {
        super();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minyan_mate);

        // Check if first app install, if so, initialize TimeZone
        initializeTimeZone();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // TODO this should have a polymorphic (and custom) menu for each pgae

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		/*
	  The {@link android.support.v4.view.PagerAdapter} that will provide
	  fragments for each of the sections. We use a
	  {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	  will keep every loaded fragment in memory. If this becomes too memory
	  intensive, it may be best to switch to a
	  {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
//                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setIcon(mSectionsPagerAdapter.getPageIcon(i))
                    .setTabListener(this));
		}
	}



    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.minyan_mate, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_terms_of_service:
	            TermsOfService.showTerms(this);
	            return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
	        default:
	        	return true;
	    }
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new ActiveMinyanFragment();
			case 1:
				return new MinyanScheduleDashboardFragment();
			default:
				return new MinyanScheduleDashboardFragment();
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

        public int getPageIcon(int position) {
            switch (position) {
            case 0:
                return R.drawable.collections_view_as_list_light;
            case 1:
                return R.drawable.device_access_time_light;
            case 2:
                return R.drawable.social_cc_bcc_light;
            default:
                break;
            }
            return R.drawable.ic_launcher;
        }
        
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_active_minyan).toUpperCase(l);
			case 1:
				return getString(R.string.title_edit_minyans).toUpperCase(l);
            case 2:
                return "Manage Contacts".toUpperCase(l);
			}
			return null;
		}
	}

    private void initializeTimeZone() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String timeZone = preferences.getString(getString(R.string.timezonePreference),"");
        Log.i("Current Application Time Zone: ", timeZone);

        // Only check if not default
        if (timeZone.equalsIgnoreCase("") || timeZone.equalsIgnoreCase("temp")) {
            TimeZone tz = TimeZone.getDefault();
            Log.i("Current Device Time Zone: ", tz.getDisplayName());
            String[] timeZoneList = getResources().getStringArray(R.array.minimal_timezones_list);

            for (String aTimeZone : timeZoneList) {
                if (tz.hasSameRules(TimeZone.getTimeZone(aTimeZone))) {
                    preferences.edit().putString(getString(R.string.timezonePreference, ""), aTimeZone).commit();
                }
            }
        }
    }
}

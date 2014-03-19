package org.minyanmate.minyanmate;

import android.app.ActionBar;
import android.app.Activity;
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

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ActionItemTarget;

import org.minyanmate.minyanmate.dialogs.TermsOfService;

import java.util.Locale;
import java.util.TimeZone;

public class MinyanMateActivity extends FragmentActivity implements
		ActionBar.TabListener {

    /**
	 * The {@link ViewPager} that will host the section contents.
	 */
    ViewPager mViewPager;

    public MinyanMateActivity() {
        super();
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_minyan_mate);

        // Check if first app install, if so, initialize
        initializeFirstInstall();


		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

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

        // Show tutorial
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tutorial", true)) {
            showTutorial();
        }
	}

    public void showTutorial() {

        Log.d("Inside showTutorial()","Clearing preferences");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.edit().clear().commit();

        Log.d("Inside showTutorial()","Entering Tutorial");
        // Open first tab
        mViewPager.setCurrentItem(2);
//        getActionBar().getTabAt(0).getCustomView();
        final ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
        co.hideOnClickOutside = false;
        co.block = true;

        final ShowcaseView sv1;
        final Activity activity = this;

        sv1 = ShowcaseView.insertShowcaseView(new ActionItemTarget(this, R.id.moreOptsMenu_AddNewContact),
                this, "Add to Minyan", "Add a person to count or invite another person", co);
        sv1.show();
//
//        sv1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowcaseView sv2 = ShowcaseView.insertShowcaseView(
//                    new ActionItemTarget(activity, R.id.moreOptsMenu_MessageParticipants),
//                    activity, "Add to Minyan", "Add a person to count or invite another person", co);
//
//                sv2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ShowcaseView sv3 = ShowcaseView.insertShowcaseView(
//                        new ActionItemTarget(activity, R.id.moreOptsMenu_ShareHeadcount),
//                        activity, "Add to Minyan", "Add a person to count or invite another person", co);
//                        sv3.show();
//                    }
//                });
//                sv2.show();
//            }
//        });
//
//        sv1.show();
//        ShowcaseViewBuilder builder= new ShowcaseViewBuilder(this);
//        bu.setText("Minyan Dashboard", "This tab acts as a dashboard when viewing active minyans")
//                .setConfigOptions(co).build().show();
//        getActionBar().getTabAt(0).getCustomView();
//        sv = ShowcaseView.insertShowcaseView(ShowcaseView.NONE,
//                this, "Add to Minyan", "Add a person to count or invite another person", co);
//        sv.show();

//        sv = ShowcaseView.insertShowcaseView(new ViewTarget(mViewPager.getChildAt(0)), this,
//                "Minyan Dashboard", "This tab acts as a dashboard when viewing active minyans", co);
//        sv.show();

//        sv = ShowcaseView.insertShowcaseView(new ActionItemTarget(this, R.id.moreOptsMenu_AddPerson),
//                this, "Add to Minyan", "Add a person to count or invite another person", co);
//        sv.show();

//        ShowcaseViews views = new TabbedShowcaseViews(this, new ShowcaseViews.OnShowcaseAcknowledged() {
//            @Override
//            public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
//
//            }
//        }, mViewPager);

//        ShowcaseViews views = new ShowcaseViews(this);
//
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.moreOptsMenu_AddPerson,
//                R.string.scheduleTutorial_clickTitle, R.string.scheduleTutorial_clickDesc, co));
////        ), false);
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.moreOptsMenu_MessageParticipants,
//                R.string.scheduleTutorial_clickTitle, R.string.scheduleTutorial_clickDesc, co));
////        ), false);
//        views.addView(new ShowcaseViews.ItemViewProperties(R.id.moreOptsMenu_ShareHeadcount,
//                R.string.scheduleTutorial_clickTitle, R.string.scheduleTutorial_clickDesc, co));
////        ), true);
//
//        views.show();

/*//            expListView.expandGroup(0);
//            ShowcaseViewBuilder showcaseViewBuilder = new ShowcaseViewBuilder(getActivity());
//            showcaseViewBuilder.setShowcaseView(
//                    ((LinearLayout) listAdapter.getChild(0,0)).findViewById(R.id.minyanTimeCheckbox)
//            );
//            expListView.getAdapter().getView(0, null, null);
//            View parent = ((LinearLayout) listAdapter.getChild(0,0));
//            int checkViewId = getActivity().findViewById(R.id.minyanTimeCheckbox).getId();
//            int textViewId = getActivity().findViewById(R.id.minyanTimeTextview).getId();
//            getActivity().findViewByI
        ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
//            co.hideOnClickOutside = true;

//            ShowcaseViews showcaseViews = new ShowcaseViews(getActivity(),
                new ShowcaseViews.OnShowcaseAcknowledged() {
                    @Override
                    public void onShowCaseAcknowledged(ShowcaseView showcaseView) {
                        PreferenceManager.getDefaultSharedPreferences(getActivity())
                                .edit().putBoolean(SCHEDULE_TUTORIAL, false);
                    }
                });

        showcaseViews.addView(new ShowcaseViews.ItemViewProperties(//R.id.minyanTimeCheckbox,
                R.string.scheduleTutorial_checkTitle, R.string.scheduleTutorial_checkDesc));
        showcaseViews.addView(new ShowcaseViews.ItemViewProperties(//R.id.minyanTimeTextview,
                R.string.scheduleTutorial_clickTitle, R.string.scheduleTutorial_clickDesc));

        Log.d("Inside showTutorial()","Showing Tutorial");

        showcaseViews.show();
//            showcaseView = ShowcaseView.insertShowcaseView(R.id.moreOptsMenu_AddPerson, getActivity(), "Test", "testing", co);*/

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
	        	break;
	    }
        return false;
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
            case 2:
                return new ContactsManagerFragment();
			default:
				return new MinyanScheduleDashboardFragment();
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

        public int getPageIcon(int position) {
            switch (position) {
            case 0:
                return R.drawable.collections_view_as_list_light;
            case 1:
                return R.drawable.collections_go_to_today_light;
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

    /**
     * This is called whenever entering the application to check the User Preferences for variables
     * which may need to be initialized.
     */
    private void initializeFirstInstall() {
        initializeTimeZone();
    }

    /**
     * Check whether the application has had a timezone initialized. If not, intialize to current
     * time zone.
     */
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

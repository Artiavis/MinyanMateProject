package org.minyanmate.minyanmate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class MinyanSettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_minyan_settings);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button by going back
		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
			Intent intent = new Intent(getBaseContext(), MinyanMateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MinyanSettingsActivity.this.finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}

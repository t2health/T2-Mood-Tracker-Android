package com.t2.vas.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.Eula;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;

public abstract class ABSActivity extends Activity {
	private static final String TAG = ABSActivity.class.getName();

	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbAdapter.open();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        if(!Global.DEV_MODE) { 
	        if(Global.REMOTE_STACK_TRACE_URL != null && Global.REMOTE_STACK_TRACE_URL.length() > 0) {
	        	ExceptionHandler.register(this, Global.REMOTE_STACK_TRACE_URL);
	        }
        }
        
        Eula.show(this);
        VASAnalytics.onPageView();
        VASAnalytics.onEvent(this.getClass().getSimpleName());
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		Analytics.onStartSession(this);
		
		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		Analytics.onEndSession(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.global, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch(item.getItemId()) {
			case R.id.help:
				i = new Intent(this, HelpActivity.class);
				i.putExtra(HelpActivity.EXTRA_TARGET, this.getHelpTarget());
				i.putExtra(HelpActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivity(i);
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public String getHelpTarget() {
		return this.getClass().getSimpleName();
	}
}

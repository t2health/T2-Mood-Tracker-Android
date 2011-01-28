package com.t2.vas.activity.preference;

import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.Global;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public abstract class ABSPreferenceActivity extends PreferenceActivity {
	private static final String TAG = ABSActivity.class.getName();

	protected SharedPreferences sharedPref;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        if(!Global.DEV_MODE) { 
	        if(Global.REMOTE_STACK_TRACE_URL != null && Global.REMOTE_STACK_TRACE_URL.length() > 0) {
	        	ExceptionHandler.register(this, Global.REMOTE_STACK_TRACE_URL);
	        }
        }
        
        VASAnalytics.onPageView();
        VASAnalytics.onEvent(this.getClass().getName());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Analytics.onStartSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();

		Analytics.onEndSession(this);
	}
}

/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.Global;
import com.t2.vas.VASAnalytics;

public abstract class ABSPreferenceActivity extends PreferenceActivity {
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
        VASAnalytics.onEvent(this, this.getClass().getName());
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

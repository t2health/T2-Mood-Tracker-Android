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
package com.t2.vas.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.AppSecurityManager;
import com.t2.vas.DBInstallData;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.DBAdapter.OnDatabaseCreatedListener;
import com.t2.vas.db.DBAdapter.OnDatabaseUpdatedListener;

@SuppressWarnings("deprecation")
public abstract class ABSTabActivity extends TabActivity  implements OnDatabaseCreatedListener, OnDatabaseUpdatedListener {
	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;

	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.tabs);

		sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(!Global.DEV_MODE) { 
			if(SharedPref.getSendAnnonData(sharedPref) && 
					Global.REMOTE_STACK_TRACE_URL != null && 
					Global.REMOTE_STACK_TRACE_URL.length() > 0) {
				ExceptionHandler.register(this, Global.REMOTE_STACK_TRACE_URL);
			}
		}

		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		dbAdapter.setOnCreateListener(this);
		dbAdapter.setOnUpdatedListener(this);
		dbAdapter.open();

		if(!Global.DEV_MODE) {
			VASAnalytics.init(Global.FLURRY_KEY, SharedPref.getSendAnnonData(sharedPref));
			VASAnalytics.setDebugEnabled(true);
			VASAnalytics.onPageView();
			VASAnalytics.onEvent(this, this.getClass().getSimpleName());
		}


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

	public String getHelpTarget() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void onDatabaseCreated(SQLiteDatabase db) {
		DBInstallData.install(this, this.dbAdapter, db);
		DBInstallData.createInitialData(dbAdapter, Global.Database.CREATE_FAKE_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DBInstallData.update(this, this.dbAdapter, db, oldVersion, newVersion);
	}


}

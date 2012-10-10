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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public class SplashScreenActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
	private TextView startupTipsView;
	private Timer startTimer;
	protected SharedPreferences sharedPref;
	boolean showStartupTips = false;
	int nextTimeout = 2500;
	
	private Handler startHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startMainActivity();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		this.setContentView(R.layout.splash_screen_activity);
		this.findViewById(R.id.splashWrapper).setOnClickListener(this);
		
		// configure the startup tips
		String[] startupTips = this.getResources().getStringArray(R.array.startup_tips);
		showStartupTips = SharedPref.getShowStartupTips(sharedPref);
		CheckBox startupTipsCheckbox = (CheckBox)this.findViewById(R.id.showTipsCheckbox);
		startupTipsCheckbox.setChecked(showStartupTips);
		startupTipsCheckbox.setOnCheckedChangeListener(this);
		
		// init the startup tips
		startupTipsView = (TextView)this.findViewById(R.id.startupTips);
		if(!showStartupTips) {
			startupTipsView.setVisibility(View.GONE);
		}
		startupTipsView.setText(
				startupTips[new Random().nextInt(startupTips.length)]
		);
		
		// configure the auto-start
		/*if(showStartupTips) {
			nextTimeout = 10000;
		}*/
		if(!showStartupTips)
		{
			TextView txtcontinue = (TextView)this.findViewById(R.id.txtcontinue);
			txtcontinue.setVisibility(View.GONE);
		startTimer = new Timer();
		startTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				startHandler.sendEmptyMessage(0);
			}
		}, nextTimeout);
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
		case R.id.showTipsCheckbox:
			SharedPref.setShowStartupTips(sharedPref, isChecked);
			showStartupTips = SharedPref.getShowStartupTips(sharedPref);
			if(isChecked) {
				try
				{
				startTimer.cancel();
				}
				catch(Exception ex){}
				TextView txtcontinue = (TextView)this.findViewById(R.id.txtcontinue);
				txtcontinue.setVisibility(View.VISIBLE);
				startupTipsView.setVisibility(View.VISIBLE);
			} else {
				TextView txtcontinue = (TextView)this.findViewById(R.id.txtcontinue);
				txtcontinue.setVisibility(View.GONE);
				startTimer = new Timer();
				startTimer.schedule(new TimerTask(){
					@Override
					public void run() {
						startHandler.sendEmptyMessage(0);
					}
				}, nextTimeout);
				startupTipsView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.splashWrapper:
			if(showStartupTips)
			{
				startMainActivity();
			}
		}
	}

	private void startMainActivity() {
		Intent i = new Intent(this, MainTabActivity.class);
		this.startActivity(i);
		this.finish();
	}
}

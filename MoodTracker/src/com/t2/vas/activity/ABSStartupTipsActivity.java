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

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public abstract class ABSStartupTipsActivity extends ABSActivity implements OnCheckedChangeListener {

	private TextView startupTipsView;
	protected boolean showStartupTips;
	private String[] startupTips;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startupTips = this.getResources().getStringArray(R.array.startup_tips);
		showStartupTips = SharedPref.getShowStartupTips(sharedPref);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
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
	}



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
		case R.id.showTipsCheckbox:
			SharedPref.setShowStartupTips(sharedPref, isChecked);
			if(isChecked) {
				startupTipsView.setVisibility(View.VISIBLE);
			} else {
				startupTipsView.setVisibility(View.GONE);
			}
		}
	}
}

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

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.t2.vas.R;

public abstract class ABSNavigationActivity extends ABSSecurityActivity {
	public static final String EXTRA_BACK_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	
	private View leftButton;
	private View rightButton;
	
	protected void setBackButtonText(String s) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		Button b = new Button(this);
		b.setText(s);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackButtonPressed();
			}
		});
		
		this.leftButton = b;
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void setBackButtonImage(int imageRes) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		ImageButton b = new ImageButton(this);
		b.setImageResource(imageRes);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackButtonPressed();
			}
		});
		
		this.leftButton = b;
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void onBackButtonPressed() {
		this.finish();
	}
	
	protected void setRightButtonText(String s) {
		Button b = new Button(this);
		b.setText(s);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRightButtonPressed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void setRightButtonImage(int imageRes) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		ImageButton b = new ImageButton(this);
		b.setImageResource(imageRes);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRightButtonPressed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	public void setRightButtonVisibility(int v) {
		this.findViewById(R.id.rightButtonContainer).setVisibility(v);
	}
	
	protected void onRightButtonPressed() {
		
	}
	
	protected View getRightButton() {
		return rightButton;
	}
	
	protected View getLeftButton() {
		return leftButton;
	}

	@Override
	protected void initCustomTitle() {
		super.initCustomTitle();
		
		Intent i = this.getIntent();
		if(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT) != null) {
			this.setBackButtonText(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT));
		}
		
		if(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT) != null) {
			this.setRightButtonText(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT));
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.onBackButtonPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

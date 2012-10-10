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

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.t2.vas.R;

public abstract class ABSPreferenceNavigation extends ABSSecurityPreferenceActivity {
	public static final String EXTRA_BACK_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_BACK_BUTTON_RESID = "leftButtonResId";
	
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	public static final String EXTRA_RIGHT_BUTTON_RESID = "leftButtonResId";
	private boolean initialized = false;
	
	private View leftButton;
	private View rightButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		//this.initCustomTitle();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		//this.initCustomTitle();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		//if(initialized) {
		//	setCustomTitle(title);
		//} else {
			super.setTitle(title);
		//}
	}

	@Override
	public void setTitle(int titleId) {
		//if(initialized) {
		//	setCustomTitle(this.getResources().getString(titleId));
		//} else {
			super.setTitle(titleId);
		//}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.onBackButtonPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void setBackButtonText(String s) {
		if(!initialized) {
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
		if(!initialized) {
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
				onRightButtonPresed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void setRightButtonImage(int imageRes) {
		if(!initialized) {
			return;
		}
		
		ImageButton b = new ImageButton(this);
		b.setImageResource(imageRes);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRightButtonPresed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void onRightButtonPresed() {
		
	}
	
	protected View getRightButton() {
		return rightButton;
	}
	
	protected View getLeftButton() {
		return leftButton;
	}
	
	private void initCustomTitle() {
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		setCustomTitle(super.getTitle());
		
		Intent i = this.getIntent();
		if(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT) != null) {
			this.setBackButtonText(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT));
			
		} else if(i.getIntExtra(EXTRA_BACK_BUTTON_RESID, 0) != 0) {
			this.setBackButtonImage(i.getIntExtra(EXTRA_BACK_BUTTON_RESID, 0));
		}
		
		if(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT) != null) {
			this.setRightButtonText(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT));
			
		} else if(i.getIntExtra(EXTRA_RIGHT_BUTTON_RESID, 0) != 0) {
			this.setRightButtonImage(i.getIntExtra(EXTRA_RIGHT_BUTTON_RESID, 0));
		}
	}
	
	private void setCustomTitle(CharSequence text) {
		TextView tv = (TextView)this.findViewById(R.id.custom_title).findViewById(R.id.text1);
		tv.setText(text);
	}
}

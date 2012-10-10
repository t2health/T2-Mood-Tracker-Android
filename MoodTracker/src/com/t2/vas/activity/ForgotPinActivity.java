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

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public class ForgotPinActivity extends ABSActivity implements OnKeyListener, OnEditorActionListener, OnClickListener {
	private EditText answer1EditText;
	private EditText answer2EditText;
	private String answer1;
	private String answer2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.forgot_pin_activity);
		
		((TextView)this.findViewById(R.id.question1)).setText(
				SharedPref.Security.getQuestion1(sharedPref)
		);
		((TextView)this.findViewById(R.id.question2)).setText(
				SharedPref.Security.getQuestion2(sharedPref)
		);
		
		answer1 = SharedPref.Security.getAnswer1(sharedPref);
		answer2 = SharedPref.Security.getAnswer2(sharedPref);
		
		answer1EditText = (EditText)this.findViewById(R.id.answer1);
		answer1EditText.setOnKeyListener(this);
		answer1EditText.setOnEditorActionListener(this);
		
		answer2EditText = (EditText)this.findViewById(R.id.answer2);
		answer2EditText.setOnKeyListener(this);
		answer1EditText.setOnEditorActionListener(this);
		
		this.findViewById(R.id.cancelButton).setOnClickListener(this);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		checkForMatchedAnswers(v);
		return false;
	}
	
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		//Log.v(TAG, "ACTION");
		checkForMatchedAnswers(v);
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.cancelButton:
				this.finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private void checkForMatchedAnswers(View v) {
		int vId = v.getId();
		
		boolean match = false;
		if(vId == R.id.answer1) {
			match = responsesMatch(
					answer1EditText.getText().toString(),
					answer1
			);
			
		} else if(vId == R.id.answer2) {
			match = responsesMatch(
					answer2EditText.getText().toString(),
					answer2
			);
		}
		
		if(match) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
	
	private boolean responsesMatch(String text1, String text2) {
		String text1New = text1.trim().toLowerCase().replaceAll("[\\s\\._-]", "");
		String text2New = text2.trim().toLowerCase().replaceAll("[\\s\\._-]", "");
		
		if(text1New.equals(text2New)) {
			return true;
		}
		return false;
	}
}

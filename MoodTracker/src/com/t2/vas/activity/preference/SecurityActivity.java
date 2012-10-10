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

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import com.t2.vas.AppSecurityManager;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.ABSActivity;

public class SecurityActivity extends ABSActivity implements OnCheckedChangeListener {
	private CheckBox enabledCheckbox;
	private ViewGroup inputsContainer;
	private EditText pinEditText;
	private EditText question1EditText;
	private EditText answer1EditText;
	private EditText question2EditText;
	private EditText answer2EditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.security_activity);

		enabledCheckbox = (CheckBox)this.findViewById(R.id.securityEnabled);
		inputsContainer = (ViewGroup)this.findViewById(R.id.inputsContainer);
		pinEditText = (EditText)this.findViewById(R.id.pin);
		question1EditText = (EditText)this.findViewById(R.id.question1);
		answer1EditText = (EditText)this.findViewById(R.id.answer1);
		question2EditText = (EditText)this.findViewById(R.id.question2);
		answer2EditText = (EditText)this.findViewById(R.id.answer2);

		enabledCheckbox.setOnCheckedChangeListener(this);

		enabledCheckbox.setChecked(SharedPref.Security.isEnabled(sharedPref));
		pinEditText.setText(SharedPref.Security.getPin(sharedPref));
		question1EditText.setText(SharedPref.Security.getQuestion1(sharedPref));
		answer1EditText.setText(SharedPref.Security.getAnswer1(sharedPref));
		question2EditText.setText(SharedPref.Security.getQuestion2(sharedPref));
		answer2EditText.setText(SharedPref.Security.getAnswer2(sharedPref));

		setFieldsEnabled(enabledCheckbox.isChecked());

		// Hide the keyboard unless the user chooses a text view.
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onBackPressed() {

		boolean enabled = enabledCheckbox.isChecked();
		if(enabled)
		{

			if(!validSecurityDataEntererd()) {
				Toast.makeText(this, "Fill out all the fields.", Toast.LENGTH_LONG).show();
				return;
			}
			else
			{


				String pin = pinEditText.getText().toString().trim();
				String q1 = question1EditText.getText().toString().trim();
				String a1 = answer1EditText.getText().toString().trim();
				String q2 = question2EditText.getText().toString().trim();
				String a2 = answer2EditText.getText().toString().trim();

				SharedPref.Security.setEnabled(sharedPref, enabled);
				SharedPref.Security.setPin(sharedPref, pin);
				SharedPref.Security.setChallenge1(
						sharedPref, 
						q1, 
						a1
						);
				SharedPref.Security.setChallenge2(
						sharedPref, 
						q2, 
						a2
						);

				AppSecurityManager.getInstance().setIsUnlocked(true);
				super.onBackPressed();
			}
		}
		else
		{
			SharedPref.Security.setEnabled(sharedPref, enabled);
			super.onBackPressed();
		}
	}

	private boolean validSecurityDataEntererd() {
		String pin = pinEditText.getText().toString().trim();
		String q1 = question1EditText.getText().toString().trim();
		String a1 = answer1EditText.getText().toString().trim();
		String q2 = question2EditText.getText().toString().trim();
		String a2 = answer2EditText.getText().toString().trim();

		return !enabledCheckbox.isChecked() || 
				(
						pin.length() > 0 && 
						q1.length() > 0 &&
						a1.length() > 0 &&
						q2.length() > 0 &&
						a2.length() > 0
						);
	}

	private void setFieldsEnabled(boolean b) {
		inputsContainer.setEnabled(b);
		for(int i = 0; i < inputsContainer.getChildCount(); ++i) {
			inputsContainer.getChildAt(i).setEnabled(b);
		}

		if(b) {
			pinEditText.requestFocus();
		} else {
			enabledCheckbox.requestFocus();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setFieldsEnabled(isChecked);
	}
}

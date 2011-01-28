package com.t2.vas.activity.preference;

import com.t2.vas.AppSecurityManager;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.ABSNavigationActivity;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class SecurityActivity extends ABSNavigationActivity implements OnCheckedChangeListener {
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
	protected void onBackButtonPressed() {
		if(!validSecurityDataEntererd()) {
			Toast.makeText(this, "Fill out all the fields.", Toast.LENGTH_LONG).show();
			return;
		}
		
		boolean enabled = enabledCheckbox.isChecked();
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
		
		AppSecurityManager.getInstance().setUnlocked(true);
		
		super.onBackButtonPressed();
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

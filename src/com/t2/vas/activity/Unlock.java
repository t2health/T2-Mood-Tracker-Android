package com.t2.vas.activity;

import java.util.Random;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class Unlock extends ABSStartupTips implements OnKeyListener, OnClickListener, OnCheckedChangeListener {
	private static final int FORGOT_PIN_ACTIVITY = 235;
	private EditText pinEditText;
	private String lockPin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.unlock_activity);
		pinEditText = (EditText)this.findViewById(R.id.pin);
		pinEditText.setOnKeyListener(this);
		this.lockPin = SharedPref.Security.getPin(sharedPref);
		
		this.findViewById(R.id.forgotPinButton).setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == FORGOT_PIN_ACTIVITY && resultCode == RESULT_OK) {
			this.setResult(RESULT_OK);
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.moveTaskToBack(true);
			return true;
		}
		
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if(v.getId() == R.id.pin) {
			String enteredPin = pinEditText.getText().toString().trim();
			if(enteredPin.length() > 0 && enteredPin.equals(lockPin)) {
				this.setResult(RESULT_OK);
				this.finish();
			}
		}
		return false;
	}
	
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
			case R.id.forgotPinButton:
				i = new Intent(this, ForgotPin.class);
				//i.putExtra(ForgotPin.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, FORGOT_PIN_ACTIVITY);
				break;
		}
	}
}

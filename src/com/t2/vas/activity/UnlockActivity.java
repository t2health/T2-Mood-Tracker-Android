package com.t2.vas.activity;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public class UnlockActivity extends ABSActivity implements OnKeyListener, OnClickListener, OnCheckedChangeListener {
	private static final int FORGOT_PIN_ACTIVITY = 235;
	private EditText pinEditText;
	private String lockPin;
	private TextView startupTipsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.unlock_activity);
		pinEditText = (EditText)this.findViewById(R.id.pin);
		pinEditText.setOnKeyListener(this);
		this.lockPin = SharedPref.Security.getPin(sharedPref);
		
		this.findViewById(R.id.forgotPinButton).setOnClickListener(this);
		
		
		// configure the startup tips
		String[] startupTips = this.getResources().getStringArray(R.array.startup_tips);
		boolean showStartupTips = SharedPref.getShowStartupTips(sharedPref);
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void onClick(View v) {
		Intent i;
		switch(v.getId()){
			case R.id.forgotPinButton:
				i = new Intent(this, ForgotPinActivity.class);
				//i.putExtra(ForgotPin.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, FORGOT_PIN_ACTIVITY);
				break;
		}
	}
}

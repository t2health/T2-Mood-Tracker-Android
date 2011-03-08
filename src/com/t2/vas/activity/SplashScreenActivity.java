package com.t2.vas.activity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

public class SplashScreenActivity extends ABSActivity implements OnClickListener, OnCheckedChangeListener {
	private static final String TAG = SplashScreenActivity.class.getName();

	private TextView startupTipsView;
	private Timer startTimer;
	private Handler startHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startMainActivity();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int nextTimeout = 2500;
		
		this.setContentView(R.layout.splash_screen_activity);
		this.findViewById(R.id.splashWrapper).setOnClickListener(this);
		
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
		
		// configure the auto-start
		if(showStartupTips) {
			nextTimeout = 10000;
		}
		startTimer = new Timer();
		startTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				startHandler.sendEmptyMessage(0);
			}
		}, nextTimeout);
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
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.splashWrapper:
			startTimer.cancel();
			startMainActivity();
		}
	}

	private void startMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		this.startActivity(i);
		this.finish();
	}
}

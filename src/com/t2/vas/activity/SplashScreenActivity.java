package com.t2.vas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.t2.vas.R;

public class SplashScreenActivity extends ABSStartupTipsActivity implements OnClickListener {
	private static final String TAG = SplashScreenActivity.class.getName();

	private TextView startupTipsView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!showStartupTips) {
			startMainActivity();
			return;
		}
		
		this.setContentView(R.layout.splash_screen_activity);
		this.findViewById(R.id.splashWrapper).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.splashWrapper:
			startMainActivity();
		}
	}

	private void startMainActivity() {
		Intent i = new Intent(this, MainActivity.class);
		this.startActivity(i);
		this.finish();
	}
}

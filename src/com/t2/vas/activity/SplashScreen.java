package com.t2.vas.activity;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.VASAnalytics;

public class SplashScreen extends ABSActivity implements OnClickListener {
	private static final String TAG = SplashScreen.class.getName();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		// Start the reminder service if we need to.
		ReminderService.startRunning(this);
		
		
		this.setContentView(R.layout.splash_screen);
		VASAnalytics.onEvent(VASAnalytics.EVENT_SPLASH_ACTIVITY);
		this.findViewById(R.id.nextButton).setOnClickListener(this);

		

		VASAnalytics.init(Global.ANALYTICS_KEY, this.sharedPref.getBoolean("send_anon_data", true));
		VASAnalytics.setEnabled(!Global.DEV_MODE);
		VASAnalytics.setDebugEnabled(true);
		ReminderServiceActivity.cancelReminderNotification(this);

		Toast toast = Toast.makeText(this, R.string.splash_intro, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();

		if(this.sharedPref.getBoolean("show_startup_tips", true)) {
			String[] startupTips = this.getResources().getStringArray(R.array.startup_tips);
			Random random = new Random();

			((TextView)this.findViewById(R.id.introText)).setText(
					startupTips[random.nextInt(startupTips.length)]
	        );
		} else {
			this.findViewById(R.id.healthTipWrapper).setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View arg0) {
		Intent i = new Intent(this, MainActivity.class);

		switch(arg0.getId()) {
			case R.id.nextButton:
				this.startActivity(i);
				this.finish();
				return;
		}

		super.onClick(arg0);
	}
}

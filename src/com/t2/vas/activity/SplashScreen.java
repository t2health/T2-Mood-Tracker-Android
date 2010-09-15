package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreen extends ABSActivity implements OnClickListener {
	private static final String TAG = SplashScreen.class.getName();
	private SharedPreferences sharedPref;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.splash_screen);
		VASAnalytics.onEvent(VASAnalytics.EVENT_SPLASH_ACTIVITY);
		this.findViewById(R.id.nextButton).setOnClickListener(this);

		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		VASAnalytics.init(Global.ANALYTICS_KEY, this.sharedPref.getBoolean("send_anon_data", true));
		VASAnalytics.setDebugEnabled(true);
		ReminderServiceActivity.cancelReminderNotification(this);

		Toast toast = Toast.makeText(this, R.string.splash_intro, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
		Intent i = new Intent(this, MainActivity2.class);

		switch(arg0.getId()) {
			case R.id.nextButton:
				this.startActivity(i);
				this.finish();
				return;
		}

		super.onClick(arg0);
	}
}

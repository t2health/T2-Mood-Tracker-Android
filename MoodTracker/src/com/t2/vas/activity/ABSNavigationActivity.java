/*
 * 
 */
package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.t2.vas.Global;
import com.t2.vas.NotificationService;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.TimePref;
import com.t2.vas.activity.editor.GroupActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class ABSNavigationActivity extends ABSSecurityActivity implements OnClickListener{
	public static final String EXTRA_BACK_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
	}

	protected void onBackButtonPressed() {
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			this.onBackButtonPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) 
	{

	}
}

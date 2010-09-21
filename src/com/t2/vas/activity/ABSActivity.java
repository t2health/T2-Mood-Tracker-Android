package com.t2.vas.activity;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.Eula;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

public class ABSActivity extends Activity implements OnClickListener {
	private static final String TAG = ABSActivity.class.getName();

	public static final int FORM_ACTIVITY = 345;
	public static final int RESULTS_ACTIVITY = 346;
	public static final int INFO_ACTIVITY = 347;
	public static final int ADD_GROUP_ACTIVITY = 348;
	public static final int MANAGE_SCALES_ACTIVITY = 349;
	public static final int ABOUT_ACTIVITY = 350;
	public static final int T2_WEBSITE_ACTIVITY = 351;
	/*public static final int NOTES_ACTIVITY = 348;
	public static final int REMINDER_ACTIVITY = 349;*/

	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbAdapter.open();

        if(!Global.DEV_MODE) { 
	        if(Global.REMOTE_STACK_TRACE_URL != null && Global.REMOTE_STACK_TRACE_URL.length() > 0) {
	        	ExceptionHandler.register(this, Global.REMOTE_STACK_TRACE_URL);
	        }
        }
        
        Eula.show(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        VASAnalytics.onPageView();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.initGlobalButtons();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.initGlobalButtons();
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		this.initGlobalButtons();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onStartSession(this, Global.FLURRY_KEY);
		}
		
		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if(sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onEndSession(this);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dbAdapter.close();
	}

	
	public void initGlobalButtons() {
		View v;

		v = this.findViewById(R.id.t2_logo);
		if(v != null) {
			v.setOnClickListener(this);
		}


		View bar = this.findViewById(R.id.globalButtonBar);
		if(bar == null) {
			return;
		}

		v = this.findViewById(R.id.globalButtonBar).findViewById(R.id.infoButton);
		if(v != null) {
			v.setOnClickListener(this);
		}

/*
		v = this.findViewById(R.id.globalButtonBar).findViewById(R.id.notesButton);
		if(v != null) {
			v.setOnClickListener(this);
		}

		v = this.findViewById(R.id.globalButtonBar).findViewById(R.id.reminderButton);
		if(v != null) {
			v.setOnClickListener(this);
		}*/
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.infoButton:
				VASAnalytics.onEvent(VASAnalytics.EVENT_INFO_ACTIVITY);
				this.startActivity(INFO_ACTIVITY);
				break;

			case R.id.t2_logo:
				VASAnalytics.onEvent(VASAnalytics.EVENT_WEBSITE_BUTTON_PRESSED);
				this.startActivity(T2_WEBSITE_ACTIVITY);
				break;
/*
			case R.id.notesButton:
				this.startActivity(NOTES_ACTIVITY);
				break;

			case R.id.reminderButton:
				this.startActivity(REMINDER_ACTIVITY);
				break;*/
		}
	}

	public void startActivity(int activityID) {
		Intent i = new Intent();

		switch(activityID) {
			case INFO_ACTIVITY:
				i.setAction("com.t2.vas.InfoActivity");
				i.putExtra("help_string_resource_id", this.getHelpResId());
				this.startActivityForResult(i, INFO_ACTIVITY);
				break;

			case T2_WEBSITE_ACTIVITY:
				i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.t2health.org"));
				this.startActivity(i);


			/*case NOTES_ACTIVITY:
				i.setAction("com.t2.vas.NotesActivity");
				this.startActivityForResult(i, NOTES_ACTIVITY);
				break;

			case REMINDER_ACTIVITY:
				i.setAction("com.t2.vas.ReminderPreferenceActivity");
				this.startActivityForResult(i, REMINDER_ACTIVITY);
				break;*/
		}
	}

	public int getHelpResId() {
		return -1;
	}
}

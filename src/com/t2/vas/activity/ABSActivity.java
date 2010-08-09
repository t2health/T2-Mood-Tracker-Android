package com.t2.vas.activity;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.Eula;

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

	private SharedPreferences sharedPref;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Eula.show(this);

        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
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

		if(this.sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onStartSession(this, Global.FLURRY_KEY);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		if(this.sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onEndSession(this);
		}
	}

	protected void aOnEvent(String event) {
		if(this.sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onEvent(event);
		}
	}

	protected void aOnEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		this.aOnEvent(event, params);
	}

	protected void aOnEvent(String event, Map<String,String> parameters) {
		if(this.sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onEvent(event, parameters);
		}
	}

	protected void aOnPageView() {
		if(this.sharedPref.getBoolean("send_anon_data", true)) {
			FlurryAgent.onPageView();
		}
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
				//this.tagEvent(Global.EVENT_TAGS.CLICK_INFO_BUTTON);
				this.startActivity(INFO_ACTIVITY);
				break;

			case R.id.t2_logo:
				//this.tagEvent(Global.EVENT_TAGS.CLICK_T2_LOGO);
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

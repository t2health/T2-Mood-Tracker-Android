package com.t2.vas.activity;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.Eula;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.preference.MainPreferenceActivity;
import com.t2.vas.db.DBAdapter;

public class ABSActivity extends Activity {
	private static final String TAG = ABSActivity.class.getName();

	public static final int FORM_ACTIVITY = 345;
	public static final int RESULTS_ACTIVITY = 346;
	public static final int INFO_ACTIVITY = 347;
	public static final int ADD_GROUP_ACTIVITY = 348;
	public static final int MANAGE_SCALES_ACTIVITY = 349;
	public static final int ABOUT_ACTIVITY = 350;
	public static final int T2_WEBSITE_ACTIVITY = 351;
	public static final int EDIT_GROUP_ACTIVITY = 352;
	public static final int DELETE_GROUP_ACTIVITY = 353;
	public static final int GROUP_DETAILS_ACTIVITY = 354;
	public static final int NOTE_ACTIVITY = 355;
	/*public static final int NOTES_ACTIVITY = 348;
	public static final int REMINDER_ACTIVITY = 349;*/
	
	private static final int HELP_MENU_ITEM = 123456;

	protected SharedPreferences sharedPref;
	protected DBAdapter dbAdapter;

	private MenuItem helpMenuItem;

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
	protected void onStart() {
		super.onStart();
		
		Analytics.onStartSession(this);
		
		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		Analytics.onEndSession(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.dbAdapter.close();
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
				this.startActivityForResult(i, T2_WEBSITE_ACTIVITY);
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.global, menu);
		
		if(this.getHelpResId() > -1) {
			MenuItem mi = menu.add(Menu.NONE, HELP_MENU_ITEM, Menu.NONE, R.string.help_title);
			mi.setIcon(android.R.drawable.ic_menu_help);
		}
		return true;
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch(item.getItemId()) {
			case HELP_MENU_ITEM:
				i = new Intent(this, WebViewActivity.class);
				i.putExtra(WebViewActivity.EXTRA_TITLE_ID, R.string.help_title);
				i.putExtra(WebViewActivity.EXTRA_CONTENT_ID, this.getHelpResId());
				i.putExtra(WebViewActivity.EXTRA_BACK_BUTTON_TEXT, "back");
				this.startActivity(i);
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}
	
	public int getHelpResId() {
		return -1;
	}
}

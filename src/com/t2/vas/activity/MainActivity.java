package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.activity.preference.MainPreferenceActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends BaseActivity implements OnItemSelectedListener, OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();
	
	private static final int FORM_ACTIVITY = 345;
	private static final int RESULTS_ACTIVITY = 346;
	private static final int NOTES_ACTIVITY = 347;
	private static final int REMINDER_ACTIVITY = 348;
	private static final int TIPS_ACTIVITY = 349;
	
	private static final int REPLAY_INTRO = 235325243;
	
	private ArrayAdapter<String> adapter;
	private ArrayList<Group> groupList;
	private Group currentGroup;
	private DBAdapter dbHelper;
	private Toast needScalesToast;

	private ArrayList<String> groupListString = new ArrayList<String>();
	private SharedPreferences sharedPrefs;

	private Toast formsAlreadyUsedToast;

	private ArrayList<HashMap<String, Object>> activityList;

	private ListView activityListView;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.main_activity);

        sharedPrefs = this.getSharedPreferences(Global.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        formsAlreadyUsedToast = Toast.makeText(this, R.string.activity_main_form_used, 3000);
        needScalesToast = Toast.makeText(this, R.string.activity_main_no_group_scales, 3000);
        dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();
        
        currentGroup = ((Group)dbHelper.getTable("group")).newInstance();
        groupList = currentGroup.getGroups();

        
        // Init the spinner and its data
        initDropDownAdapterData();
        adapter = new ArrayAdapter<String>(
        		this, 
        		android.R.layout.simple_spinner_item, 
        		groupListString
		);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinner = (Spinner)this.findViewById(R.id.groupSelector);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        
        dbHelper.close();
        
        
        // Init the activity list options
        activityList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> activityItem;
        
        activityItem = new HashMap<String, Object>();
        activityItem.put("activityId", FORM_ACTIVITY);
        activityItem.put("text1", this.getString(R.string.activity_form));
        activityItem.put("text2", this.getString(R.string.activity_form_desc));
        activityItem.put("image1", android.R.drawable.ic_menu_sort_by_size+"");
        activityList.add(activityItem);
        
        activityItem = new HashMap<String, Object>();
        activityItem.put("activityId", RESULTS_ACTIVITY);
        activityItem.put("text1", this.getString(R.string.activity_results));
        activityItem.put("text2", this.getString(R.string.activity_results_desc));
        activityItem.put("image1", android.R.drawable.ic_menu_slideshow+"");
        activityList.add(activityItem);
        
        activityItem = new HashMap<String, Object>();
        activityItem.put("activityId", NOTES_ACTIVITY);
        activityItem.put("text1", this.getString(R.string.activity_note_list));
        activityItem.put("text2", this.getString(R.string.activity_note_list_desc));
        activityItem.put("image1", android.R.drawable.ic_menu_agenda+"");
        activityList.add(activityItem);
        
        activityItem = new HashMap<String, Object>();
        activityItem.put("activityId", REMINDER_ACTIVITY);
        activityItem.put("text1", this.getString(R.string.activity_reminder_preference));
        activityItem.put("text2", this.getString(R.string.activity_reminder_preference_desc));
        activityItem.put("image1", android.R.drawable.ic_menu_recent_history+"");
        activityList.add(activityItem);
        
        activityListView = (ListView)this.findViewById(R.id.activityList);
        activityListView.setOnItemClickListener(this);
        activityListView.setAdapter(new SimpleAdapter(
        	this,
        	activityList,
        	R.layout.main_activity_list_item,
        	new String[]{
        			"text1",
        			"text2",
        			"image1",
        	},
        	new int[] {
        			R.id.text1,
        			R.id.text2,
        			R.id.image1,
        	}
        ));
        
        
		// Select the group to work with.
        if(groupList.size() > 0) {
        	boolean groupSelected = false;
        	long group_id = sharedPrefs.getLong("selected_group_id", groupList.get(0)._id);
        	if(group_id > 0) {
	        	for(int i = 0; i < groupList.size(); i++) {
	        		if(groupList.get(i)._id == group_id) {
	        			((Spinner)this.findViewById(R.id.groupSelector)).setSelection(i);
	        			groupSelected = true;
	        			break;
	        		}
	        	}
        	}
        	
        	if(!groupSelected) {
        		((Spinner)this.findViewById(R.id.groupSelector)).setSelection(0);
        	}
        }
        
        
        // Stopping any services that may be running and starting them up again.
        Intent serviceIntent = new Intent(this, ReminderService.class);
        this.stopService(serviceIntent);
        this.startService(serviceIntent);
        
        if(savedInstanceState == null) {
	        // Bring up the health tips.
	        if(!sharedPrefs.getBoolean("hide_startup_tips", false)) {
	        	Intent i = new Intent(this, StartupTipsActivity.class);
	        	i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	        	this.startActivity(i);
	        }
        }
	}
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("alreadyRunning", true);
		super.onSaveInstanceState(outState);
	}



	private void initDropDownAdapterData() {
		this.dbHelper.open();
		groupList = currentGroup.getGroups();
		
		// Convert the group list to an array of strings.
        groupListString.clear();
        for(int i = 0; i < groupList.size(); i++) {
        	groupListString.add(groupList.get(i).title);
        }
		
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	private void selectGroupAt(int index) {
		this.currentGroup = this.groupList.get(index);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initDropDownAdapterData();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences.Editor ed = sharedPrefs.edit();
		ed.putLong("selected_group_id", this.currentGroup._id);
		ed.commit();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case FORM_ACTIVITY:
				if(resultCode == Activity.RESULT_OK) {
					this.startActivity(RESULTS_ACTIVITY);
				}
				break;
		}
	}

	private void startActivity(int id) {
		ArrayList<Scale> scales;
		Intent intent;
		switch(id) {
			case FORM_ACTIVITY:
				// Disable the forms activity because it was already used today.
				this.dbHelper.open();
				long lastResult = this.currentGroup.getLatestResultTimestamp();
				scales = this.currentGroup.getScales();
				this.dbHelper.close();
				Calendar nowCal = Calendar.getInstance();
				Calendar thenCal = Calendar.getInstance();
				thenCal.setTimeInMillis(lastResult);
				
				// Dont start the activity becase it was already used.
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				if(sdf.format(nowCal.getTime()).equals(sdf.format(thenCal.getTime()))) {
					formsAlreadyUsedToast.show();
					
				// we require scales in order to use this activity.
				} else if(scales.size() <= 0) {
					needScalesToast.show();
					
				// Start the activity
				} else {
					intent = new Intent(this, FormActivity.class);
					intent.putExtra("group_id", this.currentGroup._id);
					this.startActivityForResult(intent, FORM_ACTIVITY);					
				}
				
				break;
				
			case RESULTS_ACTIVITY:
				this.dbHelper.open();
				scales = this.currentGroup.getScales();
				this.dbHelper.close();
				
				// we require scales in order to use this activity.
				if(scales.size() <= 0) {
						needScalesToast.show();
				} else {
					intent = new Intent(this, ResultsActivity.class);
					intent.putExtra("group_id", this.currentGroup._id);
					this.startActivityForResult(intent, RESULTS_ACTIVITY);
				}
				break;
				
			case NOTES_ACTIVITY:
				intent = new Intent(this, NotesActivity.class);
				intent.putExtra("group_id", this.currentGroup._id);
				this.startActivityForResult(intent, NOTES_ACTIVITY);
				break;
				
			case REMINDER_ACTIVITY:
				intent = new Intent(this, ReminderPreferenceActivity.class);
				intent.putExtra("group_id", this.currentGroup._id);
				this.startActivityForResult(intent, NOTES_ACTIVITY);
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		selectGroupAt(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	public int getHelp() {
		return R.string.activity_main_help;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.add(Menu.NONE, REPLAY_INTRO, 0, R.string.activity_main_replay_intro);
		item.setIcon(android.R.drawable.ic_menu_revert);
		
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch(item.getItemId()){
			case REPLAY_INTRO:
				i = new Intent(this, SplashScreen.class);
				this.startActivity(i);
				this.finish();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Integer activityId = (Integer)this.activityList.get(arg2).get("activityId");
		this.startActivity(activityId);
	}
}

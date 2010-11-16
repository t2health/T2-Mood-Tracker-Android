package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.editor.GroupActivity;
import com.t2.vas.activity.preference.Reminder;
import com.t2.vas.db.tables.Group;

public class MainActivity extends CustomTitle implements OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();
	private ListView groupListView;
	private Cursor groupListCursor;
	private ListView mainCommandsList;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Global.LONG_DATE_FORMAT);
	private SimpleCursorAdapter groupListAdapter;
	private boolean isFirstRun = true;
	private ArrayList<HashMap<String, String>> mainItems;
	
	private static final int ITEM_REMINDER = 9587;
	private static final int ITEM_NOTES = 9588;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main_activity);
        VASAnalytics.onEvent(VASAnalytics.EVENT_MAIN_ACTIVITY);
        
        this.setExtraButtonImage(R.drawable.info_blue);
        
        this.findViewById(R.id.addGroupButton).setOnClickListener(this);
        this.findViewById(R.id.showAllGroupsButton).setOnClickListener(this);
        this.findViewById(R.id.hideSomeGroupsButton).setOnClickListener(this);
        
        // Init the groups and the show/hide buttons.
        if(this.sharedPref.getBoolean("showAllGroupsInMain", false)) {
        	this.showAllGroups();
        } else {
        	this.hideSomeGroups();
        }
        
        
        
        
        this.isFirstRun = this.sharedPref.getBoolean("isFirstRun", true);
        
        // Build the main items.
        mainItems = new ArrayList<HashMap<String,String>>();
        HashMap<String,String> mainItem;
        
        mainItem = new HashMap<String,String>();
        mainItem.put("text1", this.getString(R.string.notes_list_title));
        mainItem.put("text2", this.getString(R.string.notes_list_desc));
        mainItem.put("action", "notes");
        mainItems.add(mainItem);
        
        mainItem = new HashMap<String,String>();
        mainItem.put("text1", this.getString(R.string.reminder_title));
        mainItem.put("text2", this.getString(R.string.reminder_desc));
        mainItem.put("action", "reminder");
        mainItems.add(mainItem);
        
        mainItem = new HashMap<String,String>();
        mainItem.put("text1", this.getString(R.string.introduction_title));
        mainItem.put("text2", this.getString(R.string.introduction_desc));
        mainItem.put("action", "intro");
        mainItems.add(mainItem);
        
        
        SimpleAdapter mainCommandsAdapter = new SimpleAdapter(
        		this,
        		mainItems,
        		R.layout.list_item_2_indicator,
        		new String[] {
        				"text1",
        				"text2",
        		},
        		new int[] {
        				R.id.text1,
        				R.id.text2,
        		}
        );
        mainCommandsList = (ListView)this.findViewById(R.id.globalList);
        mainCommandsList.setAdapter(mainCommandsAdapter);
        mainCommandsList.setOnItemClickListener(this);
        
        
        // Setup the group list adapter.
        groupListAdapter = new SimpleCursorAdapter(
        		this,
        		R.layout.list_item_2_indicator,
        		groupListCursor,
        		new String[] {
        				"title",
        				"last_result",
        		},
        		new int[] {
        				R.id.text1,
        				R.id.text2,
        		}
        );
        groupListAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(view.getId() == R.id.text2) {
					long timestamp = cursor.getLong(columnIndex);
					if(timestamp > 0) {
						((TextView)view).setText(
								simpleDateFormat.format(new Date(timestamp))
						);
					} else {
						((TextView)view).setText(
								getString(R.string.never)
						);
					}
					
					return true;
				}
				return false;
			}
		});
        groupListView = (ListView) this.findViewById(R.id.groupList);
        groupListView.setAdapter(groupListAdapter);
        groupListView.setOnItemClickListener(this);
        
        ((ScrollView)this.findViewById(R.id.scrollView)).scrollTo(0,0);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi1 = menu.add(Menu.NONE, ITEM_REMINDER, Menu.NONE, R.string.reminder_title);
		mi1.setIcon(R.drawable.reminder_default);
		
		MenuItem mi2 = menu.add(Menu.NONE, ITEM_NOTES, Menu.NONE, R.string.notes_title);
		mi2.setIcon(R.drawable.notes_default);
		
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch(item.getItemId()) {
			case ITEM_REMINDER:
				i = new Intent(this, Reminder.class);
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
				this.startActivity(i);
				return true;
				
			case ITEM_NOTES:
				i = new Intent();
				i.setAction("com.t2.vas.NotesList");
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
				this.startActivity(i);
				return true;
		
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}



	public void showAllGroups() {
		this.findViewById(R.id.showAllGroupsButton).setVisibility(View.GONE);
    	this.findViewById(R.id.hideSomeGroupsButton).setVisibility(View.VISIBLE);
    	
    	if(groupListCursor != null) {
    		this.stopManagingCursor(groupListCursor);
    		groupListCursor.close();
    	}
    	
    	groupListCursor = new Group(this.dbAdapter).getAllGroupsOrderByLastResultCursor();
    	this.startManagingCursor(groupListCursor);
    	if(this.groupListAdapter != null) {
    		this.groupListAdapter.changeCursor(groupListCursor);
    		this.groupListAdapter.notifyDataSetChanged();
    	}
	}
	
	public void hideSomeGroups() {
		this.findViewById(R.id.showAllGroupsButton).setVisibility(View.VISIBLE);
    	this.findViewById(R.id.hideSomeGroupsButton).setVisibility(View.GONE);
    	
    	if(groupListCursor != null) {
    		this.stopManagingCursor(groupListCursor);
    		groupListCursor.close();
    	}
    	
    	groupListCursor = new Group(this.dbAdapter).getVisibleGroupsOrderByLastResultCursor();
    	this.startManagingCursor(groupListCursor);
    	if(this.groupListAdapter != null) {
    		this.groupListAdapter.changeCursor(groupListCursor);
    		this.groupListAdapter.notifyDataSetChanged();
    	}
	}
	
	@Override
	public void onExtraButtonPressed() {
		//startSettingsActivity();
		this.getWindow().openPanel(Window.FEATURE_OPTIONS_PANEL, new KeyEvent 
				(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU)); 
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(arg0 == groupListView) {
			startGroupDetailsActivity(arg3, false);
			
		} else if(arg0 == mainCommandsList) {
			HashMap<String, String> data = mainItems.get(arg2);
			if(data.get("action").equals("intro")) {
				Intent i = new Intent(this, WebViewActivity.class);
				i.putExtra(WebViewActivity.EXTRA_TITLE, getString(R.string.introduction_title));
				i.putExtra(WebViewActivity.EXTRA_CONTENT, getString(R.string.introduction_content));
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
				i.putExtra("groupId", arg3);
				this.startActivity(i);
				
			} else if(data.get("action").equals("reminder")) {
				Intent i = new Intent(this, Reminder.class);
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
				this.startActivity(i);
				
			} else if(data.get("action").equals("notes")) {
				Intent i = new Intent();
				i.setAction("com.t2.vas.NotesList");
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
				i.putExtra("groupId", arg3);
				this.startActivity(i);
			}
		}
	}
	
	private void startGroupDetailsActivity(long id, boolean startAddScales) {
		Intent i = new Intent(this, GroupDetails.class);
		i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Main");
		i.putExtra(GroupDetails.EXTRA_GROUP_ID, id);
		i.putExtra(GroupDetails.EXTRA_START_SCALES, startAddScales);
		this.startActivityForResult(i, GROUP_DETAILS_ACTIVITY);
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch(v.getId()) {
			case R.id.addGroupButton:
				i = new Intent(this, GroupActivity.class);
				this.startActivityForResult(i, ADD_GROUP_ACTIVITY);
				return;
				
			case R.id.showAllGroupsButton:
				this.showAllGroups();
				return;
				
			case R.id.hideSomeGroupsButton:
				this.hideSomeGroups();
				return;
		}
		
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		groupListCursor.requery();
		groupListAdapter.notifyDataSetChanged();
		
		if(requestCode == ADD_GROUP_ACTIVITY) {
			if(data != null) {
				long groupId = data.getLongExtra(GroupActivity.EXTRA_GROUP_ID, 0);
				if(groupId > 0) {
					startGroupDetailsActivity(groupId, true);
				}
			}
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}



	@Override
	public int getHelpResId() {
		return R.string.introduction_content;
	}
}

package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.t2.vas.ArraysExtra;
import com.t2.vas.Global;
import com.t2.vas.MathExtra;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.preference.MainPreferenceActivity;
import com.t2.vas.activity.preference.ReminderActivity;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.InstallDB;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.view.SeparatedListAdapter;

public class MainActivity extends ABSNavigationActivity implements OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();
	public static final int RATE_ACTIVITY = 345;
	public static final int NOTE_ACTIVITY = 355;
	
	private SimpleAdapter rateGroupListAdapter;
	private ListView listView;
	private SeparatedListAdapter listAdapter;

	private GroupResultsDataProvider groupResultsDataProv;

	private long todayStartTime;
	private long todayEndTime;
	private Cursor groupsCursor;
	private ArrayList<HashMap<String, Object>> groupsDataList;
	private Context thisContext;
	private long previousRemindTime;
	private long nextRemindTime;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this;
        previousRemindTime = ReminderActivity.getPreviousRemindTimeSince(
        		thisContext,
        		Calendar.getInstance().getTimeInMillis()
		);
        nextRemindTime = ReminderActivity.getNextRemindTimeSince(
        		thisContext,
        		Calendar.getInstance().getTimeInMillis()
		);
        
        
        // Init today's start and end times.
        Calendar cal = Calendar.getInstance();
        MathExtra.roundTime(cal, Calendar.DAY_OF_MONTH);
        todayStartTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        todayEndTime = cal.getTimeInMillis();
        
        groupResultsDataProv = new GroupResultsDataProvider(this.dbAdapter);
        this.setContentView(R.layout.list_layout);
        
        this.setRightButtonText(getString(R.string.add_note));
        
        // Setup the group list adapter.
        groupsDataList = new ArrayList<HashMap<String,Object>>();
        updateGroupsDataList();
        
        
        rateGroupListAdapter = new SimpleAdapter(
        	this,
        	groupsDataList,
        	R.layout.list_item_1_image,
        	new String[] {
        			"title",
        			"showWarning",
        	},
        	new int[] {
        			R.id.text1,
    				R.id.image1,
        	}
        );
        rateGroupListAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				
				if(view.getId() == R.id.image1) {
					ImageView imageView = (ImageView)view;

					// show the warning.
					if((Boolean)data) {
						imageView.setImageResource(R.drawable.warning);
					// show the checkbox.
					} else {
						imageView.setImageResource(R.drawable.check);
					}
					
					return true;
				}
				return false;
			}
		});
        
        
        listAdapter = new SeparatedListAdapter(this);
        listAdapter.addSection(this.getString(R.string.rate_section_title), rateGroupListAdapter);
        listAdapter.addSection(this.getString(R.string.results_section_title), new SimpleAdapter(
        		this,
        		this.getResultsItems(),
        		R.layout.list_item_1_image,
        		new String[] {
        			"text1",
        			"image1",
        		},
        		new int[] {
        			R.id.text1,
        			R.id.image1,
        		}
        ));
        listAdapter.addSection(this.getString(R.string.general_section_title), new SimpleAdapter(
        		this,
        		this.getSettingsItems(),
        		R.layout.list_item_1_image,
        		new String[] {
        				"text1",
        				//"image1",
        		},
        		new int[] {
        				R.id.text1,
        				//R.id.image1,
        		}
        ));
        
        listView = (ListView)this.findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// Clear the reminder notification (if visible)
		ReminderService.clearNotification(this);
	}

	private void updateGroupsDataList() {
		groupsDataList.clear();
		
		List<Long> hiddenGids = SharedPref.getHiddenGroups(sharedPref);
		
		Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
		while(cursor.moveToNext()) {
			Group group = new Group(dbAdapter);
			group.load(cursor);
			
			if(hiddenGids.contains(group._id)) {
				continue;
			}
			
			
			Cursor resCursor = group.getResults(previousRemindTime, nextRemindTime);
			int resCount = resCursor.getCount();
			resCursor.close();
			
			HashMap<String,Object> data = new HashMap<String,Object>();
			data.put("_id", group._id);
			data.put("title", group.title);
			data.put("showWarning", resCount == 0);
			
			groupsDataList.add(data);
		}
		cursor.close();
	}
	
	protected void startSettingsActivity() {
		Intent i = new Intent(this, MainPreferenceActivity.class);
		i.putExtra(MainPreferenceActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		this.startActivityForResult(i, 123);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		updateGroupsDataList();
		listAdapter.notifyDataSetChanged();
		
		
		if(requestCode == RATE_ACTIVITY || requestCode == NOTE_ACTIVITY) {
			if(isNoteNecessaryForToday()) {
				notifyNoteIsNecessary();
			}
		}
	}

	private void notifyNoteIsNecessary() {
		Toast.makeText(this, R.string.unusual_results_message, Toast.LENGTH_LONG).show();
		
		this.getRightButton().startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation));
		this.getRightButton().setPressed(true);
	}

	private boolean isNoteNecessaryForToday() {
		Cursor cursor;
		Calendar startCal = Calendar.getInstance();
		MathExtra.roundTime(startCal, Calendar.DAY_OF_MONTH);
		
		Calendar startCalPast = Calendar.getInstance();
		startCalPast.setTimeInMillis(startCal.getTimeInMillis());
		startCalPast.add(Calendar.MONTH, -1);
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(startCal.getTimeInMillis());
		endCal.add(Calendar.DAY_OF_MONTH, 1);
		
		long startTime = startCal.getTimeInMillis();
		long startTimePast = startCalPast.getTimeInMillis();
		long endTime = endCal.getTimeInMillis();
		
		cursor = new Note(this.dbAdapter).queryForNotes(startTime, endTime, null);
		int notesCount = cursor.getCount();
		cursor.close();
		
		
		if(notesCount > 0) {
			Log.v(TAG, "Notes exist for today.");
			return false;
		}
		
		cursor = new Group(this.dbAdapter).getGroupsCursor();
		int colIndex = cursor.getColumnIndex("_id"); 
		while(cursor.moveToNext()) {
			long id = cursor.getLong(colIndex);
			double mostRecentVal = -1;
			
			Collection<Double> values = groupResultsDataProv.getData(
					id, 
					startTimePast, 
					endTime
			).values();
			
			double[] doubleValues = ArraysExtra.toArray(
					values.toArray(new Double[values.size()])
			);
			
			if(doubleValues.length > 0) {
				mostRecentVal = doubleValues[0];
			}
			
			if(mostRecentVal < 0) {
				continue;
			}
			
			double stdDev = MathExtra.stdDev(doubleValues);
			double mean = MathExtra.mean(doubleValues);
			double high = mean + stdDev;
			double low = mean - stdDev;
			
			if(mostRecentVal > high || mostRecentVal < low) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		
		return false;
	}

	private ArrayList<HashMap<String,Object>> getResultsItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> item;
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.graph_results_title));
        item.put("image1", R.drawable.linechart);
        item.put("id", "graph_results");
        items.add(item);
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.view_notes_title));
        item.put("image1", R.drawable.notes);
        item.put("id", "view_notes");
        items.add(item);
        
        if(Global.UNLOCK_HIDDEN_FEATURES) {
	        item = new HashMap<String,Object>();
	        item.put("text1", this.getString(R.string.share_title));
	        item.put("image1", R.drawable.share);
	        item.put("id", "share");
	        items.add(item);
        }
        
        return items;
	}
	
	private ArrayList<HashMap<String,Object>> getSettingsItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> item;
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.about_title));
        item.put("id", "about");
        items.add(item);
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.feedback_title));
        item.put("id", "feedback");
        items.add(item);
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.help_title));
        item.put("id", "help");
        items.add(item);
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.settings_title));
        item.put("id", "settings");
        items.add(item);
        
        item = new HashMap<String,Object>();
        item.put("text1", this.getString(R.string.tell_a_friend_title));
        item.put("id", "tell_a_friend");
        items.add(item);
        
        /*item = new HashMap<String,Object>();
        item.put("text1", "Regenerate Data and Close");
        item.put("id", "regenerate_data");
        items.add(item);*/
        
        return items;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String,Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		Adapter adapter = listAdapter.getAdapterForItem(arg2);
		
		if(adapter == rateGroupListAdapter) {
			startGroupFormActivity((Long)data.get("_id"));
		} else {
			String itemId = (String) data.get("id");
			
			if(itemId.equals("graph_results")) {
				Intent i = new Intent(this, GroupResultsActivity.class);
				i.putExtra(GroupResultsActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, 123);
				
			} else if(itemId.equals("view_notes")) {
				Intent i = new Intent(this, NotesListActivity.class);
				i.putExtra(NotesListActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, 123);
				
			} else if(itemId.equals("share")) {
				Intent i = new Intent(this, ShareActivity.class);
				i.putExtra(ShareActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, 123);
				
			} else if(itemId.equals("about")) {
				Intent i = new Intent(this, WebViewActivity.class);
				i.putExtra(WebViewActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				i.putExtra(WebViewActivity.EXTRA_TITLE_ID, R.string.about_title);
				i.putExtra(WebViewActivity.EXTRA_CONTENT_ID, R.string.about_text);
				this.startActivityForResult(i, 123);
				
			} else if(itemId.equals("feedback")) {
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("plain/text");
				i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.feedback_to)});
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
				i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.feedback_content)));
				this.startActivityForResult(Intent.createChooser(i, this.getString(R.string.feedback_title)), 123);
				
			} else if(itemId.equals("help")) {
				Intent i = new Intent(this, HelpActivity.class);
				i.putExtra(HelpActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, 123);
				
			} else if(itemId.equals("settings")) {
				startSettingsActivity();
				return;
				
			} else if(itemId.equals("tell_a_friend")) {
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("text/html");
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.tell_a_friend_subject));
				i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.tell_a_friend_content)));
				this.startActivityForResult(Intent.createChooser(i, this.getString(R.string.tell_a_friend_title)), 123);
			
			} else if(itemId.equals("regenerate_data")) {
				InstallDB.onCreate(dbAdapter, true);
				
				this.finish();
			}
		}
	}
	
	@Override
	protected void onRightButtonPresed() {
		Intent i = new Intent(this, AddEditNoteActivity.class);
		i.putExtra(AddEditNoteActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		this.startActivityForResult(i, NOTE_ACTIVITY);
	}

	private void startGroupFormActivity(long id) {
		Intent i = new Intent(this, RateActivity.class);
		i.putExtra(RateActivity.EXTRA_GROUP_ID, id);
		i.putExtra(RateActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		this.startActivityForResult(i, RATE_ACTIVITY);
	}
}

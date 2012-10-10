/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.t2.vas.MathExtra;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.editor.GroupActivity;
import com.t2.vas.activity.preference.ReminderActivity;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.view.SeparatedListAdapter;

public class MainRateActivity extends ABSNavigationActivity implements OnItemClickListener {


	private SimpleAdapter rateGroupListAdapter;
	private ListView listView;
	private SeparatedListAdapter listAdapter;

	private ArrayList<HashMap<String, Object>> groupsDataList;
	private Context thisContext;
	private long previousRemindTime;
	private long nextRemindTime;

	public static final int RATE_ACTIVITY = 345;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		thisContext = this;
		/*previousRemindTime = ReminderActivity.getPreviousRemindTimeSince(
				thisContext,
				Calendar.getInstance().getTimeInMillis()
				);
		nextRemindTime = ReminderActivity.getNextRemindTimeSince(
				thisContext,
				Calendar.getInstance().getTimeInMillis()
				);
		 */
		// Init today's start and end times.
		Calendar cal = Calendar.getInstance();
		MathExtra.roundTime(cal, Calendar.DAY_OF_MONTH);
		cal.add(Calendar.DAY_OF_MONTH, 1);

		this.setContentView(R.layout.list_layout);
		
		//Show a hint
        Toast.makeText(this, R.string.rate_hint, Toast.LENGTH_LONG).show();
        
		onResume();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

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
		listView = (ListView)this.findViewById(R.id.list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		// can we show the unused categories dialog?
		if(SharedPref.getNotifyGroups(sharedPref)) {
			// only show the dialog once a week.
			Calendar lastShownDialogCal = Calendar.getInstance();
			lastShownDialogCal.add(Calendar.WEEK_OF_MONTH, -1);
			long lastShownDialogTime = lastShownDialogCal.getTimeInMillis();

			// get the last time the dialog was shown.
			long unUsedDialogLastShown = sharedPref.getLong("unUsedDialogLastShown", -1);

			// get the unused groups.
			ArrayList<HashMap<String, Object>> unusedData = getUnusedGroupData();

			// if there are unused categories and its been a while since we've shown the dialog. show it.
			if(unusedData.size() > 0 && unUsedDialogLastShown < lastShownDialogTime) {
				// build the list of titles to show.
				StringBuffer titleSb = new StringBuffer();
				for(int i = 0; i < unusedData.size(); ++i) {
					titleSb.append(unusedData.get(i).get("title"));
					titleSb.append(", ");
				}
				titleSb.deleteCharAt(titleSb.length() - 1);
				titleSb.deleteCharAt(titleSb.length() - 1);

				// build and show the dialog.
				new AlertDialog.Builder(this)
				.setTitle(R.string.unused_groups_title)
				.setMessage(getString(R.string.unused_groups_desc).replace("{0}", titleSb.toString()))
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hideUnusedGroups();
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
				sharedPref.edit().putLong("unUsedDialogLastShown", System.currentTimeMillis()).commit();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private ArrayList<HashMap<String,Object>> getUnusedGroupData() {
		Calendar lastUsedCal = Calendar.getInstance();
		lastUsedCal.add(Calendar.WEEK_OF_MONTH, -1);
		long lastUsedTime = lastUsedCal.getTimeInMillis();

		ArrayList<HashMap<String,Object>> unUsedGroupData = new ArrayList<HashMap<String,Object>>();
		for(int i = 0; i < groupsDataList.size(); ++i) {
			HashMap<String,Object> item = groupsDataList.get(i);
			long latestResultTimestamp = (Long)item.get("latestResultTime");
			if(latestResultTimestamp != -1 && latestResultTimestamp < lastUsedTime) {
				unUsedGroupData.add(item);
			}
		}

		return unUsedGroupData;
	}

	private void hideUnusedGroups() {
		ArrayList<Long> hiddenGroups = SharedPref.getHiddenGroups(sharedPref);
		ArrayList<HashMap<String, Object>> unusedGroupData = getUnusedGroupData();

		for(int i = 0; i < unusedGroupData.size(); ++i) {
			hiddenGroups.add((Long)unusedGroupData.get(i).get("_id"));
		}

		SharedPref.setHiddenGroups(sharedPref, hiddenGroups);

		updateGroupsDataList();
		listAdapter.notifyDataSetChanged();
	}

	private void updateGroupsDataList() {
		groupsDataList.clear();

		List<Long> hiddenGids = SharedPref.getHiddenGroups(sharedPref);

		// get the time range for latest result timestamp.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_MONTH, -2);
		long latestResultRangeStart = cal.getTimeInMillis();
		long latestResultRangeEnd = Calendar.getInstance().getTimeInMillis();

		Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
		while(cursor.moveToNext()) {
			Group group = new Group(dbAdapter);
			group.load(cursor);

			// if this group is hidden, don't work with it.
			if(hiddenGids.contains(group._id)) {
				continue;
			}

			// get the most recent result timestamp.
			Cursor latestResultCursor = group.getResults(latestResultRangeStart, latestResultRangeEnd);
			long lastResultTime = -1;
			if(latestResultCursor.moveToLast()) {
				lastResultTime = latestResultCursor.getLong(latestResultCursor.getColumnIndex(Result.FIELD_TIMESTAMP));
			}
			latestResultCursor.close();

			boolean showWarning = true;
			Calendar todaycal = Calendar.getInstance();
			todaycal.set(Calendar.HOUR_OF_DAY, 0);
			todaycal.set(Calendar.MINUTE, 0);
			todaycal.set(Calendar.SECOND, 0);

			if(lastResultTime != -1)
			{
				if(lastResultTime < todaycal.getTimeInMillis())
					showWarning = true;
				else
					showWarning = false;
			}
			else
				showWarning = true;
			
			// build the data object.
			HashMap<String,Object> data = new HashMap<String,Object>();
			data.put("_id", group._id);
			data.put("title", group.title);
			data.put("showWarning", showWarning);
			data.put("latestResultTime", lastResultTime);

			groupsDataList.add(data);
		}
		cursor.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		updateGroupsDataList();
		listAdapter.notifyDataSetChanged();


	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		@SuppressWarnings("unchecked")
		HashMap<String,Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		Adapter adapter = listAdapter.getAdapterForItem(arg2);

		if(adapter == rateGroupListAdapter) {
			startGroupFormActivity((Long)data.get("_id"));
		} 
	}

	private void startGroupFormActivity(long id) {
		Intent i = new Intent(this, RateActivity.class);
		i.putExtra(RateActivity.EXTRA_GROUP_ID, id);
		this.startActivityForResult(i, RATE_ACTIVITY);
	}
}

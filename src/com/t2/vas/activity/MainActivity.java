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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.t2.vas.ArraysExtra;
import com.t2.vas.Eula;
import com.t2.vas.Global;
import com.t2.vas.MarketPlatform;
import com.t2.vas.MathExtra;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.preference.MainPreferenceActivity;
import com.t2.vas.activity.preference.ReminderActivity;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.BackupRestore;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.view.SeparatedListAdapter;

public class MainActivity extends ABSNavigationActivity implements OnItemClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final int RATE_ACTIVITY = 345;
	public static final int NOTE_ACTIVITY = 355;

	private SimpleAdapter rateGroupListAdapter;
	private ListView listView;
	private SeparatedListAdapter listAdapter;

	private GroupResultsDataProvider groupResultsDataProv;

	private long todayStartTime;
	private long todayEndTime;
	private ArrayList<HashMap<String, Object>> groupsDataList;
	private Context thisContext;
	private long previousRemindTime;
	private long nextRemindTime;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// show the eula if it hasn't been shown
		Eula.show(this);

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

		// can we show the unused categories dialog?
		if(sharedPref.getBoolean("notify_unused_groups", true)) {
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

			// determine if a result was added withing the reminder period
			Cursor resCursor = group.getResults(previousRemindTime, nextRemindTime);
			int resCount = resCursor.getCount();
			resCursor.close();

			// build the data object.
			HashMap<String,Object> data = new HashMap<String,Object>();
			data.put("_id", group._id);
			data.put("title", group.title);
			data.put("showWarning", resCount == 0);
			data.put("latestResultTime", lastResultTime);

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
			if(resultCode == RESULT_OK && isNoteNecessaryForToday()) {
				notifyNoteIsNecessary();
			}
		}
	}

	private void notifyNoteIsNecessary() {
		Toast.makeText(this, R.string.unusual_results_message, Toast.LENGTH_LONG).show();

		//this.getRightButton().startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation));
		//this.getRightButton().setPressed(true);
	}

	private boolean isNoteNecessaryForToday() {
		Cursor cursor;
		Calendar dataSampleStartCal = Calendar.getInstance();
		dataSampleStartCal.setTimeInMillis(todayStartTime);
		dataSampleStartCal.add(Calendar.MONTH, -1);
		long dataSampleStartTime = dataSampleStartCal.getTimeInMillis();

		cursor = new Note(this.dbAdapter).queryForNotes(todayStartTime, todayEndTime, null);
		int notesCount = cursor.getCount();
		cursor.close();


		if(notesCount > 0) {
			return false;
		}

		for(int i = 0; i < groupsDataList.size(); ++i) {
			HashMap<String,Object> data = groupsDataList.get(i);
			long id = (Long)data.get("_id");
			double mostRecentVal = -1;

			Collection<Double> values = groupResultsDataProv.getData(
					id, 
					dataSampleStartTime, 
					todayEndTime
					).values();

			Collection<Double> todayValues = groupResultsDataProv.getData(
					id, 
					todayStartTime, 
					todayEndTime
					).values();

			double[] doubleTodayValues = ArraysExtra.toArray(
					todayValues.toArray(new Double[todayValues.size()])
					);			
			double[] doubleValues = ArraysExtra.toArray(
					values.toArray(new Double[values.size()])
					);

			if(doubleTodayValues.length > 0) {
				mostRecentVal = doubleTodayValues[doubleTodayValues.length-1];
			}

			if(mostRecentVal < 0) {
				continue;
			}
			double stdDev = MathExtra.stdDev(doubleValues);
			double mean = MathExtra.mean(doubleValues);
			double high = mean + stdDev;
			double low = mean - stdDev;

			Log.v(TAG, "title:"+data.get("title"));
			Log.v(TAG, "mrv:"+mostRecentVal);
			Log.v(TAG, "stddev:"+stdDev);
			Log.v(TAG, "mean:"+mean);
			Log.v(TAG, "high:"+high);
			Log.v(TAG, "low:"+low);

			if(mostRecentVal > high || mostRecentVal < low) {
				Log.v(TAG, data.get("title").toString());
				return true;
			}
		}
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

		if(MarketPlatform.isGoogleMarket(this))
			item.put("text1", this.getString(R.string.rate_app_google));
		else
			item.put("text1", this.getString(R.string.rate_app_amazon));

		item.put("id", "rate_app");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.settings_title));
		item.put("id", "settings");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.tell_a_friend_title));
		item.put("id", "tell_a_friend");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", "Backup Database");
		item.put("id", "backup_data");
		items.add(item);

		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		@SuppressWarnings("unchecked")
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

			} else if(itemId.equals("tell_a_friend")) {
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("text/html");
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.tell_a_friend_subject));
				if(MarketPlatform.isGoogleMarket(this))
					i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.tell_a_friend_content_google)));
				else
					i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.tell_a_friend_content_amazon)));
				this.startActivityForResult(Intent.createChooser(i, this.getString(R.string.tell_a_friend_title)), 123);

			} else if(itemId.equals("rate_app")) {
				if(MarketPlatform.isGoogleMarket(this))
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("market://details?id="+this.getPackageName()));
					this.startActivityForResult(i, 123);
				}
				else
				{
					//http://www.amazon.com/gp/mas/dl/android?p=[packagename]
					Intent amazon = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.t2.vas"));
					amazon.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.startActivity(amazon);
				}
			}
			else if(itemId.equals("backup_data")) 
			{
				BackupRestore.backupDb(this);
			}
		}
	}

	@Override
	protected void onRightButtonPressed() {
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

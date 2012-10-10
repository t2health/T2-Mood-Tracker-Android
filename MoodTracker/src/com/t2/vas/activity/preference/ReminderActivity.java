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
package com.t2.vas.activity.preference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.t2.vas.Analytics;
import com.t2.vas.Global;
import com.t2.vas.NotificationService;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.TimePref;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.view.SeparatedListAdapter;

public class ReminderActivity extends ABSNavigationActivity implements OnItemClickListener {
	private SimpleAdapter dowAdapter;
	private ArrayList<HashMap<String, Object>> daysData;
	private List<Integer> enabledDays;
	private SeparatedListAdapter listAdapter;
	private SimpleAdapter timesAdapter;
	private List<TimePref> times;
	private ArrayList<HashMap<String, Object>> timesData = new ArrayList<HashMap<String,Object>>();
	private SimpleDateFormat dateFormatter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dateFormatter = new SimpleDateFormat(Global.REMINDER_TIME_FORMAT);

		times = SharedPref.getReminderTimes(sharedPref);

		enabledDays = SharedPref.getReminderEnabledDays(sharedPref);
		daysData = loadDaysOfWeek();

		loadTimesData();

		timesAdapter = new SimpleAdapter(
				this, 
				timesData, 
				R.layout.list_item_1_toggle, 
				new String[] {
						"time",
						"id",
				},
				new int[] {
						R.id.text1,
						R.id.toggleButton,
				}
				);
		timesAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == R.id.text1) {
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis((Long)data);
					((TextView)view).setText(dateFormatter.format(cal.getTime()));
					return true;

				} else if(view.getId() == R.id.toggleButton) {
					ToggleButton tb = (ToggleButton)view;
					final TimePref tp = times.get((Integer)data);

					tb.setFocusable(false);
					tb.setOnCheckedChangeListener(null);
					tb.setChecked(tp.enabled);
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							onTimeEnabledChange(tp, isChecked);
						}
					});

					return true;
				}
				return false;
			}
		});

		dowAdapter = new SimpleAdapter(
				this, 
				daysData, 
				R.layout.list_item_1_toggle, 
				new String[] {
						"title",
						"dow",
				},
				new int[] {
						R.id.text1,
						R.id.toggleButton,
				}
				);
		dowAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == R.id.toggleButton) {
					final int dow = (Integer)data;
					ToggleButton tb = (ToggleButton)view;

					tb.setFocusable(false);
					tb.setOnCheckedChangeListener(null);
					tb.setChecked(enabledDays.contains(dow));
					tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							onDayEnabledChange(dow, isChecked);
						}
					});

					return true;
				}
				return false;
			}
		});

		listAdapter = new SeparatedListAdapter(this);
		listAdapter.addSection(getString(R.string.times_list), timesAdapter);
		listAdapter.addSection(getString(R.string.days_list), dowAdapter, false);

		setContentView(R.layout.list_layout);
		ListView listView = (ListView)this.findViewById(R.id.list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
	}



	@Override
	protected void onBackButtonPressed() {
		super.onBackButtonPressed();

	}

	private ArrayList<HashMap<String,Object>> loadDaysOfWeek() {
		String[] dowNames = this.getResources().getStringArray(R.array.days_of_the_week);
		int[] dowValues = this.getResources().getIntArray(R.array.days_of_the_week_values);
		ArrayList<HashMap<String,Object>> output = new ArrayList<HashMap<String,Object>>();

		for(int i = 0; i < dowValues.length; ++i) {
			HashMap<String,Object> data = new HashMap<String,Object>();
			data.put("title", dowNames[i]);
			data.put("dow", dowValues[i]);
			output.add(data);
		}

		return output;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Adapter adapter = listAdapter.getAdapterForItem(arg2);
		if(adapter == timesAdapter) {
			final TimePref tp = times.get(arg2-1);
			final Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(tp.time);

			new TimePickerDialog(
					this,
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
							cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
							cal.set(Calendar.MINUTE, minute);
							tp.time = cal.getTimeInMillis();
							SharedPref.setReminderTimes(sharedPref, times);
							loadTimesData();
							listAdapter.notifyDataSetChanged();
							setAlarms();
						}
					},
					cal.get(Calendar.HOUR_OF_DAY),
					cal.get(Calendar.MINUTE),
					false
					).show();
		}
	}

	private void loadTimesData() {
		timesData.clear();
		for(int i = 0; i < times.size(); ++i) {
			TimePref tf = times.get(i);
			HashMap<String,Object> item = new HashMap<String,Object>();
			item.put("id", i);
			item.put("time", tf.time);
			item.put("enabled", tf.enabled);
			item.put("object", tf);
			timesData.add(item);

		}
	}

	private void onTimeEnabledChange(TimePref tp, boolean isChecked) {
		tp.enabled = isChecked;

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tp.time);
		Analytics.onEvent(this, "Reminder Time," + dateFormatter.format(cal.getTime()) + "," + (isChecked ? "Enabled" : "Disabled"));

		SharedPref.setReminderTimes(sharedPref, times);
		setAlarms();
	}

	private void onDayEnabledChange(int dayOfWeek, boolean isChecked) {
		enabledDays.remove((Object)dayOfWeek);

		if(isChecked) {
			enabledDays.add(dayOfWeek);
		}

		String[] dowNames = this.getResources().getStringArray(R.array.days_of_the_week);
		Analytics.onEvent(this, "Reminder Time," + dowNames[dayOfWeek-1] + "," + (isChecked ? "Enabled" : "Disabled"));

		SharedPref.setReminderEnabledDays(sharedPref, enabledDays);

		setAlarms();
	}

	public int lookupNextAlarmDay(int dow, ArrayList<Integer> daysEnabled)
	{
		Collections.sort(daysEnabled);
		int nextDay = -1;
		for(int i=0; i<daysEnabled.size(); i++)
		{
			if(daysEnabled.get(i) >= dow)
			{
				nextDay = daysEnabled.get(i);
				break;
			}
		}
		if(nextDay == -1)
			nextDay = daysEnabled.get(0);

		return nextDay;
	}

	public long getNextAlarmTime()
	{
		ArrayList<Integer> daysEnabled = SharedPref.getReminderEnabledDays(sharedPref);
		ArrayList<TimePref> timesEnabled = SharedPref.getReminderTimes(sharedPref);

		Calendar outcal = Calendar.getInstance();
		Calendar ccal = Calendar.getInstance();
		int hod = ccal.get(Calendar.HOUR_OF_DAY);
		int mod = ccal.get(Calendar.MINUTE);

		Calendar tcal = Calendar.getInstance();
		boolean alarmTimeSet = false;
		long lstamp = Long.MAX_VALUE;
		for(int i=0; i<timesEnabled.size(); i++)
		{
			if(timesEnabled.get(i).enabled)
			{
				tcal.setTimeInMillis(timesEnabled.get(i).time);
				int th = tcal.get(Calendar.HOUR_OF_DAY);
				int tm = tcal.get(Calendar.MINUTE);
				long tstamp = ((th*60*60*1000) + (tm*60*1000));

				//Log.v("enabled", ""+th);
				//Log.v("currenth", ""+hod);

				int targetDOW = lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled);

				if(targetDOW == ccal.get(Calendar.DAY_OF_WEEK))
				{
					if( ( (th==hod) && (tm>mod)) || ((th>hod) ) )
					{ 

						if(tstamp < lstamp)
						{
							lstamp = tstamp;
							outcal.set(Calendar.HOUR_OF_DAY, th);
							outcal.set(Calendar.MINUTE, tm);
							outcal.set(Calendar.SECOND, 0);


							int diff = targetDOW - ccal.get(Calendar.DAY_OF_WEEK);
							int doy = outcal.get(Calendar.DAY_OF_YEAR);
							doy += diff;

							//Log.v("today", ""+ccal.get(Calendar.DAY_OF_WEEK));
							//Log.v("target", ""+targetDOW);

							outcal.set(Calendar.DAY_OF_YEAR, doy);
							alarmTimeSet = true;
							//break;
						}
					}
				}
				else
				{
					alarmTimeSet = false;

				}
			}
		}
		if(alarmTimeSet == false)
		{
			//Find earliest time index
			int index = -1;
			long lastStamp = Long.MAX_VALUE;
			int lastMinute = 61;
			for(int i=0; i<timesEnabled.size(); i++)
			{
				if(timesEnabled.get(i).enabled)
				{
					Calendar fcal = Calendar.getInstance();
					fcal.setTimeInMillis(timesEnabled.get(i).time);
					int fhod = fcal.get(Calendar.HOUR_OF_DAY);
					int fmod = fcal.get(Calendar.MINUTE);
					long tstamp = ((fhod*60*60*1000) + (fmod*60*1000));
					if(tstamp < lastStamp)
					{
						lastStamp = tstamp;
						index = i;
					}
				}
			}

			tcal.setTimeInMillis(timesEnabled.get(index).time);



			int th = tcal.get(Calendar.HOUR_OF_DAY);
			int tm = tcal.get(Calendar.MINUTE);

			outcal.set(Calendar.HOUR_OF_DAY, th);
			outcal.set(Calendar.MINUTE, tm);
			outcal.set(Calendar.SECOND, 0);
			ccal.add(Calendar.DAY_OF_YEAR, 1);
			if(lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled) < ccal.get(Calendar.DAY_OF_WEEK))
				outcal.add(Calendar.DAY_OF_YEAR, 7);


			outcal.set(Calendar.DAY_OF_WEEK, lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled));

		}

		return outcal.getTimeInMillis();
	}

	public void setAlarms()
	{

		try
		{
			final AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			final Intent intent = new Intent(this,NotificationService.class);
			final PendingIntent pend = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			// Schedule an alarm
			long alarmTime = getNextAlarmTime();
			mgr.set(AlarmManager.RTC_WAKEUP, alarmTime, pend);

			Calendar lcal = Calendar.getInstance();
			lcal.setTimeInMillis(alarmTime);

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
			Log.v("MT3 Alarm Set1:", sdf.format(lcal.getTime()));
			Toast.makeText(this, "Next alarm at: " + sdf.format(lcal.getTime()), Toast.LENGTH_SHORT).show();
		}
		catch(Exception ex){
			//No alarms enabled
		}
	}

	/*public static ReminderData getReminderData(Context context) {
		// initialize the high level variables.
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		DBAdapter dbAdapter = new DBAdapter(context, Global.Database.name, Global.Database.version);
        dbAdapter.open();

        // Get the days and times enabled.
		ArrayList<Integer> daysEnabled = SharedPref.getReminderEnabledDays(sharedPref);
		ArrayList<TimePref> timesEnabled = SharedPref.getReminderTimes(sharedPref);
		for(int i = timesEnabled.size() -1; i >= 0; --i) {
			if(!timesEnabled.get(i).enabled) {
				timesEnabled.remove(i);
			}
		}

		// Get the visible groups.
		ArrayList<Long> hiddenGroups = SharedPref.getHiddenGroups(sharedPref);
		ArrayList<Group> visibleGroups = new Group(dbAdapter).getGroups();
		for(int i = visibleGroups.size() -1; i >= 0; --i) {
			if(hiddenGroups.contains(visibleGroups.get(i)._id)) {
				visibleGroups.remove(i);
			}
		}

		dbAdapter.close();

		// Return the data.
		ReminderData data = new ReminderData();
		data.daysEnabled = daysEnabled;
		data.timesEnabled = timesEnabled;
		data.visibleGroups = visibleGroups;
		return data;
	}*/

	/*public static long getNextRemindTime(Context context) {
		return getNextRemindTimeSince(context, Calendar.getInstance().getTimeInMillis());
	}
	 */
	/*public static long getNextRemindTimeSince(Context context, long timestampIn) {
		long[] times = getRemindTimesSince(context, timestampIn);
		if(times.length == 0) {
			return -1;
		}

		return times[0];
	}

	public static long getPreviousRemindTimeSince(Context context, long timestampIn) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampIn);
		cal.add(Calendar.WEEK_OF_YEAR, -1);

		long[] times = getRemindTimesSince(context, cal.getTimeInMillis());
		if(times.length == 0) {
			return -1;
		}

		long prevRemindTime = -1;
		for(int i = 0; i < times.length; ++i) {
			long time = times[i];
			if(time < timestampIn) {
				prevRemindTime = time;
			} else {
				break;
			}
		}

		return prevRemindTime;
	}*/

	/*public static long[] getRemindTimesSince(Context context, long timestampIn) {
		// Initialize
		ArrayList<Long> remindTimestamps = new ArrayList<Long>();
		ReminderData data = getReminderData(context);
		ArrayList<Integer> daysEnabled = data.daysEnabled;
		ArrayList<TimePref> timesEnabled = data.timesEnabled;
		ArrayList<Group> visibleGroups = data.visibleGroups;

		// Check to see if there is any data that would 
		if(visibleGroups.size() == 0 || timesEnabled.size() == 0 || daysEnabled.size() == 0) {
			return new long[0];
		}

		// determine the possible reminder times.
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestampIn);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long currentTime = cal.getTimeInMillis();
		int currentDow = cal.get(Calendar.DAY_OF_WEEK);
		int numDaysInWeek = cal.getActualMaximum(Calendar.DAY_OF_WEEK);
		// for each day of the week that is enabled.
		for(int i = 0; i < daysEnabled.size(); ++i) {
			int dow = daysEnabled.get(i);
			cal.setTimeInMillis(currentTime);
			cal.set(Calendar.DAY_OF_WEEK, dow);
			// if the dow is less than today, then add a week to it.
			if(dow < currentDow) {
				cal.add(Calendar.DAY_OF_MONTH, numDaysInWeek);
			}

			// for each time that is enabled.
			for(int j = 0; j < timesEnabled.size(); ++j) {
				long timestamp = timesEnabled.get(j).time;
				Calendar timeCal = Calendar.getInstance();
				timeCal.setTimeInMillis(timestamp);

				cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
				long listtimestamp = cal.getTimeInMillis();
				if(listtimestamp > timestampIn) {
					remindTimestamps.add(cal.getTimeInMillis());
				}
			}
		}

		// convert to primitive array, sort and return.
		long[] times = new long[remindTimestamps.size()];
		for(int i = 0; i < times.length; ++i) {
			times[i] = remindTimestamps.get(i);
		}
		Arrays.sort(times);
		return times;
	}*/
}

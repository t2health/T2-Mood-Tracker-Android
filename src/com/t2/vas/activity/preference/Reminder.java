package com.t2.vas.activity.preference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
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
import android.widget.ToggleButton;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.SharedPref;
import com.t2.vas.TimePref;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSNavigation;
import com.t2.vas.view.SeparatedListAdapter;

public class Reminder extends ABSNavigation implements OnItemClickListener {
	private static final String TAG = Reminder.class.getSimpleName();
	
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
        if(times.size() == 0) {
			Calendar cal = Calendar.getInstance();

			cal.set(0, 0, 0, 8, 0);
			times.add(new TimePref(cal.getTimeInMillis(), false));
			
			cal.set(0, 0, 0, 12, 0);
			times.add(new TimePref(cal.getTimeInMillis(), false));
			
			cal.set(0, 0, 0, 16, 0);
			times.add(new TimePref(cal.getTimeInMillis(), false));
		}
        
        enabledDays = SharedPref.getReminderEnabledDays(sharedPref);
        daysData = loadDaysOfWeek();
        Log.v(TAG, "ED:"+enabledDays.toString());
        
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
        listAdapter.addSection("Times", timesAdapter);
        listAdapter.addSection("Days", dowAdapter, false);
        
        setContentView(R.layout.list_layout);
        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
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
 		SharedPref.setReminderTimes(sharedPref, times);
	}
	
	private void onDayEnabledChange(int dayOfWeek, boolean isChecked) {
		enabledDays.remove((Object)dayOfWeek);
		
		if(isChecked) {
			enabledDays.add(dayOfWeek);
		}
		
		SharedPref.setReminderEnabledDays(sharedPref, enabledDays);
	}
}

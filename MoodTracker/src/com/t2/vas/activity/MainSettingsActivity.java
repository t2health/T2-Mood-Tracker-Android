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
import java.util.Map;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.t2.vas.Analytics;
import com.t2.vas.NotificationService;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.activity.editor.GroupListActivity;
import com.t2.vas.activity.preference.ClearDataActivity;
import com.t2.vas.activity.preference.ReminderActivity;
import com.t2.vas.activity.preference.SecurityActivity;
import com.t2.vas.view.SeparatedListAdapter;

public class MainSettingsActivity extends ABSNavigationActivity implements
        OnItemClickListener, OnCheckedChangeListener {
    public static final String TAG = MainSettingsActivity.class.getSimpleName();

    private ListView listView;
    private SeparatedListAdapter listAdapter;
    private ItemsAdapter generalAdapter;
	static final int TIME_DIALOG_ID = 1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_layout);

        listView = (ListView) this.findViewById(R.id.list);

        ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> item;

        item = new HashMap<String, Object>();
        item.put("id", "edit_category");
        item.put("title", getString(R.string.group_list_title));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "reminders");
        item.put("title", getString(R.string.reminders_title));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "notify");
        item.put("title", getString(R.string.notify_unused_groups));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "tips");
        item.put("title", getString(R.string.show_tips_on_startup));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "anondata");
        item.put("title", getString(R.string.send_anon_data_title));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "security");
        item.put("title", getString(R.string.security_title));
        items.add(item);

        item = new HashMap<String, Object>();
        item.put("id", "clear_data");
        item.put("title", getString(R.string.clear_data_title));
        items.add(item);

        if (Analytics.isEnrolled(this)) {
            item = new HashMap<String, Object>();
            item.put("id", "remove_study");
            item.put("title", "Disenroll From Study");
            items.add(item);
        }

        generalAdapter = new ItemsAdapter(this, items, R.layout.list_item_1,
                new String[] {
                        "title",
                }, new int[] {
                        R.id.text1,
                });

        listAdapter = new SeparatedListAdapter(this);
        listAdapter.addSection(getString(R.string.general_group_operations), generalAdapter);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Adapter adapter = listAdapter.getAdapterForItem(arg2);

        boolean isGeneralAdapter = adapter == generalAdapter;

        if (isGeneralAdapter) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            String itemId = (String) data.get("id");

            if (itemId.equals("edit_category"))
            {
                Intent i = new Intent(this, GroupListActivity.class);
                this.startActivity(i);
                return;
            }
            else if (itemId.equals("reminders"))
            {
            	 Intent i = new Intent(this, ReminderActivity.class);
                 this.startActivity(i);
            	//showDialog(TIME_DIALOG_ID);
                 return;
            }
            else if (itemId.equals("notify"))
            {
                CheckedTextView ctv = (CheckedTextView) arg1.findViewById(R.id.text1);
                if (ctv.isChecked())
                {
                    SharedPref.setNotifyGroups(sharedPref, false);
                    ctv.setChecked(false);
                }
                else
                {
                    SharedPref.setNotifyGroups(sharedPref, true);
                    ctv.setChecked(true);
                }
                return;
            }
            else if (itemId.equals("tips"))
            {
                CheckedTextView ctv = (CheckedTextView) arg1.findViewById(R.id.text1);
                if (ctv.isChecked())
                {
                    SharedPref.setShowStartupTips(sharedPref, false);
                    ctv.setChecked(false);
                }
                else
                {
                    SharedPref.setShowStartupTips(sharedPref, true);
                    ctv.setChecked(true);
                }
                return;
            }
            else if (itemId.equals("anondata"))
            {
                CheckedTextView ctv = (CheckedTextView) arg1.findViewById(R.id.text1);
                if (ctv.isChecked())
                {
                    SharedPref.setSendAnnonData(sharedPref, false);
                    ctv.setChecked(false);
                }
                else
                {
                    SharedPref.setSendAnnonData(sharedPref, true);
                    ctv.setChecked(true);
                }
                return;
            }
            else if (itemId.equals("security"))
            {
                Intent i = new Intent(this, SecurityActivity.class);
                this.startActivity(i);
                return;
            }
            else if (itemId.equals("clear_data"))
            {
                Intent i = new Intent(this, ClearDataActivity.class);
                this.startActivity(i);
                return;
            }
            else if (itemId.equals("inverseData"))
            {
                // CheckedTextView ctv =
                // (CheckedTextView)arg1.findViewById(R.id.text1);
                return;
            }
            else if (itemId.equals("remove_study")) {
                sharedPref.edit().remove(getString(R.string.prf_study_participant_number))
                        .remove(getString(R.string.prf_study_recipient_email)).commit();
                finish();
                Toast.makeText(this, "Study enrollment removed.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

    }

    private class ItemsAdapter extends SimpleAdapter {

        private LayoutInflater layoutInflater;
        private int defaultLayout;

        public ItemsAdapter(Context context,
                List<? extends Map<String, ?>> data, int resource,
                String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.defaultLayout = resource;
            this.layoutInflater = (LayoutInflater) context
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> item = (HashMap<String, Object>) this.getItem(position);

            View newView = convertView;
            if (item.get("id").equals("notify")) {
                newView = layoutInflater.inflate(R.layout.list_item_1_checked, null);
                ((CheckedTextView) newView.findViewById(R.id.text1)).setChecked(SharedPref
                        .getNotifyGroups(sharedPref));

            } else if (item.get("id").equals("tips")) {
                newView = layoutInflater.inflate(R.layout.list_item_1_checked, null);
                ((CheckedTextView) newView.findViewById(R.id.text1)).setChecked(SharedPref
                        .getShowStartupTips(sharedPref));

            } else if (item.get("id").equals("anondata")) {
                newView = layoutInflater.inflate(R.layout.list_item_1_checked, null);
                ((CheckedTextView) newView.findViewById(R.id.text1)).setChecked(SharedPref
                        .getSendAnnonData(sharedPref));
            } else {
                newView = layoutInflater.inflate(defaultLayout, null);
            }

            ((TextView) newView.findViewById(R.id.text1)).setText(item.get("title") + "");

            return newView;
        }
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this,
					mTimeSetListener,
					SharedPref.getNotifyHour(sharedPref), SharedPref.getNotifyMinute(sharedPref), false);
		}
		return null;
	}
    
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
			new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			SharedPref.setNotifyHour(sharedPref, hourOfDay);
			SharedPref.setNotifyMinute(sharedPref, minute);

			Calendar nc = Calendar.getInstance();
			nc.set(Calendar.HOUR_OF_DAY, SharedPref.getNotifyHour(sharedPref));
			nc.set(Calendar.MINUTE, SharedPref.getNotifyMinute(sharedPref));
			nc.set(Calendar.SECOND, 0);

			// Schedule an alarm
			final AlarmManager mgr = (AlarmManager)MainSettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
			final Intent intent = new Intent(MainSettingsActivity.this,NotificationService.class);
			final PendingIntent pend = PendingIntent.getService(MainSettingsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mgr.setRepeating(AlarmManager.RTC_WAKEUP, nc.getTimeInMillis(), (1000*60*60*24*7), pend);
		}
	};
}

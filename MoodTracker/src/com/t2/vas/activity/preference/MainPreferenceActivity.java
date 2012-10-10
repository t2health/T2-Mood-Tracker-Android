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

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ExportActivity;
import com.t2.vas.activity.ImportActivity;
import com.t2.vas.activity.editor.GroupListActivity;

public class MainPreferenceActivity extends ABSPreferenceNavigation implements OnPreferenceClickListener, OnPreferenceChangeListener {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preference_activity);

        PreferenceScreen screen = this.getPreferenceScreen();
        screen.findPreference("group_editor").setOnPreferenceClickListener(this);
        screen.findPreference("reminders").setOnPreferenceClickListener(this);
        screen.findPreference("clear_data").setOnPreferenceClickListener(this);
        screen.findPreference("security").setOnPreferenceClickListener(this);
    	//screen.findPreference("import").setOnPreferenceClickListener(this);
        //screen.findPreference("export").setOnPreferenceClickListener(this);

        screen.findPreference("send_anon_data").setOnPreferenceChangeListener(this);
    }

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String prefKey = preference.getKey();

		if(prefKey.equals("group_editor")) {
			Intent i = new Intent(this, GroupListActivity.class);
			i.putExtra(GroupListActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		} else if(prefKey.equals("reminders")) {
			Intent i = new Intent(this, ReminderActivity.class);
			i.putExtra(ReminderActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		} else if(prefKey.equals("clear_data")) {
			Intent i = new Intent(this, ClearDataActivity.class);
			i.putExtra(ClearDataActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		} else if(prefKey.equals("security")) {
			Intent i = new Intent(this, SecurityActivity.class);
			//i.putExtra(SecurityActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		} else if(prefKey.equals("import")) {
			Intent i = new Intent(this, ImportActivity.class);
			//i.putExtra(SecurityActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		} else if(prefKey.equals("export")) {
			Intent i = new Intent(this, ExportActivity.class);
			//i.putExtra(SecurityActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;

		}

		return false;
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals("send_anon_data")) {
			Boolean isChecked = (Boolean)newValue;
			if(isChecked) {
				VASAnalytics.setEnabled(true);
				VASAnalytics.onEvent(this, VASAnalytics.EVENT_SETTING_ANALYTICS_ENABLED);
			} else {
				VASAnalytics.onEvent(this, VASAnalytics.EVENT_SETTING_ANALYTICS_DISABLED);
				VASAnalytics.setEnabled(false);
			}
		}

		return true;
	}
}

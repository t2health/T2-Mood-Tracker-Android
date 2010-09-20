package com.t2.vas.activity.preference;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.PasswordActivity;
import com.t2.vas.activity.editor.GroupListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class Reminder extends PreferenceActivity implements OnPreferenceChangeListener {
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VASAnalytics.onEvent(VASAnalytics.EVENT_REMINDER_ACTIVITY);
        addPreferencesFromResource(R.xml.reminder_preference);
        
        PreferenceScreen screen = this.getPreferenceScreen();
        
        screen.findPreference("reminders_enabled").setOnPreferenceChangeListener(this);
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference.getKey().equals("reminders_enabled")) {
			Boolean isChecked = (Boolean)newValue;
			if(isChecked) {
				ReminderService.startRunning(this, false);
			} else {
				ReminderService.stopRunning(this);
			}
		}
		return true;
	}
}

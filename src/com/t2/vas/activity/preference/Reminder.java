package com.t2.vas.activity.preference;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.VASAnalytics;

public class Reminder extends CustomTitle implements OnPreferenceChangeListener {
	public void onCreate(Bundle savedInstanceState) {
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

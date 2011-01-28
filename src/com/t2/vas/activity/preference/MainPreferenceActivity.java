package com.t2.vas.activity.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.editor.GroupListActivity;

public class MainPreferenceActivity extends ABSPreferenceNavigation implements OnPreferenceClickListener, OnPreferenceChangeListener {
	private static final String TAG = MainPreferenceActivity.class.getName();
	private SharedPreferences sharedPref;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preference_activity);

        PreferenceScreen screen = this.getPreferenceScreen();
        screen.findPreference("group_editor").setOnPreferenceClickListener(this);
        screen.findPreference("reminders").setOnPreferenceClickListener(this);
        screen.findPreference("clear_data").setOnPreferenceClickListener(this);
        screen.findPreference("security").setOnPreferenceClickListener(this);
        
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
			i.putExtra(SecurityActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;
		}

		return false;
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String current_password = sharedPref.getString("notes_password", null);

		if(preference.getKey().equals("send_anon_data")) {
			Boolean isChecked = (Boolean)newValue;
			if(isChecked) {
				VASAnalytics.setEnabled(true);
				VASAnalytics.onEvent(VASAnalytics.EVENT_SETTING_ANALYTICS_ENABLED);
			} else {
				VASAnalytics.onEvent(VASAnalytics.EVENT_SETTING_ANALYTICS_DISABLED);
				VASAnalytics.setEnabled(false);
			}
		}

		return true;
	}
}

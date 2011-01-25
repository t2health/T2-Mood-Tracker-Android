package com.t2.vas.activity.preference;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.PasswordActivity;
import com.t2.vas.activity.editor.GroupListActivity;

public class MainPreferenceActivity extends ABSPreferenceNavigation implements OnPreferenceClickListener, OnPreferenceChangeListener {
	private static final String TAG = MainPreferenceActivity.class.getName();
	private static final int REQUEST_PASSWORD_SET = 34;
	private static final int REQUEST_PASSWORD_UNSET = 35;
	private static final int REQUEST_PASSWORD_UPDATE = 36;

	private SharedPreferences sharedPref;
	private Toast notesLockedToast;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VASAnalytics.onEvent(VASAnalytics.EVENT_SETTINGS_ACTIVITY);

        addPreferencesFromResource(R.xml.main_preference_activity);

        PreferenceScreen screen = this.getPreferenceScreen();
        screen.findPreference("group_editor").setOnPreferenceClickListener(this);
        screen.findPreference("reminders").setOnPreferenceClickListener(this);
        screen.findPreference("clear_data").setOnPreferenceClickListener(this);
        screen.findPreference("security").setOnPreferenceClickListener(this);
        
        screen.findPreference("send_anon_data").setOnPreferenceChangeListener(this);
    }


	private void setPassword(String password) {
		if(password == null || password.trim().length() <= 0) {
			sharedPref.edit().remove("notes_password").commit();
		} else {
			sharedPref.edit().putString("notes_password", password).commit();
		}
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
			Intent i = new Intent(this, Reminder.class);
			i.putExtra(Reminder.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;
			
		} else if(prefKey.equals("clear_data")) {
			Intent i = new Intent(this, ClearData.class);
			i.putExtra(ClearData.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;
			
		} else if(prefKey.equals("security")) {
			Intent i = new Intent(this, Security.class);
			i.putExtra(Security.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivity(i);
			return true;
		}

		return false;
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String current_password = sharedPref.getString("notes_password", null);
//		Log.v(TAG, "Current password:"+current_password);

		if(preference.getKey().equals("password_protect_notes")) {
			Boolean isChecked = (Boolean)newValue;

			// Enabling password protection
			if(isChecked) {
				Log.v(TAG, "Checked, set a new password.");

				// If password is not set, set one.
				Intent i = new Intent(this, PasswordActivity.class);
				i.putExtra("mode", PasswordActivity.MODE_SET);
				i.putExtra("current_password", current_password);
				this.startActivityForResult(i, REQUEST_PASSWORD_SET);
				return false;

			// Disabling password protection.
			} else {

				// Currently password protecting. prompt for current password.
				if(current_password != null) {
					Intent i = new Intent(this, PasswordActivity.class);
					i.putExtra("mode", PasswordActivity.MODE_UNLOCK);
					i.putExtra("current_password", current_password);
					this.startActivityForResult(i, REQUEST_PASSWORD_UNSET);
					return false;
				}
			}
			
		// Enable/disable analytics.
		} else if(preference.getKey().equals("send_anon_data")) {
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

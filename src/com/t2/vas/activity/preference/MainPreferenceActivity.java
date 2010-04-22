package com.t2.vas.activity.preference;

import com.t2.vas.Global;
import com.t2.vas.R;
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
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;

public class MainPreferenceActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	private static final String TAG = MainPreferenceActivity.class.getName();
	private static final int REQUEST_PASSWORD_SET = 34;
	private static final int REQUEST_PASSWORD_UNSET = 35;
	private static final int REQUEST_PASSWORD_UPDATE = 36;
	
	private SharedPreferences sharedPref;
	private CheckBoxPreference passwordProtectNotesPref;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.sharedPref = this.getSharedPreferences(Global.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.getPreferenceManager().setSharedPreferencesName(Global.SHARED_PREFERENCES_NAME);
        
        addPreferencesFromResource(R.xml.main_preference_activity);
        
        PreferenceScreen screen = this.getPreferenceScreen();
        passwordProtectNotesPref = (CheckBoxPreference)screen.findPreference("password_protect_notes");
        passwordProtectNotesPref.setOnPreferenceClickListener(this);
        passwordProtectNotesPref.setOnPreferenceChangeListener(this);
        
        screen.findPreference("group_editor").setOnPreferenceClickListener(this);
        screen.findPreference("change_password").setOnPreferenceClickListener(this);
    }

	
	private void setPassword(String password) {
		if(password == null || password.trim().length() <= 0) {
			sharedPref.edit().remove("notes_password").commit();
		} else {
			sharedPref.edit().putString("notes_password", password).commit();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
			case REQUEST_PASSWORD_SET:
				if(resultCode == Activity.RESULT_OK && data != null) {
					String newPassword = data.getStringExtra("new_password");
					this.setPassword(newPassword);
					this.passwordProtectNotesPref.setChecked(true);
					Toast.makeText(this, "The password has been set.", 3000).show();
					
				} else {
					this.passwordProtectNotesPref.setChecked(false);
					Toast.makeText(this, "The password was not set.", 3000).show();
				}
				break;
				
			case REQUEST_PASSWORD_UNSET:
				if(resultCode == Activity.RESULT_OK) {
					this.passwordProtectNotesPref.setChecked(false);
					Toast.makeText(this, "The password has been removed.", 3000).show();
					
				} else {
					Toast.makeText(this, "The password was not removed.", 3000).show();
					this.passwordProtectNotesPref.setChecked(true);
					
				}
				break;
				
			case REQUEST_PASSWORD_UPDATE:
				if(resultCode == Activity.RESULT_OK && data != null) {
					String newPassword = data.getStringExtra("new_password");
					this.setPassword(newPassword);
					this.passwordProtectNotesPref.setChecked(true);
					Toast.makeText(this, "The password has been changed.", 3000).show();
					
				} else {
					Toast.makeText(this, "The password was not changed.", 3000).show();
				}
				break;
		}
	}



	@Override
	public boolean onPreferenceClick(Preference preference) {
		String current_password = sharedPref.getString("notes_password", null);
		Log.v(TAG, "Current password:"+current_password);
		
		if(preference.getKey().equals("group_editor")) {
			Intent i = new Intent(this, GroupListActivity.class);
			this.startActivity(i);
			return true;
			
			
		} else if(preference.getKey().equals("change_password")) {
			Intent i = new Intent(this, PasswordActivity.class);
			i.putExtra("mode", PasswordActivity.MODE_UPDATE);
			i.putExtra("current_password", current_password);
			this.startActivityForResult(i, REQUEST_PASSWORD_UPDATE);
			return true;
		}
		
		
		return false;
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String current_password = sharedPref.getString("notes_password", null);
		Log.v(TAG, "Current password:"+current_password);
		
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
		}
		
		return false;
	}
}

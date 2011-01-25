package com.t2.vas.activity.preference;

import android.content.Intent;

import com.t2.vas.AppSecurityManager;

public class ABSSecurityPreference extends ABSPreferenceActivity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		AppSecurityManager.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		AppSecurityManager.getInstance().onWindowFocusChanged(hasFocus);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppSecurityManager.getInstance().onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		AppSecurityManager.getInstance().onPause(this);
	}
}

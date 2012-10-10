/*
 * 
 */
package com.t2.vas.activity;

import android.content.Intent;

import com.t2.vas.AppSecurityManager;
import com.t2.vas.SharedPref;

public abstract class ABSSecurityActivity extends ABSTabActivity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		AppSecurityManager.getInstance().onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		//AppSecurityManager.getInstance().onWindowFocusChanged(hasFocus);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		
		//Check if this is the tab activity so that security screen doesn't open twice (one for tabactivity, and one for child activity)
		//if (this.getParent() instanceof MainTabActivity)
		//AppSecurityManager.getInstance().onResume(this, SharedPref.Security.isEnabled(sharedPref));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		
		//if (this.getParent() instanceof MainTabActivity)
		//AppSecurityManager.getInstance().onPause(this, SharedPref.Security.isEnabled(sharedPref));
	}
}

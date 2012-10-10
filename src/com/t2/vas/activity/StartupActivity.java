/*
 * 
 */
package com.t2.vas.activity;

import android.content.Intent;
import android.os.Bundle;
import com.t2.vas.DBInstallData;
import com.t2.vas.MarketPlatform;
import com.t2.vas.SharedPref;

public class StartupActivity extends ABSActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		// update the database if the original update didn't take.
    	// NOt sure why this happens, but this is a hack to fix the problem.
		DBInstallData.forceInstallDatabase(this);
		
		Intent i;
		// show the main activity, unlock screen will appear if necessary.
		if(SharedPref.Security.isEnabled(sharedPref)) {
			i = new Intent(this, MainTabActivity.class);
		
		// show the plain splash page.
		} else {
			i = new Intent(this, SplashScreenActivity.class);
		}
		
		MarketPlatform.printHashcode(this);
		
		this.startActivity(i);
		this.finish();
	}
}

package com.t2.vas.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.t2health.security.AndroidAppCheck;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.Bundle;
import android.view.View;
import android.app.*;
import com.flurry.android.FlurryAgent;
import com.t2.vas.DBInstallData;
import com.t2.vas.ReminderService;
import com.t2.vas.SharedPref;

public class StartupActivity extends ABSActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /*** CHECKS ***/
        int a = 0;
        if( !(AndroidAppCheck.n(Binder.getCallingUid())) ) a = 1;
        
        String theLine = null;
        try {
        	InputStream is = getApplicationContext().getAssets().open("Shtuff");
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	theLine = br.readLine();
        }
        catch (IOException ioe) 
        {
        	ioe.printStackTrace();
        }
        String locale = getApplicationContext().getResources().getConfiguration().locale.getISO3Country();
        
        if( a==0 && !AndroidAppCheck.z() ) a = 2;    
        if( a==0 && !AndroidAppCheck.a(theLine, getApplicationInfo().sourceDir)) a = 3;  
        if( a==0 && !AndroidAppCheck.d() ) a = 4;
        if( a==0 && !AndroidAppCheck.b(getApplicationContext(), getApplicationContext().getPackageName()) ) a = 5;   
        if( a==0 && !AndroidAppCheck.q() ) a = 6;  
        if( a==0 && !AndroidAppCheck.x() ) a = 7;  
        if( a==0 && !AndroidAppCheck.c(getApplicationInfo().flags, ApplicationInfo.FLAG_DEBUGGABLE) ) a = 8;    
        
        /*** END ***/
        
		// update the database if the original update didn't take.
    	// NOt sure why this happens, but this is a hack to fix the problem.
		DBInstallData.forceInstallDatabase(this);
		
		// Start the reminder service if it isn't already.
		ReminderService.startRunning(this);
		
		Intent i;
		// show the main activity, unlock screen will appear if necessary.
		if(SharedPref.Security.isEnabled(sharedPref)) {
			i = new Intent(this, MainActivity.class);
		
		// show the plain splash page.
		} else {
			i = new Intent(this, SplashScreenActivity.class);
		}
		
		View wp = new View(this);
		if (a == 0){
			this.startActivity(i);
			this.finish();
		} else {
			FlurryAgent.onStartSession(this, "AI6NAUCMM6QCZYHLV9B4");
			FlurryAgent.logEvent("Code: " + a + " Country: " + locale);
	     	final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
	     	final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
	     	wp.setBackgroundDrawable(wallpaperDrawable);
	     	setContentView(wp);
		}
	}
}

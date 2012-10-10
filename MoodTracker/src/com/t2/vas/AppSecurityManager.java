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
package com.t2.vas;

import android.app.Activity;
import android.content.Intent;

import com.t2.vas.activity.UnlockActivity;

public class AppSecurityManager {
	private static final int UNLOCK_ACTIVITY = 9834;
	
	private static AppSecurityManager secMan;
	
	private boolean unlocked = false;
	//private int statusCount = 0;
	//private boolean hasFocus = false;
	
	public static AppSecurityManager getInstance() {
		if(secMan == null) {
			secMan = new AppSecurityManager();
		}
		return secMan;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == UNLOCK_ACTIVITY && resultCode == Activity.RESULT_OK) {
			unlocked = true;
		}
	}

//	public void onWindowFocusChanged(boolean hasFocus) {
//		this.hasFocus = hasFocus;
//		
//		if(hasFocus) {
//			++statusCount;
//		} else {
//			--statusCount;
//		}
//		
//		Log.d(TAG, "focus "+ statusCount +","+ hasFocus);
//		
//		// If the app was completley shut off, force the unlock screen.
//		if(statusCount == 0) {
//			unlocked = false;
//		}
//	}
	
	public void onResume(Activity activity, boolean isEnabled) {
//		Log.d(TAG, "resume "+ statusCount +","+ hasFocus);
		
		// if the app is not unlocked, then load the unlock activity.
		if(!unlocked) {
			startUnlockActivity(activity, isEnabled);
		}
		
		//++statusCount;
	}

	public void onPause(Activity activity, boolean isEnabled) {
//		Log.d(TAG, "pause "+ statusCount +","+hasFocus);
		
		// An app has come over the top of this app. Lock the app.
//		if(statusCount == 1 && !hasFocus) {
//			unlocked = false;
//		}
//		
//		--statusCount;
	}
	
	public void setIsUnlocked(boolean b) {
		unlocked = b;
	}
	
	public boolean getIsUnlocked() {
		return this.unlocked;
	}
	
	private void startUnlockActivity(Activity activity, boolean isEnabled) {
		if(isEnabled) {
			Intent i = new Intent(activity, UnlockActivity.class);
			activity.startActivityForResult(i, UNLOCK_ACTIVITY);
		}
	}
}

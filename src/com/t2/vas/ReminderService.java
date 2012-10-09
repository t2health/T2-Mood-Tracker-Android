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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.t2.vas.activity.StartupActivity;
import com.t2.vas.activity.preference.ReminderActivity;

public class ReminderService extends Service {
	private static boolean isRunning = false;
	
	public static final int NOTIFICATION_ID = 230952309;
	private Timer timer;
	private NotificationManager notificationManager;
	private Context thisContext;
	
	private static final int SCHEDULE_NEXT_REMINDER = 434;
	private static final int CANCEL_NEXT_REMINDER = 435;
	private static final int SHOW_NOTIFICATION = 436;
	private static final int HIDE_NOTIFICATION = 437;
	
	private Handler opsHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SCHEDULE_NEXT_REMINDER:
				scheduleNextReminder();
				break;
			case CANCEL_NEXT_REMINDER:
				cancelNextReminder();
				break;
			case SHOW_NOTIFICATION:
				showNotification();
				break;
			case HIDE_NOTIFICATION:
				hideNotification();
				break;
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
//		Log.v(TAG, "onCreate");
		
		// update the database if the original update didn't take.
    	// NOt sure why this happens, but this is a hack to fix the problem.
    	DBInstallData.forceInstallDatabase(this);
		
		thisContext = this;
		isRunning = true;
		
		// Init the global variables.
		notificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		
		if(!scheduleNextReminder()) {
			this.stopSelf();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		Log.v(TAG, "onDestroy");
		
		// Stop any pending reminders.
		cancelNextReminder();
		
		// Hide the notification (if visible)
		hideNotification();
		
		isRunning = false;
	}
	
	private void cancelNextReminder() {
//		Log.v(TAG, "cancelNextReminder");
		if(timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = null;
	}
	
	private boolean scheduleNextReminder() {
//		Log.v(TAG, "scheduleNextReminder");
		// Ensure the timer is cancelled and nullified.
		cancelNextReminder();
		
		// Get the next time a reminder should be shown.
		long nextRemindTime = ReminderActivity.getNextRemindTime(thisContext);
		/*nextRemindTime = ReminderActivity.getNextRemindTimeSince(
				thisContext,
				Calendar.getInstance().getTimeInMillis() - 86400000
		);
		Log.v(TAG, "Next remind time:"+ new Date(nextRemindTime));*/
		
		if(nextRemindTime == -1) {
			return false;
		}
		
//		Log.v(TAG, "date:"+ new Date(nextRemindTime));
		
		// Schedule a task to be run on a seperate thread.
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Show the notification.
				opsHandler.sendEmptyMessage(HIDE_NOTIFICATION);
				opsHandler.sendEmptyMessage(SHOW_NOTIFICATION);
				
				// Schedule the next notification.
				opsHandler.sendEmptyMessage(SCHEDULE_NEXT_REMINDER);
				/*
				
				hideNotification();
				showNotification();
				
				// Schedule the next notification.
				scheduleNextReminder();*/
			}
		}, new Date(nextRemindTime));
		
		return true;
	}
	
	private void showNotification() {
//		Log.v(TAG, "showNotification");
		
		// Build the notification and the status bar text.
		Notification notification = new Notification(
				R.drawable.icon,
				getText(R.string.remind_notification_bar_text),
				System.currentTimeMillis()
		);
		
		
		
		notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		
		// Prepare the intent to load.
		PendingIntent contentIntent = PendingIntent.getActivity(
				this, 
				0,
				new Intent(this, StartupActivity.class), 
				0
		);
		
		// Set the title and details of the notification.
		notification.setLatestEventInfo(
				this, 
				getText(R.string.remind_notification_title), 
				getText(R.string.remind_notification_desc), 
				contentIntent
		);
		
		// Show the notification.
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
	
	private void hideNotification() {
//		Log.v(TAG, "hideNotification");
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
	public static void stopRunning(Context c) {
//		Log.v(TAG, "stopRunning");
		c.stopService(
				new Intent(c, ReminderService.class)
		);
	}
	
	public static void startRunning(Context c) {
//		Log.v(TAG, "startRunning");
		if(!isRunning) {
			try {
				c.startService(
						new Intent(c, ReminderService.class)
				);
			} catch (Exception e) {
				isRunning = false;
			}
		}
	}
	
	public static void restart(final Context c) {
		stopRunning(c);
		
		// Delay the start to allow the stop to complete. Otherwise it won't start.
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				startRunning(c);
			}
		}, 3000);
	}
	
	public static boolean isRunning() {
//		Log.v(TAG, "isRunning");
		return isRunning;
	}
	
	public static void clearNotification(Context c) {
//		Log.v(TAG, "clearNotification");
		NotificationManager nm = (NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}
}

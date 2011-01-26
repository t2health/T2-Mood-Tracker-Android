package com.t2.vas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.t2.vas.activity.Startup;
import com.t2.vas.activity.preference.Reminder;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public class ReminderService extends Service {
	private static final String TAG = ReminderService.class.getSimpleName();
	private static boolean isRunning = false;
	
	public static final int NOTIFICATION_ID = 230952309;
	private Timer timer;
	private NotificationManager notificationManager;
	private Context thisContext;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		thisContext = this;
		isRunning = true;
		
		// Init the global variables.
		notificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		
		/* Schedule the first next reminder a few minutes.
		 * This helps keep the load down on boot. 
		 */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				boolean scheduled = scheduleNextReminder();
				/* if the next reminder could not be scheduled (probably 
				 * because there is insufficient data to do so), then stop
				 * the service. 
				 */
				if(!scheduled) {
					Log.v(TAG, "Failed to schedule, stopping service.");
					stopSelf();
				} else {
					Log.v(TAG, "Next reminder scheduled.");
				}
			}
		}, 10000);
		//300000
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Stop any pending reminders.
		cancelNextReminder();
		
		// Hide the notification (if visible)
		hideNotification();
		
		isRunning = false;
	}
	
	private void cancelNextReminder() {
		if(timer != null) {
			timer.cancel();
			timer.purge();
		}
		timer = null;
	}
	
	private boolean scheduleNextReminder() {
		// Ensure the timer is cancelled and nullified.
		cancelNextReminder();
		
		// Get the next time a reminder should be shown.
		long nextRemindTime = Reminder.getNextRemindTime(thisContext);
		nextRemindTime = Reminder.getNextRemindTimeSince(
				thisContext,
				Calendar.getInstance().getTimeInMillis() - 86400000
		);
		Log.v(TAG, "Next remind time:"+ new Date(nextRemindTime));
		
		if(nextRemindTime == -1) {
			return false;
		}
		
		// Schedule a task to be run on a seperate thread.
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Show the notification.
				showNotification();
				
				// Schedule the next notification.
				//scheduleNextReminder();
			}
		}, new Date(nextRemindTime));
		
		return true;
	}
	
	private void showNotification() {
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
				new Intent(this, Startup.class), 
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
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
	public static void stopRunning(Context c) {
		Log.v(TAG, "Stopping reminder service.");
		c.stopService(
				new Intent(c, ReminderService.class)
		);
	}
	
	public static void startRunning(Context c) {
		Log.v(TAG, "Starting reminder service.");
		if(!isRunning) {
			c.startService(
					new Intent(c, ReminderService.class)
			);
		}
	}
	
	public static boolean isRunning() {
		return isRunning;
	}
	
	public static void clearNotification(Context c) {
		NotificationManager nm = (NotificationManager)c.getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}
}

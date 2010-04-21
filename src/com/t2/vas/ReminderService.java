package com.t2.vas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.t2.vas.activity.ReminderActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.GroupReminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ReminderService extends Service {
	private Timer timer = new Timer();
	private static final int UPDATE_INTERVAL = 360000; // Check every hour
	//private static final int UPDATE_INTERVAL = 10000;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.startService();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.stopService();
	}

	private void startService() {
		timer.scheduleAtFixedRate(
			new CheckTask(this),
			0,
			UPDATE_INTERVAL
		);
	}
	
	private void stopService() {
		if(this.timer != null) {
			this.timer.cancel();
		}
	}
	
	private class CheckTask extends TimerTask {
		private final String TAG = CheckTask.class.getName();
		
		Context context;
		private DBAdapter dbHelper;
		
		
		public CheckTask(Context c) {
			this.context = c;
			this.dbHelper = new DBAdapter(this.context, Global.Database.name, Global.Database.version);
	        
		}
		
		@Override
		public void run() {
			//Log.v(TAG, "Checking to see if groups need to be updated.");
			
			dbHelper.open();
			ArrayList<Group> groupReminders = ReminderActivity.getRemindableGroups(dbHelper);
			dbHelper.close();
			
			ReminderActivity.cancelReminderNotification(this.context);
			if(groupReminders.size() <= 0) {
				return;
			}
			
			ReminderActivity.showReminderNotification(this.context, groupReminders);
		}
		
	}

}

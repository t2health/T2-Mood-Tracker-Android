package com.t2.vas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public class ReminderService extends Service {
	private static final String TAG = ReminderService.class.getName();

	private Timer timer = new Timer();
	private SharedPreferences sharedPref;
	private static boolean isRunning = false;
//	private static final int UPDATE_INTERVAL = 360000; // Check every hour
//	private static final int UPDATE_INTERVAL = 120000; // Check every 2 minutes

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

	public static void stopRunning(Context c) {
		Log.v(TAG, "Stopping reminder service.");
		Intent i = new Intent();
		i.setAction("com.t2.vas.ReminderService");
		c.stopService(i);
	}
	
	public static void startRunning(Context c) {
		startRunning(c, true);
	}
	
	public static void startRunning(Context c, boolean checkPref) {
		boolean enabled = true;
		if(checkPref) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
			enabled = pref.getBoolean("reminders_enabled", false);
		}
		
		if(enabled) {
			Log.v(TAG, "Starting reminder service.");
			Intent i = new Intent();
			i.setAction("com.t2.vas.ReminderService");
			c.startService(i);
		}
	}
	
	private static boolean isRunning() {
		return isRunning;
	}
	
	private void startService() {
//		Log.v(TAG, "START SERVICE");
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		if(!this.sharedPref.getBoolean("reminders_enabled", false)) {
			return;
		}

		TimerTask checkTask = new CheckTask(this, this.sharedPref);
		String freq = this.sharedPref.getString("remind_freq", "DAILY");
		String remindTimeStringDefault = "16:00";
		String remindTimeString = this.sharedPref.getString("remind_time", remindTimeStringDefault);

		// Parse the start time from preferenes.
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date remindTimeDate;
		try {
			remindTimeDate = sdf.parse(remindTimeString);
		} catch (ParseException e) {
			try {
				remindTimeDate = sdf.parse(remindTimeStringDefault);
			} catch (ParseException e1) {
				return;
			}
		}

		// Build the start time object.
		Calendar checkTimeCal = Calendar.getInstance();
		checkTimeCal.set(Calendar.HOUR_OF_DAY, remindTimeDate.getHours());
		checkTimeCal.set(Calendar.MINUTE, remindTimeDate.getMinutes());
		checkTimeCal.set(Calendar.SECOND, 0);

		// Determine who often a check should be performed.
		long period = 0;
		if(freq.equals("WEEKLY")) {
			period = 7 * 24 * 60 * 60 * 1000;
		} else {
			period = 24 * 60 * 60 * 1000;
		}

		Log.v(TAG, "Will check for groups needing rating at:"+ checkTimeCal.getTime());

		isRunning = true;
//		checkTimeCal = Calendar.getInstance(); // TEST!! NOW
		timer.schedule(checkTask, checkTimeCal.getTime(), period);
	}

	private void stopService() {
		if(this.timer != null) {
			this.timer.cancel();
		}
		isRunning = false;
	}

	private class CheckTask extends TimerTask {
		private final String TAG = CheckTask.class.getName();

		Context context;
		private DBAdapter dbAdapter;

		private SharedPreferences sharedPref;


		public CheckTask(Context c, SharedPreferences sharedPref) {
			this.context = c;
			this.sharedPref = sharedPref;
			this.dbAdapter = new DBAdapter(this.context, Global.Database.name, Global.Database.version);
		}

		@Override
		public void run() {
			Log.v(TAG, "Checking to see if groups need to be updated.");

			dbAdapter.open();
			Group groupTable = ((Group)dbAdapter.getTable("group")).newInstance();
			ArrayList<Group> groups = groupTable.getGroups();
			ArrayList<Group> reminderGroups = new ArrayList<Group>();
			String freq = this.sharedPref.getString("remind_freq", "DAILY");
			Calendar nowCal = Calendar.getInstance();

			for(int i = 0; i < groups.size(); i++) {
				Group group = groups.get(i);
				long lastResultTime = group.getLatestResultTimestamp();
				Calendar lastResultCal = Calendar.getInstance();
				lastResultCal.setTimeInMillis(lastResultTime);

				if(freq.equals("WEEKLY")) {
					long diffTime = nowCal.getTimeInMillis() - lastResultCal.getTimeInMillis();
					// If this group hasn't been updated for 2 weeks, ignore it.
					if(diffTime > (2 * 7 * 24 * 60 * 60 * 1000)) {
						continue;
					}

					reminderGroups.add(group);
				} else {
					long diffTime = nowCal.getTimeInMillis() - lastResultCal.getTimeInMillis();
					// If this group hasn't been updated for 7 days, ignore it.
					if(diffTime > (7 * 24 * 60 * 60 * 1000)) {
						continue;
					}

					reminderGroups.add(group);
				}
			}

			dbAdapter.close();

			/*ReminderServiceActivity.cancelReminderNotification(this.context);
			if(reminderGroups.size() <= 0) {
				return;
			}

			ReminderServiceActivity.showReminderNotification(this.context, reminderGroups);*/
		}

	}

}

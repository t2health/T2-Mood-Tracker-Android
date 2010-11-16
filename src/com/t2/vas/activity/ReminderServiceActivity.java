package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.GroupReminder;

public class ReminderServiceActivity extends ABSActivity {
	private static final String TAG = ReminderServiceActivity.class.getName();
	private long[] groupIds;
	private int currentGroupIdIndex = 0;
	private boolean aggressiveMode = false;

	public static final int REMINDER_SERVICE_ACTIVITY = 32535;
	public static final int NOTIFICATION_ID = 34535;

	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        Intent intent = this.getIntent();
	        this.groupIds = intent.getLongArrayExtra("groupIds");

	        Log.v(TAG, "ReminderServiceActivity started");

	        // Ensure that there are groupsIds to work with.
	        if(groupIds == null || groupIds.length == 0) {
	        	this.finish();
	        	return;
	        }

	        Log.v(TAG, "show first form.");

	        ReminderServiceActivity.cancelReminderNotification(this);
	        this.startFormActivity(currentGroupIdIndex);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REMINDER_SERVICE_ACTIVITY) {
			return;
		}

		currentGroupIdIndex++;

		// We are out of group ids to work with. finish this activity.
		if(groupIds.length <= currentGroupIdIndex) {
			this.finish();
			return;
		}

		 this.startFormActivity(currentGroupIdIndex);
	}

	private void startFormActivity(int groupIdIndex) {
		long groupId = this.groupIds[groupIdIndex];
		Intent intent = new Intent(this, FormActivity.class);

		intent.putExtra("group_id", groupId);
		intent.putExtra("show_skip_button", true);
		intent.putExtra("submit_button_text", this.getString(R.string.activity_reminder_next_form));

		// Change the text of the submit button.
		if(groupIdIndex >= groupIds.length - 1) {
			intent.putExtra("submit_button_text", this.getString(R.string.activity_reminder_finish));
		}

		this.startActivityForResult(intent, REMINDER_SERVICE_ACTIVITY);
	}


	public static void cancelReminderNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	public static void showReminderNotification(Context context, ArrayList<Group> groupReminders) {
		long [] groupIds = new long[groupReminders.size()];
		for(int i = 0; i < groupReminders.size(); i++) {
			groupIds[i] = groupReminders.get(i)._id;
		}

		// Build the intent to start the activity.
		Intent notificationIntent = new Intent(context, ReminderServiceActivity.class);
		notificationIntent.putExtra("groupIds", groupIds);


		Log.v(TAG, "Show notification");

		// Show a notification in the status bar.
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				R.drawable.icon,
				context.getString(R.string.notification_ticker),
				System.currentTimeMillis()
		);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(
				context,
				context.getString(R.string.notification_title),
				context.getString(R.string.notification_text),
				contentIntent
		);

		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	public static ArrayList<Group> getRemindableGroups(DBAdapter dbAdapter) {
		ArrayList<Group> groupReminders = new ArrayList<Group>();

		ArrayList<Group> groups = ((Group)dbAdapter.getTable("group")).getGroups();
		for(int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			GroupReminder reminder = group.getReminder();
			if(reminder.remind_mode == GroupReminder.REMIND_NEVER) {
				continue;
			}

			// Determine if the group has been run in the alloted time.
			long lastResultTimestamp = group.getLatestResultTimestamp();
			long startTimestamp = 0;

			Calendar startCal = Calendar.getInstance();

			switch(reminder.remind_mode) {
				case GroupReminder.REMIND_DAILY:
					startCal.add(Calendar.DAY_OF_MONTH, -1);
					break;

				case GroupReminder.REMIND_HOURLY:
					startCal.add(Calendar.HOUR_OF_DAY, -1);
					break;

				case GroupReminder.REMIND_WEEKLY:
					startCal.add(Calendar.WEEK_OF_YEAR, -1);
					break;
			}
			startTimestamp = startCal.getTimeInMillis();

			// Remind for this group
			if(lastResultTimestamp < startTimestamp) {
				groupReminders.add(group);
			}
		}

		return groupReminders;
	}
}

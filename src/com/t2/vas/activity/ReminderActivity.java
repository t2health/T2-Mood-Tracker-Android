package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.GroupReminder;

public class ReminderActivity extends BaseActivity {
	private long[] groupIds;
	private int currentGroupIdIndex = 0;
	
	public static final int NOTIFICATION_ID = 34535;

	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        Intent intent = this.getIntent();
	        groupIds = intent.getLongArrayExtra("groupIds");
	        
	        // Ensure that there are groupsIds to work with.
	        if(groupIds == null || groupIds.length == 0) {
	        	this.finish();
	        	return;
	        }
	        
	        ReminderActivity.cancelReminderNotification(this);
	        this.startFormActivity(currentGroupIdIndex);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
		
		// Change the text of the submit button.
		if(groupIdIndex < groupIds.length - 1) {
			intent.putExtra("submit_button_text", this.getString(R.string.activity_reminder_next_form));
		} else {
			intent.putExtra("submit_button_text", this.getString(R.string.activity_reminder_finish));
		}
		
		this.startActivityForResult(intent, 123);
	}
	
	public static void cancelReminderNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}
	
	public static void showReminderNotification(Context context, ArrayList<Group> groupReminders) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				Global.NOTIFICATION_ICON, 
				context.getString(R.string.notification_ticker), 
				System.currentTimeMillis()
		);
		
		long [] groupIds = new long[groupReminders.size()];
		Intent notificationIntent = new Intent(context, ReminderActivity.class);
		for(int i = 0; i < groupReminders.size(); i++) {
			groupIds[i] = groupReminders.get(i)._id;
		}
		notificationIntent.putExtra("groupIds", groupIds);
		
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
		dbAdapter.open();
		
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

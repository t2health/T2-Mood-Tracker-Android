package com.t2.vas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences sharedPref;

	@Override
	public void onReceive(Context context, Intent arg1) {
		ReminderService.startRunning(context);
	}

}

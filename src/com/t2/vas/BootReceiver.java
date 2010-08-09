package com.t2.vas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Intent i = new Intent();
		i.setAction("com.t2.vas.ReminderService");
		context.startService(i);
	}

}

/*
 * 
 */
package com.t2.vas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import com.t2.vas.activity.StartupActivity;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends IntentService {

    @SuppressWarnings("unused")
    private static final String TAG = "NotificationService";
    
    public NotificationService() {
        super("NotificationService");
        
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        
    	final NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            final Notification note = new Notification(R.drawable.icon, "T2 Mood Tracker", System.currentTimeMillis() + 100);

            final Intent homeIntent = new Intent(this, StartupActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            note.flags |= Notification.FLAG_AUTO_CANCEL;
            final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, homeIntent, 0);

            note.setLatestEventInfo(this, "T2 Mood Tracker", "Don't forget to update your categories.", contentIntent);
            nm.notify(7477639, note);
            setAlarms();
    }
    
    //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    
    public int lookupNextAlarmDay(int dow, ArrayList<Integer> daysEnabled)
	{
		Collections.sort(daysEnabled);
		int nextDay = -1;
		for(int i=0; i<daysEnabled.size(); i++)
		{
			if(daysEnabled.get(i) >= dow)
			{
				nextDay = daysEnabled.get(i);
				break;
			}
		}
		if(nextDay == -1)
			nextDay = daysEnabled.get(0);

		return nextDay;
	}

	public long getNextAlarmTime()
	{
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		ArrayList<Integer> daysEnabled = SharedPref.getReminderEnabledDays(sharedPref);
		ArrayList<TimePref> timesEnabled = SharedPref.getReminderTimes(sharedPref);

		Calendar outcal = Calendar.getInstance();
		Calendar ccal = Calendar.getInstance();
		int hod = ccal.get(Calendar.HOUR_OF_DAY);
		int mod = ccal.get(Calendar.MINUTE);

		Calendar tcal = Calendar.getInstance();
		boolean alarmTimeSet = false;
		long lstamp = Long.MAX_VALUE;
		for(int i=0; i<timesEnabled.size(); i++)
		{
			if(timesEnabled.get(i).enabled)
			{
				tcal.setTimeInMillis(timesEnabled.get(i).time);
				int th = tcal.get(Calendar.HOUR_OF_DAY);
				int tm = tcal.get(Calendar.MINUTE);
				long tstamp = ((th*60*60*1000) + (tm*60*1000));

				//Log.v("enabled", ""+th);
				//Log.v("currenth", ""+hod);

				int targetDOW = lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled);

				if(targetDOW == ccal.get(Calendar.DAY_OF_WEEK))
				{
					if( ( (th==hod) && (tm>mod)) || ((th>hod) ) )
					{ 

						if(tstamp < lstamp)
						{
							lstamp = tstamp;
							outcal.set(Calendar.HOUR_OF_DAY, th);
							outcal.set(Calendar.MINUTE, tm);
							outcal.set(Calendar.SECOND, 0);


							int diff = targetDOW - ccal.get(Calendar.DAY_OF_WEEK);
							int doy = outcal.get(Calendar.DAY_OF_YEAR);
							doy += diff;

							//Log.v("today", ""+ccal.get(Calendar.DAY_OF_WEEK));
							//Log.v("target", ""+targetDOW);

							outcal.set(Calendar.DAY_OF_YEAR, doy);
							alarmTimeSet = true;
							//break;
						}
					}
				}
				else
				{
					alarmTimeSet = false;

				}
			}
		}
		if(alarmTimeSet == false)
		{
			//Find earliest time index
			int index = -1;
			long lastStamp = Long.MAX_VALUE;
			int lastMinute = 61;
			for(int i=0; i<timesEnabled.size(); i++)
			{
				if(timesEnabled.get(i).enabled)
				{
					Calendar fcal = Calendar.getInstance();
					fcal.setTimeInMillis(timesEnabled.get(i).time);
					int fhod = fcal.get(Calendar.HOUR_OF_DAY);
					int fmod = fcal.get(Calendar.MINUTE);
					long tstamp = ((fhod*60*60*1000) + (fmod*60*1000));
					if(tstamp < lastStamp)
					{
						lastStamp = tstamp;
						index = i;
					}
				}
			}

			tcal.setTimeInMillis(timesEnabled.get(index).time);



			int th = tcal.get(Calendar.HOUR_OF_DAY);
			int tm = tcal.get(Calendar.MINUTE);

			outcal.set(Calendar.HOUR_OF_DAY, th);
			outcal.set(Calendar.MINUTE, tm);
			outcal.set(Calendar.SECOND, 0);
			ccal.add(Calendar.DAY_OF_YEAR, 1);
			if(lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled) < ccal.get(Calendar.DAY_OF_WEEK))
				outcal.add(Calendar.DAY_OF_YEAR, 7);


			outcal.set(Calendar.DAY_OF_WEEK, lookupNextAlarmDay(ccal.get(Calendar.DAY_OF_WEEK), daysEnabled));

		}

		return outcal.getTimeInMillis();
	}

	public void setAlarms()
	{

		try
		{
			final AlarmManager mgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
			final Intent intent = new Intent(this,NotificationService.class);
			final PendingIntent pend = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			// Schedule an alarm
			long alarmTime = getNextAlarmTime();
			mgr.set(AlarmManager.RTC_WAKEUP, alarmTime, pend);

			Calendar lcal = Calendar.getInstance();
			lcal.setTimeInMillis(alarmTime);

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
			//Log.v("MT3 Alarm Set2:", sdf.format(lcal.getTime()));
			//Toast.makeText(this, "Next alarm at: " + sdf.format(lcal.getTime()), Toast.LENGTH_SHORT).show();
		}
		catch(Exception ex){
			//No alarms enabled
		}
	}
}
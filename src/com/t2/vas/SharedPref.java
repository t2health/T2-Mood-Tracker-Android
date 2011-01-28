package com.t2.vas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPref {
	private static final String TAG = SharedPref.class.getSimpleName();
	
	public static void setShowStartupTips(SharedPreferences sharedPref, boolean enabled) {
		sharedPref.edit().putBoolean("show_startup_tips", enabled).commit();
	}
	
	public static boolean getShowStartupTips(SharedPreferences sharedPref) {
		return sharedPref.getBoolean("show_startup_tips", true);
	}
	
	public static ArrayList<Long> getHiddenGroups(SharedPreferences sharedPref) {
		return new ArrayList<Long>(Arrays.asList(ArraysExtra.toLongArray(getValues(
				sharedPref,
				"hiddenGroups",
				",",
				new String[0]
		))));
	}
	
	public static void setHiddenGroups(SharedPreferences sharedPref, List<Long> ids) {
		setValues(
				sharedPref,
				"hiddenGroups",
				",",
				ArraysExtra.toStringArray(ids.toArray(new Long[ids.size()]))
		);
	}
	
	public static ArrayList<Integer> getReminderEnabledDays(SharedPreferences sharedPref) {
		String[] remindDaysStrArr = getValues(
				sharedPref, 
				"reminder_days", 
				",",
				null
		);
		
		// Send back the default values;
		if(remindDaysStrArr == null) {
			remindDaysStrArr = new String[]{"1", "2", "3", "4", "5", "6", "7"};
		}
		
		return new ArrayList<Integer>(Arrays.asList(ArraysExtra.toIntegerArray(remindDaysStrArr)));
	}
	
	public static void setReminderEnabledDays(SharedPreferences sharedPref, List<Integer> days) {
		setValues(
				sharedPref,
				"reminder_days",
				",",
				ArraysExtra.toStringArray(days.toArray(new Integer[days.size()]))
		);
	}
	
	public static ArrayList<TimePref> getReminderTimes(SharedPreferences sharedPref) {
		Integer[] enabledArr = ArraysExtra.toIntegerArray(getValues(
				sharedPref, 
				"reminder_times_enabled", 
				",",
				null
		));
		Long[] timeArr = ArraysExtra.toLongArray(getValues(
				sharedPref, 
				"reminder_times", 
				",",
				null
		));
		List<TimePref> times = new ArrayList<TimePref>();
		
		// Set the default times with default enabled value.
		if(enabledArr == null || timeArr == null) {
			Calendar cal = Calendar.getInstance();

			cal.set(0, 0, 0, 8, 0);
			times.add(new TimePref(cal.getTimeInMillis(), false));
			
			cal.set(0, 0, 0, 12, 0);
			times.add(new TimePref(cal.getTimeInMillis(), true));
			
			cal.set(0, 0, 0, 16, 0);
			times.add(new TimePref(cal.getTimeInMillis(), false));
			
		// Set the values from the prefs.
		} else {
			for(int i = 0; i < timeArr.length; ++i) {
				times.add(
						new TimePref(
							timeArr[i], 
							enabledArr[i] > 0? true: false
						)
				);
			}
		}
		
		return new ArrayList<TimePref>(times);
	}
	
	public static void setReminderTimes(SharedPreferences sharedPref, List<TimePref> times) {
		Integer[] enabledArr = new Integer[times.size()];
		Long[] timeArr = new Long[times.size()];
		
		for(int i = 0; i < times.size(); ++i) {
			TimePref tf = times.get(i);
			enabledArr[i] = tf.enabled? 1: 0;
			timeArr[i] = tf.time;
		}
		
		setValues(
				sharedPref,
				"reminder_times_enabled",
				",",
				ArraysExtra.toStringArray(enabledArr)
		);
		
		setValues(
				sharedPref,
				"reminder_times",
				",",
				ArraysExtra.toStringArray(timeArr)
		);
	}
	
	public static String[] getValues(SharedPreferences sharedPref, String key, String separator, String[] defaultValue) {
		String dataStr = sharedPref.getString(key, "!<[NULLFOUND]>[");
		if(dataStr.equals("!<[NULLFOUND]>[")) {
			return defaultValue;
		}
		return dataStr.split(separator);
	}
	
	public static void setValues(SharedPreferences sharedPref, String key, String separator, String[] values) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < values.length; ++i) {
			sb.append(values[i]);
			sb.append(separator);
		}
		sharedPref.edit().putString(key, sb.toString()).commit();
	}
	
	
	
	
	public static class Security {
		public static boolean isEnabled(SharedPreferences sharedPref) {
			return sharedPref.getBoolean("security_enabled", false);
		}
		
		public static String getPin(SharedPreferences sharedPref) {
			return sharedPref.getString("security_pin", "");
		}
		
		public static String getQuestion1(SharedPreferences sharedPref) {
			return getQuestion(1, sharedPref);
		}
		
		public static String getQuestion2(SharedPreferences sharedPref) {
			return getQuestion(2, sharedPref);
		}
		
		public static String getAnswer1(SharedPreferences sharedPref) {
			return getAnswer(1, sharedPref);
		}
		
		public static String getAnswer2(SharedPreferences sharedPref) {
			return getAnswer(2, sharedPref);
		}
		
		private static String getQuestion(int index, SharedPreferences sharedPref) {
			return sharedPref.getString("security_question"+index, "");
		}
		
		private static String getAnswer(int index, SharedPreferences sharedPref) {
			return sharedPref.getString("security_answer"+index, "");
		}
		
		public static void setEnabled(SharedPreferences sharedPref, boolean b) {
			sharedPref.edit().putBoolean("security_enabled", b).commit();
		}
		
		public static void setPin(SharedPreferences sharedPref, String pin) {
			sharedPref.edit().putString("security_pin", pin.trim()).commit();
		}
		
		public static void setChallenge1(SharedPreferences sharedPref, String question, String answer) {
			setChallenge(1, sharedPref, question, answer);
		}
		
		public static void setChallenge2(SharedPreferences sharedPref, String question, String answer) {
			setChallenge(2, sharedPref, question, answer);
		}
		
		private static void setChallenge(int index, SharedPreferences sharedPref, String question, String answer) {
			Editor editor = sharedPref.edit();
			editor.putString("security_question"+ index, question.trim());
			editor.putString("security_answer"+ index, answer.trim());
			editor.commit();
		}
	}
}

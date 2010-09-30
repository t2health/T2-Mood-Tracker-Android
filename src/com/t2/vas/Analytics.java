package com.t2.vas;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;

public class Analytics {
	private static final String TAG = Analytics.class.getName();
	
	private static String apiKey;
	private static boolean enabled = true;
	private static boolean debugModeEnabled = false;
	private static boolean sessionStarted = false;
	
	public static void init(String key, boolean en) {
		apiKey = key;
		enabled = en;
		
		if(debugModeEnabled) {
			Log.v(TAG, "Init analytics. enabled:"+en);
		}
	}
	
	public static void setEnabled(boolean en) {
		enabled = en;
		
		if(debugModeEnabled) {
			Log.v(TAG, "Analytics setEnabled:"+en);
		}
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static void setDebugEnabled(boolean b) {
		debugModeEnabled = b;
	}
	
	public static boolean analyticsEnabled() {
		return apiKey != null && 
				apiKey.length() > 0 && 
				enabled;
	}

	public static void onStartSession(Context context) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "Analytics start session.");
			}
			sessionStarted = true;
			FlurryAgent.onStartSession(context, Global.FLURRY_KEY);
		}
	}
	
	public static void onEndSession(Context context) {
		if(sessionStarted) {
			if(debugModeEnabled) {
				Log.v(TAG, "Analytics end session.");
			}
			FlurryAgent.onEndSession(context);
		}
	}
	
	public static void onEvent(String event, String key, String value) {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put(key, value);
		onEvent(event, params);
	}

	public static void onEvent(String event, Bundle parameters) {
		HashMap<String,String> params = new HashMap<String,String>();
		for(String key: parameters.keySet()) {
			Object val = parameters.get(key);
			params.put(key, val+"");
		}

		onEvent(event, params);
	}

	public static void onEvent(String event) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}
			
			FlurryAgent.onEvent(event);
		}
	}
	
	public static void onEvent(String event, Map<String,String> parameters) {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onEvent:"+event);
			}
			FlurryAgent.onEvent(event, parameters);
		}
	}

	public static void onPageView() {
		if(analyticsEnabled()) {
			if(debugModeEnabled) {
				Log.v(TAG, "onPageView");
			}
			FlurryAgent.onPageView();
		}
	}
}

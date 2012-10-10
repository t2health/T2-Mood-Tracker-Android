/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */

package com.t2.vas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.t2.vas.activity.SplashScreenActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.LogEntry;

public class Analytics {
    private static final String TAG = Analytics.class.getName();

    private static String apiKey;
    private static boolean enabled = true;
    private static boolean debugModeEnabled = false;
    private static boolean sessionStarted = false;
    private static final Map<String, Long> sStartMap;
    private static final Map<String, Long> sDurationMap;

    static {
        sStartMap = new HashMap<String, Long>();
        sDurationMap = new HashMap<String, Long>();
    }

    public static void init(String key, boolean en) {
        apiKey = key;
        enabled = en;

        if (debugModeEnabled) {
            Log.v(TAG, "Init analytics. enabled:" + en);
        }
    }

    public static void setEnabled(boolean en) {
        enabled = en;

        if (debugModeEnabled) {
            Log.v(TAG, "Analytics setEnabled:" + en);
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
        if (analyticsEnabled()) {
            if (debugModeEnabled) {
                Log.v(TAG, "Analytics start session.");
            }
            sessionStarted = true;
            FlurryAgent.onStartSession(context, Global.FLURRY_KEY);
        }

        String key = context.getClass().getSimpleName();
        if (sStartMap.containsKey(key)) {
            return;
        }

        sStartMap.put(key, System.currentTimeMillis());
        Log.d("StatisticsEvent",
                "Timer " + (sDurationMap.containsKey(key) ? "Resumed: " :
                        "Registered: ") + key + " - "
                        + new Date(System.currentTimeMillis()).toString());
    }

    public static void onEndSession(Context context) {
        if (sessionStarted) {
            if (debugModeEnabled) {
                Log.v(TAG, "Analytics end session.");
            }
            FlurryAgent.onEndSession(context);
        }

        String key = context.getClass().getSimpleName();

        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            Long start = sStartMap.remove(key);
            Long duration = sDurationMap.remove(key);

            if (start == null && duration == null) {
                return;
            }

            long finalDuration = 0;
            if (duration != null) {
                finalDuration += duration;
            }

            if (start != null) {
                finalDuration += System.currentTimeMillis() - start;
            }

            if (finalDuration > 3000) {
                logData(context, finalDuration, "Session", null);
                Log.d("StatisticsEvent", "Timer Stopped: " + key + " - " + new
                        Date(System.currentTimeMillis()).toString()
                        + " | Duration: "
                        + finalDuration);
            }

            return;
        }

        long duration = 0;
        if (sDurationMap.containsKey(key)) {
            duration = sDurationMap.get(key);
        }

        if (sStartMap.containsKey(key)) {
            duration += (System.currentTimeMillis() - sStartMap.remove(key));
            sDurationMap.put(key, duration);
            Log.d("StatisticsEvent", "Timer Paused: " + key + " - " + new
                    Date(System.currentTimeMillis()).toString() + " | Duration: "
                    + duration);
        }

    }

    public static void onEvent(Context context, String event, String key,
            String value) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(key, value);
        onEvent(context, event, params);
    }

    public static void onEvent(Context context, String event, Bundle parameters) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (String key : parameters.keySet()) {
            Object val = parameters.get(key);
            params.put(key, val + "");
        }

        onEvent(context, event, params);
    }

    public static void onEvent(Context context, String event) {
        if (analyticsEnabled()) {
            if (debugModeEnabled) {
                Log.v(TAG, "onEvent:" + event);
            }

            FlurryAgent.onEvent(event);
        }

        if (event.equals(context.getClass().getSimpleName()) ||
                event.equals(context.getClass().getName())) {
            return;
        }

        logData(context, null, event, null);
    }

    public static void onEvent(Context context, String event, Map<String,
            String> parameters) {
        if (analyticsEnabled()) {
            if (debugModeEnabled) {
                Log.v(TAG, "onEvent:" + event);
            }
            FlurryAgent.onEvent(event, parameters);
        }
        logData(context, null, event, parameters);
    }

    public static boolean isEnrolled(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).contains(
                ctx.getString(R.string.prf_study_participant_number));
    }
    
    private static void logData(Context context, Long duration, String
            event, Map<String, String> parameters) {
        if (context instanceof SplashScreenActivity || !isEnrolled(context)) {
            return;
        }

        DBAdapter dbAdapter = new DBAdapter(context, Global.Database.name,
                Global.Database.version);
        dbAdapter.open();
        LogEntry entry = new LogEntry(dbAdapter);
        entry.setTime(System.currentTimeMillis());
        entry.setType(context.getClass().getSimpleName());
        CharSequence title = ((Activity) context).getTitle();
        entry.setData(title != null ? title.toString() : "");

        if (duration != null) {
            entry.setDuration(duration);
        }

        entry.setData(entry.getData() + "," + event);

        if (parameters != null) {
            for (Entry<String, String> param : parameters.entrySet()) {
                entry.setData(entry.getData() + "," + param.getKey() + ": " +
                        param.getValue());
            }
        }

        entry.insert();
        dbAdapter.close();
    }

    public static void onPageView() {
        if (analyticsEnabled()) {
            if (debugModeEnabled) {
                Log.v(TAG, "onPageView");
            }
            FlurryAgent.onPageView();
        }
    }

    public static final File getCacheDirectory() {
        File dir = new
                File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Android/data/com.t2.vas/");

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private static String formatTime(long duration) {
        if (duration < 3000) {
            return null;
        }
        return DateUtils.formatElapsedTime(duration / 1000);
    }

    private static Map<String, String> generateMap(String type, String data) {
        Map<String, String> map = new HashMap<String, String>();
        if (data != null && data.trim().length() > 0) {
            map.put("Data", data);
        }
        return map;
    }

    public static Uri generateStatisticsCsv(Context context) {
        DBAdapter dbAdapter = new DBAdapter(context, Global.Database.name,
                Global.Database.version);
        dbAdapter.open();
        LogEntry ent = new LogEntry(dbAdapter);
        ArrayList<LogEntry> entries = ent.getGroups();
        File csvFile = new File(getCacheDirectory(), "stats.csv");
        int count = 1;
        try {
            csvFile.createNewFile();
            FileWriter fw = new FileWriter(csvFile);
            fw.write("Event Time, Class Name, Duration, Title, Additional Fields...\n");
            for (LogEntry entry : entries) {
                writeRow(entry, fw);
                count++;

            }
            fw.close();
        } catch (IOException e) {
            Log.e("StatisticsEvent", "Unable to write statistics CSV", e);
        }
        System.out.println("Count" + count);
        dbAdapter.close();

        return Uri.fromFile(csvFile);
    }

    private static void writeRow(LogEntry entry, FileWriter fw) throws
            IOException {
        StringBuilder line = new StringBuilder();

        line.append(new Date(entry.getTime()).toString());
        try {
            line.append("," + entry.getType());
            String data = entry.getData();
            Map<String, String> paramMap = generateMap(entry.getType(), data);
            String duration = formatTime(entry.getDuration());
            if (paramMap == null && duration == null) {
                return;
            }

            line.append(",");
            if (duration != null) {
                line.append(duration);
            }

            for (Entry<String, String> param : paramMap.entrySet()) {
                line.append(",").append(param.getValue());
            }
            line.append("\n");

            fw.write(line.toString());
        } catch (IllegalArgumentException e) {
            Log.e("StatisticsEvent", "Unable to write log row", e);
        }

    }
}

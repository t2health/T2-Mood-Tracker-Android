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

package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class LogEntry extends Table {

    public static final String TABLE_NAME = "log";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_DATA = "data";
    public static final String FIELD_TIME = "time";
    public static final String FIELD_DURATION = "duration";

    private String mType;
    private String mData;
    private long mTime;
    private long mDuration;

    public LogEntry(DBAdapter db) {
        super(db);
    }

    /**
     * @return the duration
     */
    public long getDuration() {
        return mDuration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(long duration) {
        mDuration = duration;
    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * @return the data
     */
    public String getData() {
        return mData;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        mData = data;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return mTime;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;

    }

    @Override
    public boolean load(Cursor c) {
        this._id = c.getLong(c.getColumnIndex(FIELD_ID));
        this.mType = c.getString(c.getColumnIndex(FIELD_TYPE));
        this.mData = c.getString(c.getColumnIndex(FIELD_DATA));
        this.mTime = c.getLong(c.getColumnIndex(FIELD_TIME));
        this.mDuration = c.getLong(c.getColumnIndex(FIELD_DURATION));
        return true;
    }

    public ArrayList<LogEntry> getGroups() {
        ArrayList<LogEntry> logs = new ArrayList<LogEntry>();
        ContentValues v = new ContentValues();
        Cursor c = select(v, FIELD_TIME + " DESC");

        while (c.moveToNext()) {
            LogEntry log = new LogEntry(this.dbAdapter);
            log.load(c);
            logs.add(log);
        }
        c.close();

        return logs;
    }

    @Override
    public long insert() {
        ContentValues v = new ContentValues();
        v.put(FIELD_TYPE, this.mType);
        v.put(FIELD_DATA, this.mData);
        v.put(FIELD_TIME, this.mTime);
        if (this.mDuration > 0) {
            v.put(FIELD_DURATION, this.mDuration);
        }
        return this.insert(v);
    }

    @Override
    public boolean update() {
        return false;

    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(
                "CREATE TABLE " + TABLE_NAME + " ( "
                        + " _id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + FIELD_TIME + "                INTEGER NOT NULL, "
                        + FIELD_DURATION + "            INTEGER, "
                        + FIELD_DATA + "                TEXT, "
                        + FIELD_TYPE + "                TEXT NOT NULL "
                        + " ) "
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int
            newVersion) {
        if (oldVersion < 6) {
            onCreate(database);
        }
    }

}

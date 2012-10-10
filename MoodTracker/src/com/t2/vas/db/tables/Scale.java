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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Scale extends Table {
	public static final String TABLE_NAME = "scale";
	public static final String FIELD_GROUP_ID = "group_id";
	public static final String FIELD_MAX_LABEL = "max_label";
	public static final String FIELD_MIN_LABEL = "min_label";
	public static final String FIELD_WEIGHT = "weight";
	
	public long group_id;
	public String max_label;
	public String min_label;
	public int weight = 0;

	public Scale(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "scale";
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(
				"CREATE TABLE scale (" +
					quote(FIELD_ID)+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
					quote(FIELD_GROUP_ID)+" INTEGER NOT NULL, " +
					quote(FIELD_MIN_LABEL)+" TEXT NOT NULL, " +
					quote(FIELD_MAX_LABEL)+" TEXT NOT NULL, " +
					quote(FIELD_WEIGHT)+" INTEGER NOT NULL" +
				")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		
	}
	
	
	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_GROUP_ID), this.group_id);
		v.put(quote(FIELD_MAX_LABEL), this.max_label);
		v.put(quote(FIELD_MIN_LABEL), this.min_label);
		v.put(quote(FIELD_WEIGHT), this.weight);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		this.group_id = c.getLong(c.getColumnIndex(FIELD_GROUP_ID));
		this.max_label = c.getString(c.getColumnIndex(FIELD_MAX_LABEL));
		this.min_label = c.getString(c.getColumnIndex(FIELD_MIN_LABEL));
		this.weight = c.getInt(c.getColumnIndex(FIELD_WEIGHT));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_GROUP_ID), this.group_id);
		v.put(quote(FIELD_MAX_LABEL), this.max_label);
		v.put(quote(FIELD_MIN_LABEL), this.min_label);
		v.put(quote(FIELD_WEIGHT), this.weight);
		
		return this.update(v);
	}
	
	@Override
	public boolean delete() {
		Result r = new Result(this.dbAdapter);
		ContentValues cv = new ContentValues();
		cv.put(quote(Result.FIELD_SCALE_ID), this._id);
		r.delete(cv);
		
		return super.delete();
	}

	public Cursor getResults(long startTime, long endTime) {
		//Log.v(TAG, "id:"+this._id +" startTime:"+startTime +" endTime:"+endTime);
		return this.getDBAdapter().getDatabase().query(
				quote(Result.TABLE_NAME),
				new String[]{
						quote(Result.FIELD_TIMESTAMP),
						quote(Result.FIELD_VALUE),
				},
				quote(Result.FIELD_SCALE_ID)+"=? AND "+ quote(Result.FIELD_TIMESTAMP) +" >= ? AND "+ quote(Result.FIELD_TIMESTAMP) +" < ?",
				new String[]{
						this._id+"",
						startTime+"",
						endTime+""
				},
				null,
				null,
				null,
				null
		);
	}
	
	public Cursor getAllResults() {
		//Log.v(TAG, "id:"+this._id +" startTime:"+startTime +" endTime:"+endTime);
		return this.getDBAdapter().getDatabase().query(
				quote(Result.TABLE_NAME),
				new String[]{
						quote(Result.FIELD_TIMESTAMP),
						quote(Result.FIELD_VALUE),
				},
				quote(Result.FIELD_SCALE_ID)+"=?",
				new String[]{
						this._id+""
				},
				null,
				null,
				null,
				null
		);
	}
	
	public Cursor getUniqueScalesCursor() {
		return this.getDBAdapter().getDatabase().query(
				quote(Scale.TABLE_NAME), 
				new String[] {
						"MIN("+quote(Scale.FIELD_ID)+") "+ quote(Scale.FIELD_ID),
						"MIN("+quote(Scale.FIELD_GROUP_ID)+") "+ quote(Scale.FIELD_GROUP_ID),
						"MIN("+quote(Scale.FIELD_MAX_LABEL)+") "+ quote(Scale.FIELD_MAX_LABEL),
						"MIN("+quote(Scale.FIELD_MIN_LABEL)+") "+ quote(Scale.FIELD_MIN_LABEL),
						"MIN("+quote(Scale.FIELD_WEIGHT)+") "+ quote(Scale.FIELD_WEIGHT)
				},
				null, 
				null, 
				quote(Scale.FIELD_MIN_LABEL)+" || "+quote(Scale.FIELD_MAX_LABEL), 
				null, 
				quote(Scale.FIELD_MIN_LABEL)+" || "+quote(Scale.FIELD_MAX_LABEL)
		);
	}
}

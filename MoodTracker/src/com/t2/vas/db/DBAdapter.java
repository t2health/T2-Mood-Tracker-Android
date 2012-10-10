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
package com.t2.vas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {
	private Context context;
	private SQLiteDatabase database;
	private OnDatabaseCreatedListener createListener;
	private OnDatabaseUpdatedListener onUpgradeListner;
	
	public DBAdapter(Context c, String dbName, int dbVersion) {
		super(c, dbName, null, dbVersion);
		this.context = c;
		this.init();
	}
	
	private void init() {
		
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public SQLiteDatabase getDatabase() {
		return this.database;
	}
	
	public DBAdapter open() {
		this.database = this.getWritableDatabase();
		return this;
	}

	public boolean isOpen() {
		if(this.database == null) {
			return false;
		}
		return this.database.isOpen();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.database = db;
		
		if(this.createListener != null) {
			this.createListener.onDatabaseCreated(db);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(this.onUpgradeListner != null) {
			this.onUpgradeListner.onUpgrade(db, oldVersion, newVersion);
		}
	}
	
	public static ContentValues buildContentValues(String[] keys, String[] values) {
		ContentValues v = new ContentValues();
		for(int i = 0; i < keys.length; i++) {
			v.put(keys[i], values[i]);
		}
		return v;
	}
	
	public void setOnCreateListener(OnDatabaseCreatedListener l) {
		this.createListener = l;
	}
	
	public void setOnUpdatedListener(OnDatabaseUpdatedListener l) {
		this.onUpgradeListner = l;
	}
	
	public interface OnDatabaseCreatedListener {
		public void onDatabaseCreated(SQLiteDatabase db);
	}
	
	public interface OnDatabaseUpdatedListener {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
	
	//Steveo
	public Cursor GetRawCursor(String sql)
	{
		return this.database.rawQuery(sql, null);
	}
}

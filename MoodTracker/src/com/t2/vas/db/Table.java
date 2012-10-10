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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class Table extends AbsTable {
	public static final String FIELD_ID = "_id";
	
	public long _id;

	public Table(DBAdapter d) {
		super(d);
	}

	@Override
	public boolean delete() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put(FIELD_ID, this._id);

		return this.delete(whereConditions) > 0;
	}

	@Override
	public boolean load() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put(FIELD_ID, this._id);

		Cursor c = this.select(whereConditions);
		if(!c.moveToNext()) {
			c.close();
			return false;
		}
		boolean res = this.load(c);
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		c.close();

		return res;
	}

	@Override
	public boolean save() {
		if(this._id > 0) {
			this.update();
		} else {
			this._id = this.insert();
		}
		return this.load();
	}
	
	public void empty() {
		this.dbAdapter.getDatabase().execSQL("DELETE FROM `"+ this.getTableName()+"`");
	}
	
	public boolean update(ContentValues values) {
		ContentValues whereConditions = new ContentValues();
		return this.update(values, whereConditions) > 0;
	}
	
	public int update(ContentValues values, ContentValues whereConditions) {
		this.openForThis();
		
		//whereConditions.put(quote(FIELD_ID), values.getAsLong(FIELD_ID));
		whereConditions.put(quote(FIELD_ID), this._id);
		
		QueryComponents qc = QueryComponents.factory(whereConditions);
		int i = this.dbAdapter.getDatabase().update("`"+this.getTableName()+"`", values, qc.whereClause, qc.whereArgs);
		
		this.closeForThis();
		return i;
	}

	@Override
	public abstract String getTableName();

	public abstract boolean load(Cursor c);

	@Override
	public abstract long insert();

	@Override
	public abstract boolean update();

	@Override
	public abstract void onCreate(SQLiteDatabase database);

	@Override
	public abstract void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
}

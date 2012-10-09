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

public class Result extends Table {
	public static final String TABLE_NAME = "result";
	public static final String FIELD_GROUP_ID = "group_id";
	public static final String FIELD_SCALE_ID = "scale_id";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_VALUE = "value";
	
	public long group_id;
	public long scale_id;
	public long timestamp;
	public int value;

	public Result(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "result";
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(
				"CREATE TABLE "+ quote(TABLE_NAME) +" (" +
						quote(FIELD_ID)+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
						quote(FIELD_GROUP_ID)+" INTEGER NOT NULL, " +
						quote(FIELD_SCALE_ID)+" INTEGER NOT NULL, " +
						quote(FIELD_TIMESTAMP)+" INTEGER NOT NULL, " +
						quote(FIELD_VALUE)+" INTEGER NOT NULL" +
				")"
		);
		
		// Create the index
		database.execSQL(
				"CREATE INDEX group_id_timestamp_index ON "+ quote(TABLE_NAME) +"("+ quote(FIELD_GROUP_ID) +", "+ quote(FIELD_TIMESTAMP) +")"
		);
		database.execSQL(
				"CREATE INDEX result_scale_id_timestamp_index ON "+ quote(TABLE_NAME) +"("+ quote(FIELD_SCALE_ID) +", "+ quote(FIELD_TIMESTAMP) +")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(newVersion == 3) {
			database.execSQL("DROP INDEX group_id_index");
			database.execSQL("DROP INDEX result_scale_id_index");
			database.execSQL("DROP INDEX result_timestamp_index");
			
			database.execSQL(
					"CREATE INDEX group_id_timestamp_index ON "+ quote(TABLE_NAME) +"("+ quote(FIELD_GROUP_ID) +", "+ quote(FIELD_TIMESTAMP) +")"
			);
			database.execSQL(
					"CREATE INDEX result_scale_id_timestamp_index ON "+ quote(TABLE_NAME) +"("+ quote(FIELD_SCALE_ID) +", "+ quote(FIELD_TIMESTAMP) +")"
			);
		}
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_GROUP_ID), this.group_id);
		v.put(quote(FIELD_SCALE_ID), this.scale_id);
		v.put(quote(FIELD_TIMESTAMP), this.timestamp);
		v.put(quote(FIELD_VALUE), this.value);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		this.group_id = c.getLong(c.getColumnIndex(FIELD_GROUP_ID));
		this.scale_id = c.getLong(c.getColumnIndex(FIELD_SCALE_ID));
		this.timestamp = c.getLong(c.getColumnIndex(FIELD_TIMESTAMP));
		this.value = c.getInt(c.getColumnIndex(FIELD_VALUE));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_GROUP_ID), this.group_id);
		v.put(quote(FIELD_SCALE_ID), this.scale_id);
		v.put(quote(FIELD_TIMESTAMP), this.timestamp);
		v.put(quote(FIELD_VALUE), this.value);

		return this.update(v);
	}
}

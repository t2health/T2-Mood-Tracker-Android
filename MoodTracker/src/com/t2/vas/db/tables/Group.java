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

public class Group extends Table {
	public static final String TABLE_NAME = "group";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_IMMUTABLE = "immutable";
	public static final String FIELD_INVERSE_RESULTS = "inverse_results";
	
	public String title;
	public int immutable = 0;
	public boolean inverseResults = false;

	public Group(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		this.dbAdapter.getDatabase().execSQL(
				"CREATE TABLE "+ quote(Group.TABLE_NAME) +"(" +
						quote(FIELD_ID)+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
						quote(FIELD_TITLE)+" TEXT NOT NULL, " +
						quote(FIELD_IMMUTABLE)+" INTEGER NOT NULL, "+ 
						quote(FIELD_INVERSE_RESULTS)+" INTEGER NOT NULL" +
				")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(newVersion == 3) {
			database.execSQL("ALTER TABLE "+ quote(Group.TABLE_NAME) +" ADD COLUMN "+ FIELD_INVERSE_RESULTS +" INTEGER DEFAULT 0");
			database.execSQL("UPDATE "+ quote(Group.TABLE_NAME) +" SET "+ FIELD_INVERSE_RESULTS +"=1 WHERE "+ quote(FIELD_TITLE) +" LIKE('General Well-Being')");
		}
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_TITLE), this.title);
		v.put(quote(FIELD_IMMUTABLE), this.immutable);
		v.put(quote(FIELD_INVERSE_RESULTS), this.inverseResults?1:0);
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		this.title = c.getString(c.getColumnIndex(FIELD_TITLE));
		this.immutable = c.getInt(c.getColumnIndex(FIELD_IMMUTABLE));
		this.inverseResults = c.getInt(c.getColumnIndex(FIELD_INVERSE_RESULTS))>0?true:false;
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_TITLE), this.title);
		v.put(quote(FIELD_IMMUTABLE), this.immutable);
		v.put(quote(FIELD_INVERSE_RESULTS), this.inverseResults?1:0);
		return this.update(v);
	}

	@Override
	public boolean delete() {
		ContentValues cv;

		Scale s = new Scale(this.dbAdapter);
		cv = new ContentValues();
		cv.put(quote(Scale.FIELD_GROUP_ID), this._id);
		s.delete(cv);

		Result r = new Result(dbAdapter);
		cv = new ContentValues();
		cv.put(quote(Result.FIELD_GROUP_ID), this._id);
		r.delete(cv);

		return super.delete();
	}

	public Cursor getGroupsWithScalesCursor() {
		return this.dbAdapter.getDatabase().query(
				quote(Group.TABLE_NAME), 
				null, 
				quote(Group.FIELD_ID) +" IN(SELECT DISTINCT("+quote(Scale.FIELD_GROUP_ID)+") FROM "+ quote(Scale.TABLE_NAME) +")", 
				null, 
				null, 
				null, 
				quote(Group.FIELD_TITLE)
		);
	}
	
	public Cursor getGroupsCursor() {
		ContentValues v = new ContentValues();
		return this.select(
				v,
				quote(Group.FIELD_TITLE)+" ASC"
		);
	}
	
	public ArrayList<Group> getGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getGroupsCursor();

		while(c.moveToNext()) {
			Group group = new Group(this.dbAdapter);
			group.load(c);
			groups.add(group);
		}
		c.close();

		return groups;
	}

	public Cursor getScalesCursor() {
		ContentValues v = new ContentValues();
		v.put(quote(Scale.FIELD_GROUP_ID), this._id+"");
		return new Scale(this.dbAdapter).select(v, quote(Scale.FIELD_WEIGHT)+" ASC");
	}
	
	public ArrayList<Scale> getScales() {
		ArrayList<Scale> scales = new ArrayList<Scale>();
		Cursor c = this.getScalesCursor();
		while(c.moveToNext()) {
			Scale scale = new Scale(this.dbAdapter);
			scale.load(c);

			scales.add(scale);
		}
		c.close();

		return scales;
	}

	public int getResultsCount() {
		Cursor c = this.getDBAdapter().getDatabase().query(
				quote(Result.TABLE_NAME),
				new String[]{
						"COUNT(*) cnt",
				},
				quote(Result.FIELD_GROUP_ID)+"=?",
				new String[]{
						this._id+"",
				},
				null,
				null,
				null,
				null
		);
		
		int cnt = 0;
		if(c.moveToFirst()) {
			cnt = c.getInt(c.getColumnIndex("cnt"));
		}
		c.close();
		return cnt;
	}
	
	public void clearResults() {
		this.dbAdapter.getDatabase().delete(
				quote(Result.TABLE_NAME),
				quote(Result.FIELD_GROUP_ID)+"=?",
				new String[] {
					this._id+"",
				}
		);
	}
	
	public Cursor getResults(long startTime, long endTime) {
		return this.getDBAdapter().getDatabase().query(
				quote(Result.TABLE_NAME),
				new String[]{
						quote(Result.FIELD_TIMESTAMP),
						quote(Result.FIELD_VALUE),
				},
				quote(Result.FIELD_GROUP_ID)+"=? AND "+ quote(Result.FIELD_TIMESTAMP)+" >= ? AND "+ quote(Result.FIELD_TIMESTAMP)+" < ?",
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
		return this.getDBAdapter().getDatabase().query(
				quote(Result.TABLE_NAME),
				new String[]{
						quote(Result.FIELD_TIMESTAMP),
						quote(Result.FIELD_VALUE),
				},
				quote(Result.FIELD_GROUP_ID)+"=?",
				new String[]{
						this._id+""
				},
				null,
				null,
				null,
				null
		);
	}
	
	public long getIdByName(String name) {
		Cursor c = this.getDBAdapter().getDatabase().query(
				quote(Group.TABLE_NAME), 
				new String[] {
					"_id",
				}, 
				quote(Group.FIELD_TITLE)+"=?", 
				new String[] {
					name
				}, 
				null, 
				null, 
				null
		);
		if(!c.moveToFirst()) {
			c.close();
			return -1;
		}
		
		if(c.getCount() > 1) {
			c.close();
			return -1;
		}
		
		long id = c.getLong(0);
		c.close();
		return id;
	}
	
	public long getScaleId(String minLabel, String maxLabel) {
		Cursor c = this.dbAdapter.getDatabase().query(
				quote(Scale.TABLE_NAME), 
				new String[] {
					quote(Scale.FIELD_ID)
				}, 
				quote(Scale.FIELD_GROUP_ID)+"=? AND "+ quote(Scale.FIELD_MIN_LABEL)+"=? AND "+ quote(Scale.FIELD_MAX_LABEL)+"=?", 
				new String[] {
					this._id+"",
					minLabel,
					maxLabel,
				}, 
				null, 
				null, 
				null
		);
		
		if(!c.moveToFirst()) {
			c.close();
			return -1;
		}
		
		if(c.getCount() > 1) {
			c.close();
			return -1;
		}
		
		long id = c.getLong(0);
		c.close();
		return id;
	}
	
	public int getScalesCount() {
		Cursor c = this.dbAdapter.getDatabase().query(
				quote(Scale.TABLE_NAME), 
				new String[] {
					"COUNT(*)",
				}, 
				quote(Scale.FIELD_GROUP_ID)+"=?", 
				new String[] {
					this._id+""
				}, 
				null, 
				null, 
				null
		);
		c.moveToFirst();
		int cnt = c.getInt(0);
		c.close();
		return cnt;
	}
	
	public boolean resultExists(long timestamp, int value) {
		Cursor c = this.dbAdapter.getDatabase().query(
				quote(Result.TABLE_NAME), 
				new String[] {
					"COUNT(*)",
				}, 
				quote(Result.FIELD_GROUP_ID)+"=? AND "+ quote(Result.FIELD_TIMESTAMP) +"=? AND "+ quote(Result.FIELD_VALUE)+"=?" , 
				new String[] {
					this._id+"",
					timestamp+"",
					value+"",
				}, 
				null, 
				null, 
				null
		);
		c.moveToFirst();
		int cnt = c.getInt(0);
		c.close();
		return cnt > 0;
	}
}

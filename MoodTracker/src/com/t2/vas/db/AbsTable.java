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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbsTable {
	protected DBAdapter dbAdapter;
	protected HashMap<String, String> metaData = new HashMap<String, String>();
	private boolean openForThis = false; 
	
	public AbsTable(DBAdapter d) {
		this.setDBAdapter(d);
	}

	public abstract String getTableName();
	public abstract void onCreate(SQLiteDatabase database);
	public abstract void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);
	
	public void onDrop() {
		this.dbAdapter.getDatabase().execSQL("DROP TABLE IF EXISTS`"+this.getTableName()+"`");
	}

	public void setDBAdapter(DBAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	public DBAdapter getDBAdapter() {
		return dbAdapter;
	}
	
	protected static String quote(String s) {
		return "`"+s+"`";
	}
	
	protected void openForThis() {
		if(!this.dbAdapter.isOpen()) {
			openForThis = true;
			this.dbAdapter.open();
		}
	}
	
	protected void closeForThis() {
		if(this.openForThis) {
			this.dbAdapter.close();
			this.openForThis = false;
		}
	}
	
	public long insert(ContentValues v) {
		this.openForThis();
		
		long l = this.dbAdapter.getDatabase().insert("`"+this.getTableName()+"`", null, v);
		
		this.closeForThis();
		return l;
	}
	
	public long delete(ContentValues whereConditions) {
		QueryComponents qc = QueryComponents.factory(whereConditions);
		return this.delete(qc.whereClause, qc.whereArgs);
	}
	
	public long delete(String whereClause, String[] whereArgs) {
		this.openForThis();
		
		long l = this.dbAdapter.getDatabase().delete("`"+this.getTableName()+"`", whereClause, whereArgs);
		
		this.closeForThis();
		return l;
	}
	
	public Cursor selectNewest(String columnName) {
		Cursor c = this.select(null, null, null, null, columnName+" DESC", "1");
		c.moveToNext();
		return c;
	}
	
	public Cursor select(ContentValues whereConditions) {
		QueryComponents qc = QueryComponents.factory(whereConditions);
		return this.select(qc.whereClause, qc.whereArgs, null, null, null, null);
	}
	
	public Cursor select(ContentValues whereConditions, String orderBy) {
		QueryComponents qc = QueryComponents.factory(whereConditions);
		return this.select(qc.whereClause, qc.whereArgs, null, null, orderBy, null);
	}
	
	public Cursor select(String whereClause, String[] whereArgs) {
		return this.select(whereClause, whereArgs, null, null, null, null);
	}
	
	public Cursor select(String whereClause, String[] whereArgs, String orderBy) {
		return this.select(whereClause, whereArgs, null, null, orderBy, null);
	}
	
	public Cursor select(String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) {
		this.openForThis();
		
		Cursor c = this.dbAdapter.getDatabase().query("`"+this.getTableName()+"`", null, whereClause, whereArgs, groupBy, having, orderBy, limit);
		
		//this.closeForThis();
		return c;
	}
	
	
	public abstract boolean save();
	public abstract boolean load();
	public abstract long insert();
	public abstract boolean update();
	public abstract boolean delete();
	
	protected static class QueryComponents {
		public String whereClause = "";
		public String[] whereArgs;
		
		private QueryComponents(String wc, String[] wa) {
			this.whereClause = wc;
			this.whereArgs = wa;
		}
		
		public static QueryComponents factory(ContentValues v) {
			if(v == null) {
				return new QueryComponents(null, null);
			}
			
			Set<Entry<String,Object>> s = v.valueSet();
			
			ArrayList<String> where = new ArrayList<String>();
			ArrayList<String> values = new ArrayList<String>();
			for(Entry<String,Object> e: s) {
				where.add(e.getKey()+"=?");
				values.add(e.getValue().toString());
			}
			
			String where_st = "";
			for(int i = 0; i < where.size(); i++) {
				where_st += where.get(i)+" AND ";
			}
			if(where_st.length() > 4) {
				where_st = where_st.substring(0, where_st.length() - 4);
			}
			
			String[] whereArgs = values.toArray(new String[values.size()]);
			
			return new QueryComponents(where_st, whereArgs);
		}
	}
	
	public void setMetaData(String name, String value) {
		this.metaData.put(name, value);
	}
	
	public String getMetaData(String name) {
		return this.metaData.get(name);
	}
}

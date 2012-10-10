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
package com.t2.vas.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import android.database.Cursor;
import android.text.format.DateFormat;

import com.t2.vas.activity.DataPoint;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

public abstract class DataProvider {
	protected DBAdapter dbAdapter;

	public DataProvider(DBAdapter db) {
		this.dbAdapter = db;
	}
	
	public abstract LinkedHashMap<Long,Double> getData(long id, long startTime, long endTime);
	
	//Added to fix charting error - Steveody (no longer needed with new charts)
	/*public double GetGroupAverage(long id, long startTime, long endTime)
	{
		double total = 0.0;
		int count = 0;
		
		Group group = new Group(dbAdapter);
		group._id = id;
		
		Cursor c = group.getResults(startTime, endTime);
		while(c.moveToNext()) {
			total += c.getDouble(c.getColumnIndex("value"));
			count++;
		}
		c.close();
		
		return total/count;
	}*/
	
	public ArrayList<DataPoint> getGroupData(long id, long startTime, long endTime) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();
		Group group = new Group(dbAdapter);
		group._id = id;
		
		Cursor c = group.getResults(startTime, endTime);
		ArrayList<String> dpdates = new ArrayList<String>();
		while(c.moveToNext()) {
			if(!dpdates.contains(DateFormat.format("MM/dd/yy mm", c.getLong(c.getColumnIndex("timestamp"))).toString()))
			{
				dpdates.add(DateFormat.format("MM/dd/yy mm", c.getLong(c.getColumnIndex("timestamp"))).toString());
				data.add(new DataPoint(c.getLong(c.getColumnIndex("timestamp")),c.getDouble(c.getColumnIndex("value"))));
			}
			else
			{
				data.get(dpdates.indexOf(DateFormat.format("MM/dd/yy mm", c.getLong(c.getColumnIndex("timestamp"))).toString())).addValue(c.getDouble(c.getColumnIndex("value")));
				//Log.v("dataprovider", "added value to current");
			}
			
		}
		c.close();
		
		return data;
	}
	
	public ArrayList<DataPoint> getAllGroupData(long id) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();
		Group group = new Group(dbAdapter);
		group._id = id;
		
		Cursor c = group.getAllResults();
		ArrayList<String> dpdates = new ArrayList<String>();
		while(c.moveToNext()) {
			if(!dpdates.contains(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString()))
			{
				dpdates.add(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString());
				data.add(new DataPoint(c.getLong(c.getColumnIndex("timestamp")),c.getDouble(c.getColumnIndex("value"))));
			}
			else
			{
				data.get(dpdates.indexOf(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString())).addValue(c.getDouble(c.getColumnIndex("value")));
				//Log.v("dataprovider", "added value to current");
			}
			
		}
		c.close();
		
		return data;
	}
	
	public ArrayList<DataPoint> getScaleData(long id, long startTime, long endTime) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();
		Scale scale = new Scale(dbAdapter);
		scale._id = id;
		
		Cursor c = scale.getResults(startTime, endTime);
		ArrayList<String> dpdates = new ArrayList<String>();
		while(c.moveToNext()) {
			
			//if(!dpdates.contains(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString()))
			{
				dpdates.add(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString());
				data.add(new DataPoint(c.getLong(c.getColumnIndex("timestamp")),c.getDouble(c.getColumnIndex("value"))));
				//Log.v("dataprovider", "added new dp");
			}
			//else
			//{
			//	data.get(dpdates.indexOf(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString())).addValue(c.getDouble(c.getColumnIndex("value")));
			//	Log.v("dataprovider", "added value to current");
			//}
		}
		c.close();
		
		Collections.sort(data);
		
		return data;
	}
	
	public ArrayList<DataPoint> getAllScaleData(long id) {
		ArrayList<DataPoint> data = new ArrayList<DataPoint>();
		Scale scale = new Scale(dbAdapter);
		scale._id = id;
		
		Cursor c = scale.getAllResults();
		ArrayList<String> dpdates = new ArrayList<String>();
		while(c.moveToNext()) {
			
			//if(!dpdates.contains(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString()))
			{
				dpdates.add(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString());
				data.add(new DataPoint(c.getLong(c.getColumnIndex("timestamp")),c.getDouble(c.getColumnIndex("value"))));
				//Log.v("dataprovider", "added new dp");
			}
			//else
			//{
			//	data.get(dpdates.indexOf(DateFormat.format("MM/dd/yy", c.getLong(c.getColumnIndex("timestamp"))).toString())).addValue(c.getDouble(c.getColumnIndex("value")));
			//	Log.v("dataprovider", "added value to current");
			//}
		}
		c.close();
		
		Collections.sort(data);
		
		return data;
	}

}

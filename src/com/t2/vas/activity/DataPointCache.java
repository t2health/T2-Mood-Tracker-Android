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
package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.HashMap;

public class DataPointCache {
	private HashMap<String,DataPointCacheEntry> cache = new HashMap<String,DataPointCacheEntry>();
	
	public ArrayList<DataPoint> getCache(String key, long startTime, long endTime, int calendarGroupByField) {
		DataPointCacheEntry c = cache.get(key);
		if(c == null) {
			return null;
		}
		
		if(c.isCacheExpired(startTime, endTime, calendarGroupByField)) {
			cache.remove(key);
			return null;
		}
		
		return c.data;
	}
	
	public void setCache(String key, ArrayList<DataPoint> data, long startTime, long endTime, int calendarGroupByField) {
		cache.remove(key);
		cache.put(key, new DataPointCacheEntry(
				data,
				startTime,
				endTime,
				calendarGroupByField
		));
	}
	
	public void clearCache(String key) {
		cache.remove(key);
	}
	
	private static class DataPointCacheEntry {
		public long startTime;
		public long endTime;
		public int calendarGroupByField;
		public ArrayList<DataPoint> data;
		
		public DataPointCacheEntry(ArrayList<DataPoint> data, long startTime, long endTime, int calendarGroupByField) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.calendarGroupByField = calendarGroupByField;
			this.data = data;
		}
		
		public boolean isCacheExpired(long startTime, long endTime, int calendarGroupByField) {
			return !(this.startTime == startTime && this.endTime == endTime && this.calendarGroupByField == calendarGroupByField);
		}
	}
}

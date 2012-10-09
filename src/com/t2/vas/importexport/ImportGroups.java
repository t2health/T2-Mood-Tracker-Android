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
package com.t2.vas.importexport;

import java.util.ArrayList;
import java.util.HashMap;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class ImportGroups {
	public Group group;
	public ArrayList<Scale> scales = new ArrayList<Scale>();
	public HashMap<Scale,ArrayList<Result>> results = new HashMap<Scale,ArrayList<Result>>();
	
	public int scaleCount;
	public int resultCount;
	public long resultMinTimestamp;
	public long resultMaxTimestamp;
	
	public boolean isGroupExists(DBAdapter adapter) {
		return true;
	}
	
	public boolean isScaleExists(DBAdapter adapter, Scale scale) {
		return true;
	}
	
	public boolean isResultExists(DBAdapter adapter, Result result) {
		return true;
	}
}
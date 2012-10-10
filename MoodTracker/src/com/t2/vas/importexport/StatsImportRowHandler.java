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

import com.t2.vas.importexport.ImportExport.ImportRowHandler;

class StatsImportRowHandler implements ImportRowHandler {
	public ImportFileStats fileStats;
	private GroupStat currentGroupStat;
	private ScaleStat currentScaleStat;
	
	public StatsImportRowHandler() {
		reset();
	}
	
	public void reset() {
		fileStats = new ImportFileStats();
	}

	@Override
	public void onGroupFound(String name, boolean inverseResults) {
		currentGroupStat = new GroupStat();
		currentGroupStat.title = name;
		fileStats.addGroup(currentGroupStat);
	}

	@Override
	public void onScaleFound(String minLabel, String maxLabel) {
		currentScaleStat = new ScaleStat();
		currentScaleStat.minLabel = minLabel;
		currentScaleStat.maxLabel = maxLabel;
		currentGroupStat.addScale(currentScaleStat);
	}

	@Override
	public void onResultFound(long timestamp, int value) {
		currentGroupStat.addResult(timestamp, value);
		currentScaleStat.addResult(timestamp, value);
	}

	@Override
	public void onNoteFound(long timestamp, String note) {
		fileStats.addNote(timestamp);
	}
}

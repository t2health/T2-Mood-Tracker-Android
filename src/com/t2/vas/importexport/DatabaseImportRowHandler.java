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

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.importexport.ImportExport.ImportRowHandler;

class DatabaseImportRowHandler implements ImportRowHandler {
	private DBAdapter dbAdapter;
	private ImportFileStats fileStats;
	
	private boolean mergeGroup = false;
	private Group currentGroup;
	private Scale currentScale;
	private Result currentResult;
	private Note currentNote;
	private ArrayList<String> importNames;
	private boolean importNotes;
	private long startTime;
	private long endTime;
	private boolean importCurrentGroupData = true;
	
	public DatabaseImportRowHandler(DBAdapter dbAdapter, ImportFileStats stats, ArrayList<String> importGroupNames, boolean importNotes, long startTime, long endTime) {
		this.dbAdapter = dbAdapter;
		this.fileStats = stats;
		this.importNames = importGroupNames;
		this.importNotes = importNotes;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public void onGroupFound(String name, boolean inverseResults) {
		this.importCurrentGroupData = importNames.contains(name);
		
		mergeGroup = false;
		currentScale = null;
		currentResult = null;
		currentGroup = new Group(this.dbAdapter);
		
		// Group exists, see if the scales match, if so, merge the data.
		long groupId = currentGroup.getIdByName(name);
		mergeGroup = shouldMerge(fileStats, dbAdapter, name);
		
		// if we are merging, load the group
		if(mergeGroup) {
			currentGroup = new Group(this.dbAdapter);
			currentGroup._id = groupId;
			currentGroup.load();
		// create the new group.
		} else {
			currentGroup = new Group(this.dbAdapter);
			currentGroup.title = name +" (import)";
			currentGroup.inverseResults = inverseResults;
			
			if(importCurrentGroupData) {
				currentGroup.save();
			}
		}
	}

	@Override
	public void onScaleFound(String minLabel, String maxLabel) {
		currentResult = null;
		
		// if we are merging, grab the scale, if it exists.
		currentScale = new Scale(this.dbAdapter);
		if(mergeGroup) {
			currentScale._id = currentGroup.getScaleId(minLabel, maxLabel);
			currentScale.load();
		}
		
		// This scale doesn't exist, create it.
		if(currentScale._id <= 0) {
			currentScale.group_id = currentGroup._id;
			currentScale.min_label = minLabel;
			currentScale.max_label = maxLabel;
			
			if(importCurrentGroupData) {
				currentScale.save();
			}
		}
	}

	@Override
	public void onResultFound(long timestamp, int value) {
		// don't work with this, it is out of the time bounds.
		if(timestamp < this.startTime || timestamp > this.endTime) {
			return;
		}
		
		// Don't import this result it is part of a group not selected for import.
		if(!importCurrentGroupData) {
			return;
		}
		
		// Only check if the result exists when merging data.
		if(mergeGroup) {
			if(!currentGroup.resultExists(timestamp, value)) {
				currentResult = new Result(this.dbAdapter);
				currentResult.group_id = currentGroup._id;
				currentResult.scale_id = currentScale._id;
				currentResult.timestamp = timestamp;
				currentResult.value = value;
				currentResult.save();
			}	
		// just add the result, don't check if it exists.
		} else {
			currentResult = new Result(this.dbAdapter);
			currentResult.group_id = currentGroup._id;
			currentResult.scale_id = currentScale._id;
			currentResult.timestamp = timestamp;
			currentResult.value = value;
			currentResult.save();
		}
	}

	@Override
	public void onNoteFound(long timestamp, String note) {
		// don't work with this, it is out of the time bounds.
		if(timestamp < this.startTime || timestamp > this.endTime) {
			return;
		}
		
		if(!importNotes) {
			return;
		}
		
		currentNote = new Note(this.dbAdapter);
		if(!currentNote.exists(timestamp, note)) {
			currentNote.timestamp = timestamp;
			currentNote.note = note;
			currentNote.save();
		}
	}
	
	public static boolean shouldMerge(ImportFileStats fileStats, DBAdapter dbAdapter, String groupTitle) {
		Group currentGroup = new Group(dbAdapter);
		long groupId = currentGroup.getIdByName(groupTitle);
		if(groupId > 0) {
			currentGroup._id = groupId;
			
			GroupStat gs = fileStats.getGroupStat(groupTitle);
			ScaleStat ss;
			long scaleId = -1;
			int matchCount = 0;
			for(int i = 0; i < gs.scales.size(); ++i) {
				ss = gs.scales.get(i);
				scaleId = currentGroup.getScaleId(ss.minLabel, ss.maxLabel);
				if(scaleId > 0) {
					matchCount++;
				}
			}
			
			if(matchCount <= currentGroup.getScalesCount()) {
				return true;
			}
		}
		return false;
	}
}

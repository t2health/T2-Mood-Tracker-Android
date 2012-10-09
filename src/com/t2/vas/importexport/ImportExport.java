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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Scale;

public class ImportExport {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String group_row_prefix = "group";
	private static final String scale_row_prefix = "scale";
	private static final String result_row_prefix = "result";
	private static final String note_row_prefix = "note";
	
	private static StatsImportRowHandler statsImportRowHandler = new StatsImportRowHandler();
	
	public static void exportGroupData(DBAdapter dbAdapter, File file, boolean append, List<Long> groupIds, long startTime, long endTime) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file, append));
			Group group;
			for(int i = 0; i < groupIds.size(); ++i) {
				group = new Group(dbAdapter);
				group._id = groupIds.get(i);
				group.load();
				
				// print the title
				writer.writeNext(new String[]{
						group_row_prefix,
						group.title,
						group.inverseResults+"",
				});
				
				Cursor cursor;
				Scale scale;
				ArrayList<Scale> scales = group.getScales();
				for(int j = 0; j < scales.size(); ++j) {
					scale = scales.get(j);
					
					// print the max/min scales
					writer.writeNext(new String[] {
							scale_row_prefix,
							scale.min_label,
							scale.max_label,
					});
					
					// print the values.
					cursor = scale.getResults(startTime, endTime);
					int timeIndex = cursor.getColumnIndex("timestamp");
					int valueIndex = cursor.getColumnIndex("value");
					while(cursor.moveToNext()) {
						writer.writeNext(new String[]{
								result_row_prefix,
								dateFormat.format(new Date(cursor.getLong(timeIndex))),
								cursor.getInt(valueIndex)+"",
						});
					}
					
					cursor.close();
				}
			}
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void exportNotesData(DBAdapter dbAdapter, File file, boolean append, long startTime, long endTime) {
		try {
			CSVWriter writer = new CSVWriter(new FileWriter(file, append));
			Note note = new Note(dbAdapter);
			
			Cursor cursor = note.getNotesCursor(startTime, endTime);
			int timestampIndex = cursor.getColumnIndex(Note.FIELD_TIMESTAMP);
			int noteIndex = cursor.getColumnIndex(Note.FIELD_NOTE);
			while(cursor.moveToNext()) {
				writer.writeNext(new String[] {
						note_row_prefix,
						dateFormat.format(new Date(cursor.getLong(timestampIndex))),
						cursor.getString(noteIndex),
				});
			}
			cursor.close();
			
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void importData(File file, DBAdapter dbAdapter, ImportFileStats fileStats, ArrayList<String> importGroupNames, boolean importNotes, long startTime, long endTime) {
		if(!file.exists()) {
			return;
		}
		
		parseFile(
				file, 
				new DatabaseImportRowHandler(dbAdapter, fileStats, importGroupNames, importNotes, startTime, endTime)
		);
	}
	
	public static ImportFileStats getFileStats(File file) {
		if(!file.exists()) {
			return null;
		}

		statsImportRowHandler.reset();
		boolean parsed = parseFile(
				file, 
				statsImportRowHandler
		);
		
		if(parsed) {
			return statsImportRowHandler.fileStats;
		}
		return null;
	}
	
	private static boolean parseFile(File file, ImportRowHandler rowHandler) {
		if(!file.exists()) {
			return false;
		}
		
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			
			int rowLength = 0;
			boolean inGroup = false;
			boolean inScale = false;
			String firstField = null;
			String[] row = null;
			while((row = reader.readNext()) != null) {
				rowLength = row.length;
				
				if(rowLength <= 0) {
					continue;
				}
				
				firstField = row[0];
				
				if(firstField.equals(group_row_prefix)) {
					inGroup = true;
					boolean inverse = Boolean.parseBoolean(row[2]);
					rowHandler.onGroupFound(row[1], inverse);
					
				} else if(inGroup && firstField.equals(scale_row_prefix)) {
					inScale = true;
					rowHandler.onScaleFound(row[1], row[2]);
					
				} else if(inGroup && inScale && firstField.equals(result_row_prefix)) {
					try {;
						rowHandler.onResultFound(
								dateFormat.parse(row[1]).getTime(),
								Integer.parseInt(row[2])
						);
					} catch (NumberFormatException e) {
					} catch (ParseException e) {}
					
				} else if(firstField.equals(note_row_prefix)) {
					try {
						rowHandler.onNoteFound(
								dateFormat.parse(row[1]).getTime(),
								row[2]
						);
					} catch (NumberFormatException e) {
					} catch (ParseException e) {}
				}
			}
			reader.close();
			return true;
			
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		return false;
	}
	
	public interface ImportRowHandler {
		public void onGroupFound(String name, boolean inverseResults);
		public void onScaleFound(String minLabel, String maxLabel);
		public void onResultFound(long timestamp, int value);
		public void onNoteFound(long timestamp, String note);
	}
}

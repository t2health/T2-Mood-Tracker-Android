/*
 * 
 */
package com.t2.vas.data;

import java.util.LinkedHashMap;

import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;

public class NotesDataProvider extends DataProvider {

	public NotesDataProvider(DBAdapter db) {
		super(db);
	}

	@Override
	public LinkedHashMap<Long, Double> getData(long id, long startTime,
			long endTime) {
		LinkedHashMap<Long, Double> data = new LinkedHashMap<Long, Double>();
		Note note = new Note(dbAdapter);
		Cursor cursor = note.getNotesCursor(startTime, endTime);
		int timeIndex = cursor.getColumnIndex("timestamp");
		
		while(cursor.moveToNext()) {
			data.put(cursor.getLong(timeIndex), 100.0);
		}
		cursor.close();
		
		return data;
	}

}

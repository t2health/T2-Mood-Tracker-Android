package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Note extends Table {
	private static final String TAG = Note.class.getName();
	public long timestamp;
	public String note;

	public Note(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "note";
	}

	@Override
	public void onCreate() {
		// Create the table
		this.dbAdapter.getDatabase().execSQL("" +
				"CREATE TABLE " +
				"	note " +
				"(" +
				"	_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"	timestamp INTEGER NOT NULL, " +
				"	note TEXT NOT NULL" +
				")");

		// Create the index
		this.dbAdapter.getDatabase().execSQL("" +
				"CREATE INDEX note_timestamp_index ON note(timestamp)" +
		"");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("timestamp", this.timestamp);
		v.put("note", this.note);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.timestamp = c.getLong(c.getColumnIndex("timestamp"));
		this.note = c.getString(c.getColumnIndex("note"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("timestamp", this.timestamp);
		v.put("note", this.note);

		return this.update(v);
	}

	@Override
	public Note newInstance() {
		return new Note(this.dbAdapter);
	}

	public ArrayList<Note> getNotes(String orderBy) {
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor c = this.select(null, "timestamp DESC");

		while(c.moveToNext()) {
			Note n = (Note)this.getDBAdapter().getTable("note").newInstance();
			n.load(c);
			notes.add(n);
		}

        return notes;
	}

	public Cursor queryForNotes(long startTimestamp, long endTimestamp, String orderBy) {
		ArrayList<String> whereValues = new ArrayList<String>();
		ArrayList<String> whereConditions = new ArrayList<String>();

		if(startTimestamp >= 0) {
			whereConditions.add("timestamp >= ?");
			whereValues.add(startTimestamp+"");
		}
		if(endTimestamp >= 0) {
			whereConditions.add("timestamp < ?");
			whereValues.add(endTimestamp+"");
		}

		String[] whereValuesArray = null;
		String whereSt = null;
		if(whereConditions.size() > 0) {
			whereValuesArray = whereValues.toArray(new String[whereValues.size()]);
			whereSt = "";

			for(int i = 0; i < whereConditions.size(); i++) {
				whereSt += whereConditions.get(i)+ " AND ";
			}
			whereSt = whereSt.substring(0, whereSt.length() - 4);
		}
		/*Log.v(TAG, "w:"+whereSt);
		for(int i = 0; i < whereValuesArray.length; i++) {
			Log.v(TAG, "  v:"+whereValuesArray[i]);
		}*/

		return ((Note)dbAdapter.getTable("note")).select(
				whereSt,
				whereValuesArray,
				orderBy
		);
	}

	public long getEarliestTimestampSince(long timestamp) {
		Log.v(TAG, "Since:"+timestamp);
		Cursor c = this.getDBAdapter().getDatabase().query(
				"note",
				new String[] {
						"MIN(timestamp)",
				},
				"timestamp >= ?",
				new String[] {
						timestamp+"",
				},
				null,
				null,
				null
		);
		
		if(c.moveToFirst()) {
			long newTimestamp = c.getLong(0);
			c.close();
			Log.v(TAG, "Long:"+newTimestamp);

			return newTimestamp;
		}
		
		c.close();

		return -1;
	}
	
	public long getMostRecentTimestamp() {
		Cursor c = this.getDBAdapter().getDatabase().query(
				"note",
				new String[] {
						"MAX(timestamp)",
				},
				null,
				null,
				null,
				null,
				null
		);
		
		if(c.moveToFirst()) {
			long newTimestamp = c.getLong(0);
			c.close();

			return newTimestamp;
		}
		
		c.close();

		return -1;
	}
	
	public Cursor getNotesCursor(long startTime, long endTime) {
		return this.dbAdapter.getDatabase().query(
				"note", 
				null,
				"timestamp >= ? AND timestamp < ?",
				new String[] {
						startTime+"",
						endTime+"",
				},
				null,
				null,
				null
		);
	}
	
	public int getCount() {
		Cursor c = this.dbAdapter.getDatabase().query(
				"note", 
				new String[] {
						"COUNT(*) cnt",
				},
				null,
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
	
	public void clearAll() {
		this.getDBAdapter().getDatabase().execSQL("DELETE FROM note");
	}
}

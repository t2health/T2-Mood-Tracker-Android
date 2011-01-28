package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

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

		return new Note(this.dbAdapter).select(
				whereSt,
				whereValuesArray,
				orderBy
		);
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

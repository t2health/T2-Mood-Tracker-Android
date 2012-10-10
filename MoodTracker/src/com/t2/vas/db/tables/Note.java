/*
 * 
 */
package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Note extends Table {
	public static final String TABLE_NAME = "note";
	public static final String FIELD_TIMESTAMP = "timestamp";
	public static final String FIELD_NOTE = "note";
	
	public long timestamp;
	public String note;

	public Note(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// Create the table
		database.execSQL(
				"CREATE TABLE "+ quote(Note.TABLE_NAME) +"(" +
					quote(FIELD_ID)+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
					quote(FIELD_TIMESTAMP)+" INTEGER NOT NULL, " +
					quote(FIELD_NOTE)+" TEXT NOT NULL" +
				")"
		);

		// Create the index
		database.execSQL(
				"CREATE INDEX note_timestamp_index ON "+ quote(Note.TABLE_NAME) +"("+ quote(FIELD_TIMESTAMP) +")"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_TIMESTAMP), this.timestamp);
		v.put(quote(FIELD_NOTE), this.note);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex(FIELD_ID));
		this.timestamp = c.getLong(c.getColumnIndex(FIELD_TIMESTAMP));
		this.note = c.getString(c.getColumnIndex(FIELD_NOTE));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put(quote(FIELD_TIMESTAMP), this.timestamp);
		v.put(quote(FIELD_NOTE), this.note);

		return this.update(v);
	}

	public Cursor queryForNotes(long startTimestamp, long endTimestamp, String orderBy) {
		ArrayList<String> whereValues = new ArrayList<String>();
		ArrayList<String> whereConditions = new ArrayList<String>();

		if(startTimestamp >= 0) {
			whereConditions.add(quote(FIELD_TIMESTAMP)+" >= ?");
			whereValues.add(startTimestamp+"");
		}
		if(endTimestamp >= 0) {
			whereConditions.add(quote(FIELD_TIMESTAMP)+" < ?");
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
				quote(Note.TABLE_NAME), 
				null,
				quote(FIELD_TIMESTAMP)+" >= ? AND "+ quote(FIELD_TIMESTAMP)+" < ?",
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
				quote(Note.TABLE_NAME), 
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
		this.getDBAdapter().getDatabase().execSQL("DELETE FROM "+quote(Note.TABLE_NAME));
	}
	
	public boolean exists(long timestamp, String note) {
		Cursor c = this.dbAdapter.getDatabase().query(
				quote(Note.TABLE_NAME), 
				new String[] {
					"COUNT(*)",
				}, 
				 quote(Note.FIELD_TIMESTAMP) +"=? AND "+ quote(Note.FIELD_NOTE)+"=?" , 
				new String[] {
					timestamp+"",
					note,
				}, 
				null, 
				null, 
				null
		);
		c.moveToFirst();
		int cnt = c.getInt(0);
		c.close();
		return cnt > 0;
	}
}

package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Note extends Table {
	public long group_id;
	public long scale_id;
	public long result_id;
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
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE note (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, scale_id INTEGER NOT NULL, result_id INTEGER NOT NULL, timestamp INTEGER NOT NULL, note TEXT NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("group_id", this.group_id);
		v.put("scale_id", this.scale_id);
		v.put("result_id", this.result_id);
		v.put("timestamp", this.timestamp);
		v.put("note", this.note);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.group_id = c.getLong(c.getColumnIndex("group_id"));
		this.scale_id = c.getLong(c.getColumnIndex("scale_id"));
		this.result_id = c.getLong(c.getColumnIndex("result_id"));
		this.timestamp = c.getLong(c.getColumnIndex("timestamp"));
		this.note = c.getString(c.getColumnIndex("note"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("group_id", this.group_id);
		v.put("scale_id", this.scale_id);
		v.put("result_id", this.result_id);
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
}

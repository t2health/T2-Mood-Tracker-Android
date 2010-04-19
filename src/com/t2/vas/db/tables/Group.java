package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.AbsTable;
import com.t2.vas.db.Table;

public class Group extends Table {
	public long group_id;
	public String title;

	public Group(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "group";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE `group` (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL)");
		
		ContentValues v = new ContentValues();
		v.put("title", "How are you feeling today?");
		this.insert(v);
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		
	}
	
	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("title", this.title);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.title = c.getString(c.getColumnIndex("title"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("title", this.title);
		
		return this.update(v);
	}
	
	@Override
	public Group newInstance() {
		return new Group(this.dbAdapter);
	}
	
	
	public ArrayList<Scale> getScales() {
		ContentValues v = new ContentValues();
		v.put("group_id", this._id+"");
		
		ArrayList<Scale> scales = new ArrayList<Scale>();
		Cursor c = this.getDBAdapter().getTable("scale").select(v, "weight ASC");
		while(c.moveToNext()) {
			Scale scale = (Scale)this.getDBAdapter().getTable("scale").newInstance();
			scale.load(c);
			
			scales.add(scale);
		}
		
		return scales;
	}
}

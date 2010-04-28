package com.t2.vas.db.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class GroupReminder extends Table {
	public static final int REMIND_NEVER = 0;
	public static final int REMIND_HOURLY = 1;
	public static final int REMIND_DAILY = 2;
	public static final int REMIND_WEEKLY = 3;
	public static final int REMIND_MONTHLY = 4;
	
	public long group_id;
	public int remind_mode = REMIND_NEVER;
	
	public GroupReminder(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "groupreminder";
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("group_id", this.group_id);
		v.put("remind_mode", this.remind_mode);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.group_id = c.getLong(c.getColumnIndex("group_id"));
		this.remind_mode = c.getInt(c.getColumnIndex("remind_mode"));
		return true;
	}

	@Override
	public GroupReminder newInstance() {
		return new GroupReminder(this.dbAdapter);
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE `groupreminder` (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, remind_mode INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("group_id", this.group_id);
		v.put("remind_mode", this.remind_mode);
		
		return this.update(v);
	}

}

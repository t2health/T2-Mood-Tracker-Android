package com.t2.vas.db;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class Table extends AbsTable {
	private static final String TAG = "TABLE";
	public long _id;

	public Table(DBAdapter d) {
		super(d);
	}

	@Override
	public boolean delete() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put("_id", this._id);

		return this.delete(whereConditions) > 0;
	}

	@Override
	public boolean load() {
		ContentValues whereConditions = new ContentValues();
		whereConditions.put("_id", this._id);

		Cursor c = this.select(whereConditions);
		if(!c.moveToNext()) {
			c.close();
			return false;
		}
		boolean res = this.load(c);
		this._id = c.getLong(c.getColumnIndex("_id"));
		c.close();

		return res;
	}

	@Override
	public boolean save() {
		if(this._id > 0) {
			this.update();
		} else {
			this._id = this.insert();
		}
		return this.load();
	}

	@Override
	public abstract String getTableName();

	public abstract boolean load(Cursor c);

	@Override
	public abstract long insert();

	@Override
	public abstract boolean update();

	@Override
	public abstract void onCreate();

	@Override
	public abstract void onUpgrade(int oldVersion, int newVersion);

	public abstract Table newInstance();
}

package com.t2.vas.db.tables;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.AbsTable;
import com.t2.vas.db.Table;

public class Result extends Table {
	public long group_id;
	public long scale_id;
	public long timestamp;
	public int value;

	public Result(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "result";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE result (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, scale_id INTEGER NOT NULL, timestamp INTEGER NOT NULL, value INTEGER NOT NULL)");
		// Create the index
		this.dbAdapter.getDatabase().execSQL("" +
				"CREATE INDEX group_id_index ON result(group_id)" +
		"");
		this.dbAdapter.getDatabase().execSQL("" +
				"CREATE INDEX result_scale_id_index ON result(scale_id)" +
		"");
		this.dbAdapter.getDatabase().execSQL("" +
				"CREATE INDEX result_timestamp_index ON result(timestamp)" +
		"");

	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}


	public Cursor getNotes(long resultId) {
		ContentValues v = new ContentValues();
		v.put("result_id", resultId+"");
		return this.dbAdapter.getTable("note").select(v, "timestamp DESC");
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("group_id", this.group_id);
		v.put("scale_id", this.scale_id);
		v.put("timestamp", this.timestamp);
		v.put("value", this.value);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.group_id = c.getLong(c.getColumnIndex("group_id"));
		this.scale_id = c.getLong(c.getColumnIndex("scale_id"));
		this.timestamp = c.getLong(c.getColumnIndex("timestamp"));
		this.value = c.getInt(c.getColumnIndex("value"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("group_id", this.group_id);
		v.put("scale_id", this.scale_id);
		v.put("timestamp", this.timestamp);
		v.put("value", this.value);

		return this.update(v);
	}

	@Override
	public Result newInstance() {
		return new Result(this.dbAdapter);
	}

	public long getEarliestTimestampSince(long groupId, long timestamp) {
		Cursor c = this.getDBAdapter().getDatabase().query(
				"result",
				new String[] {
						"timestamp",
				},
				"group_id=? AND timestamp >= ?",
				new String[] {
						groupId+"",
						timestamp+"",
				},
				null,
				null,
				"timestamp ASC",
				"1"
			);

		if(c.moveToFirst()) {
			Long newTimestamp = c.getLong(0);
			c.close();

			return newTimestamp;
		}
		c.close();

		return -1;
	}
}

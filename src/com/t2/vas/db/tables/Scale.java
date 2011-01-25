package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Scale extends Table {
	private static final String TAG = Scale.class.getName();
	
	public long group_id;
	public long scale_id;
	public String max_label;
	public String min_label;
	public int weight = 0;

	/*public static final int GROUPBY_YEAR = Calendar.YEAR;
	public static final int GROUPBY_MONTH = Calendar.MONTH;
	public static final int GROUPBY_WEEK = Calendar.WEEK_OF_YEAR;
	public static final int GROUPBY_DAY = Calendar.DAY_OF_MONTH;
	public static final int GROUPBY_HOUR = Calendar.HOUR_OF_DAY;*/
	
	public static final int ORDERBY_ASC = 13;
	public static final int ORDERBY_DESC = 14;
	
	public Scale(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "scale";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE scale (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, min_label TEXT NOT NULL, max_label TEXT NOT NULL, weight INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		
	}
	
	
	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("group_id", this.group_id);
		v.put("max_label", this.max_label);
		v.put("min_label", this.min_label);
		v.put("weight", this.weight);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.group_id = c.getLong(c.getColumnIndex("group_id"));
		this.max_label = c.getString(c.getColumnIndex("max_label"));
		this.min_label = c.getString(c.getColumnIndex("min_label"));
		this.weight = c.getInt(c.getColumnIndex("weight"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("group_id", this.group_id);
		v.put("max_label", this.max_label);
		v.put("min_label", this.min_label);
		v.put("weight", this.weight);
		
		return this.update(v);
	}
	
	@Override
	public boolean delete() {
		Result r = (Result)this.dbAdapter.getTable("result");
		ContentValues cv = new ContentValues();
		cv.put("scale_id", this._id);
		r.delete(cv);
		
		return super.delete();
	}

	@Override
	public Scale newInstance() {
		return new Scale(this.dbAdapter);
	}
	
	public Result[] getResults() {
		ContentValues v = new ContentValues();
		v.put("scale_id", this._id+"");
		
		ArrayList<Result> results = new ArrayList<Result>();
		Cursor c = this.getDBAdapter().getTable("result").select(v);

		while(c.moveToNext()) {
			
			Result r = (Result)this.getDBAdapter().getTable("result").newInstance();
			r.load(c);
			results.add(r);
		}
		
		return results.toArray(new Result[results.size()]);
	}
	
	public Note[] getNotes() {
		return this.getNotes(Scale.ORDERBY_ASC);
	}
	
	public Note[] getNotes(int order) {
		String orderBy;
		
		switch(order) {
			case Scale.ORDERBY_DESC:
				orderBy = "timestamp DESC";
				break;
			case Scale.ORDERBY_ASC:
				orderBy = "timestamp ASC";
				break;
			default:
				orderBy = "timestamp ASC";
				break;
		}
		
		ContentValues v = new ContentValues();
		v.put("scale_id", this._id+"");
		
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor c = this.getDBAdapter().getTable("note").select(v, orderBy);

		while(c.moveToNext()) {
			
			Note n = (Note)this.getDBAdapter().getTable("note").newInstance();
			n.load(c);
			notes.add(n);
		}
		
		return notes.toArray(new Note[notes.size()]);
	}
	
	public Cursor getResults(long startTime, long endTime) {
		//Log.v(TAG, "id:"+this._id +" startTime:"+startTime +" endTime:"+endTime);
		return this.getDBAdapter().getDatabase().query(
				"result",
				new String[]{
						"timestamp",
						"value",
				},
				"scale_id=? AND timestamp >= ? AND timestamp < ?",
				new String[]{
						this._id+"",
						startTime+"",
						endTime+""
				},
				null,
				null,
				null,
				null
		);
	}
	
	public Cursor getUniqueScalesCursor() {
		return this.getDBAdapter().getDatabase().query(
				"scale", 
				new String[] {
						"MIN(_id) _id",
						"MIN(group_id) group_id",
						"MIN(max_label) max_label",
						"MIN(min_label) min_label",
						"MIN(weight) weight",
				},
				null, 
				null, 
				"min_label || max_label", 
				null, 
				"min_label || max_label"
		);
	}
}

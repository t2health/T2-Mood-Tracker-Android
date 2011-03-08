package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Group extends Table {
	public static final String FIELD_INVERSE_RESULTS = "inverse_results";
	
	private static final String TAG = Group.class.getName();

	public String title;
	public int immutable = 0;
	public boolean inverseResults = false;

	public Group(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "group";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE `group` (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, immutable INTEGER NOT NULL, "+ FIELD_INVERSE_RESULTS +" INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		if(oldVersion == 1 && newVersion == 2) {
			this.dbAdapter.getDatabase().execSQL("ALTER TABLE `group` ADD COLUMN ("+ FIELD_INVERSE_RESULTS +" INTEGER NOT NULL)");
		}
	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("title", this.title);
		v.put("immutable", this.immutable);
		v.put(FIELD_INVERSE_RESULTS, this.inverseResults?1:0);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.title = c.getString(c.getColumnIndex("title"));
		this.immutable = c.getInt(c.getColumnIndex("immutable"));
		this.inverseResults = c.getInt(c.getColumnIndex(FIELD_INVERSE_RESULTS))>0?true:false;
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("title", this.title);
		v.put("immutable", this.immutable);
		v.put(FIELD_INVERSE_RESULTS, this.inverseResults?1:0);

		return this.update(v);
	}

	@Override
	public boolean delete() {
		ContentValues cv;

		Scale s = new Scale(this.dbAdapter);
		cv = new ContentValues();
		cv.put("group_id", this._id);
		s.delete(cv);

		Result r = new Result(dbAdapter);
		cv = new ContentValues();
		cv.put("group_id", this._id);
		r.delete(cv);

		return super.delete();
	}

	public Cursor getGroupsWithScalesCursor() {
		ContentValues v = new ContentValues();
		
		return this.getDBAdapter().getDatabase().query(
				"`group` g, `scale` s", 
				new String[] {
						"g.*",
				},
				"g._id=s.group_id", 
				null, 
				"g._id", 
				"COUNT(*) > 0", 
				"g.title"
		);
	}
	
	public Cursor getGroupsCursor() {
		ContentValues v = new ContentValues();
		return this.select(
				v,
				"title ASC"
		);
	}
	
	public ArrayList<Group> getGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getGroupsCursor();

		while(c.moveToNext()) {
			Group group = new Group(this.dbAdapter);
			group.load(c);
			groups.add(group);
		}
		c.close();

		return groups;
	}

	public Cursor getScalesCursor() {
		ContentValues v = new ContentValues();
		v.put("group_id", this._id+"");
		return new Scale(this.dbAdapter).select(v, "weight ASC");
	}
	
	public ArrayList<Scale> getScales() {
		ArrayList<Scale> scales = new ArrayList<Scale>();
		Cursor c = this.getScalesCursor();
		while(c.moveToNext()) {
			Scale scale = new Scale(this.dbAdapter);
			scale.load(c);

			scales.add(scale);
		}
		c.close();

		return scales;
	}

	public int getResultsCount() {
		Cursor c = this.getDBAdapter().getDatabase().query(
				"result",
				new String[]{
						"COUNT(*) cnt",
				},
				"group_id=?",
				new String[]{
						this._id+"",
				},
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
	
	public void clearResults() {
		this.dbAdapter.getDatabase().delete(
				"result",
				"group_id=?",
				new String[] {
					this._id+"",
				}
		);
	}
	
	public Cursor getResults(long startTime, long endTime) {
		//Log.v(TAG, "id:"+this._id +" startTime:"+startTime +" endTime:"+endTime);
		return this.getDBAdapter().getDatabase().query(
				"result",
				new String[]{
						"timestamp",
						"value",
				},
				"group_id=? AND timestamp >= ? AND timestamp < ?",
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
}

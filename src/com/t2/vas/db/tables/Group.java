package com.t2.vas.db.tables;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Group extends Table {
	private static final String TAG = Group.class.getName();

	public long group_id;
	public String title;
	public int immutable = 0;

	public Group(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "group";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE `group` (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, immutable INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {

	}

	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("title", this.title);
		v.put("immutable", this.immutable);

		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.title = c.getString(c.getColumnIndex("title"));
		this.immutable = c.getInt(c.getColumnIndex("immutable"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("title", this.title);
		v.put("immutable", this.immutable);

		return this.update(v);
	}

	@Override
	public boolean delete() {
		ContentValues cv;

		Scale s = (Scale)this.dbAdapter.getTable("scale");
		cv = new ContentValues();
		cv.put("group_id", this._id);
		s.delete(cv);

		Result r = (Result)this.dbAdapter.getTable("result");
		cv = new ContentValues();
		cv.put("group_id", this._id);
		r.delete(cv);

		GroupReminder gr = (GroupReminder)this.dbAdapter.getTable("groupreminder");
		cv = new ContentValues();
		cv.put("group_id", this._id);
		gr.delete(cv);

		return super.delete();
	}

	@Override
	public Group newInstance() {
		return new Group(this.dbAdapter);
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
		return this.getDBAdapter().getTable("group").select(
				v,
				"title ASC"
		);
	}
	
	public ArrayList<Group> getGroups() {
		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getGroupsCursor();

		while(c.moveToNext()) {
			Group group = (Group)this.getDBAdapter().getTable("group").newInstance();
			group.load(c);
			groups.add(group);
		}
		c.close();

		return groups;
	}

	public ArrayList<Group> getMutableGroups() {
		ContentValues v = new ContentValues();
		v.put("immutable", 0);

		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getDBAdapter().getTable("group").select(
				v,
				"title ASC"
		);

		while(c.moveToNext()) {
			Group group = (Group)this.getDBAdapter().getTable("group").newInstance();
			group.load(c);
			groups.add(group);
		}
		c.close();

		return groups;
	}

	public ArrayList<Group> getGroupsOrderByLastResult() {
		ContentValues v = new ContentValues();

		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getDBAdapter().getDatabase().query(
				"`group` g",
				new String[]{
						"g.*",
						"(SELECT MAX(timestamp) FROM result WHERE group_id=g._id) last_result",
				},
				null,
				null,
				null,
				null,
				"last_result DESC, g.title ASC"
		);

		while(c.moveToNext()) {
			Group group = (Group)this.getDBAdapter().getTable("group").newInstance();
			group.load(c);
			groups.add(group);
		}
		c.close();

		return groups;
	}

	public ArrayList<Group> getGroupsWithResults() {
		ArrayList<Group> groups = this.getGroupsOrderByLastResult();
		ArrayList<Group> newGroups = new ArrayList<Group>();

		for(int i = 0; i < groups.size(); i++) {
			if(groups.get(i).hasResults()) {
				newGroups.add(groups.get(i));
			}
		}

		return newGroups;
	}

	public boolean hasResults() {
		Cursor hasResultsCursor = this.getDBAdapter().getDatabase().query(
				"result",
				null,
				"group_id=?",
				new String[]{
						this._id+"",
				},
				null,
				null,
				null,
				"1"
		);

		if(hasResultsCursor.moveToFirst()) {
			hasResultsCursor.close();
			return true;
		}
		hasResultsCursor.close();
		return false;
	}

	public Cursor getNamedScalesCursor() {
		return this.getDBAdapter().getDatabase().query(
				"scale", 
				new String[] {
					"_id",
					"min_label || ' ' || max_label title",	
				}, 
				"group_id=?", 
				new String[] {
						this._id+"",
				}, 
				null, 
				null, 
				"title"
		);
	}
	
	public Cursor getScalesCursor() {
		ContentValues v = new ContentValues();
		v.put("group_id", this._id+"");
		return this.getDBAdapter().getTable("scale").select(v, "weight ASC");
	}
	
	public ArrayList<Scale> getScales() {
		ArrayList<Scale> scales = new ArrayList<Scale>();
		Cursor c = this.getScalesCursor();
		while(c.moveToNext()) {
			Scale scale = (Scale)this.getDBAdapter().getTable("scale").newInstance();
			scale.load(c);

			scales.add(scale);
		}
		c.close();

		return scales;
	}

	public GroupReminder getReminder() {
		GroupReminder gr = ((GroupReminder)this.dbAdapter.getTable("groupreminder")).newInstance();
		gr.group_id = this._id;
		gr.remind_mode = GroupReminder.REMIND_NEVER;

		ContentValues whereConditions = new ContentValues();
		whereConditions.put("group_id", this._id);

		Cursor c = gr.select(whereConditions);
		if(c.moveToNext()) {
			gr.load(c);
		}
		c.close();

		return gr;
	}

	public long getLatestResultTimestamp() {
		Cursor c = this.dbAdapter.getDatabase().query(
				"result",
				new String[] {
						"MAX(timestamp) timestamp",
				},
				"group_id=?",
				new String[] {
					this._id+""
				},
				null,
				null,
				null,
				null
		);


		if(c.moveToNext()) {
			Long ts = c.getLong(c.getColumnIndex("timestamp"));
			c.close();
			if(ts != null) {
				return ts;
			} else {
				return -1;
			}
		}
		
		c.close();
		
		return -1;
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
}

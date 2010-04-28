package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Group extends Table {
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
	
	public ArrayList<Group> getGroups() {
		ContentValues v = new ContentValues();
		
		ArrayList<Group> groups = new ArrayList<Group>();
		Cursor c = this.getDBAdapter().getTable("group").select(v);
		while(c.moveToNext()) {
			Group group = (Group)this.getDBAdapter().getTable("group").newInstance();
			group.load(c);
			
			groups.add(group);
		}
		
		return groups;
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
	
	public GroupReminder getReminder() {
		GroupReminder gr = ((GroupReminder)this.dbAdapter.getTable("groupreminder")).newInstance();
		gr.group_id = this._id;
		gr.remind_mode = GroupReminder.REMIND_NEVER;
		
		ContentValues whereConditions = new ContentValues();
		whereConditions.put("group_id", this._id);
		
		Cursor c = gr.select(whereConditions);
		if(c.moveToNext()) {
			gr.load(c);
			c.close();
			
		}
		
		return gr;
	}
	
	public long getLatestResultTimestamp() {
		Cursor c = this.dbAdapter.getDatabase().query(
				"result", 
				null, 
				"group_id=?", 
				new String[] {
					this._id+""
				}, 
				null, 
				null, 
				"timestamp DESC", 
				"1"
		);
		
		if(c.moveToNext()) {
			return c.getLong(c.getColumnIndex("timestamp"));
		}
		
		return -1;
	}
}

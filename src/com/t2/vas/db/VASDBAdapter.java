package com.t2.vas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VASDBAdapter {
	private static final String TAG = "DBAdapter";
	private Context context;
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public VASDBAdapter(Context c) {
		this.context = c;
	}
	
	public VASDBAdapter open() throws SQLException {
		this.dbHelper = new DatabaseHelper(this.context, "VAS_data", 1);
		this.db = this.dbHelper.getWritableDatabase();
		this.dbHelper.dropTables(this.db);
		this.dbHelper.onCreate(this.db);
		
		return this;
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	
	public long insertGroup(String title) {
		return _insertGroup(this.db, title);
	}
	public static long _insertGroup(SQLiteDatabase db, String title) {
		ContentValues v = new ContentValues();
		v.put("title", title);
		
		return db.insert("`group`", null, v);
	}
	public boolean updateGroup(long rowId, String title) {
		ContentValues v = new ContentValues();
		v.put("title", title);
		
		return this.db.update("`group`", v, "_id="+rowId, null) > 0;
	}
	public boolean deleteGroup(long rowId) {
		this.db.delete("result", "scale_id IN(SELECT _id FROM scale WHERE group_id="+rowId+")", null);
		this.db.delete("scale", "group_id="+rowId, null);
		return this.db.delete("`group`", "_id="+rowId, null) > 0;
	}
	public Cursor selectAllGroups() {
		return this.db.query("`group`", new String[]{"_id", "title"}, null, null, null, null, null);
	}
	public Cursor selectGroup(long rowId) throws SQLException {
		Cursor c = this.db.query("`group`", new String[]{"_id", "title"}, "_id="+rowId, null, null, null, null);
		
		if(c != null) {
			c.moveToFirst();
		}
		
		return c;
	}
	
	
	public long insertScale(long groupId, String left, String right) {
		return _insertScale(this.db, groupId, left, right);
	}
	public static long _insertScale(SQLiteDatabase db, long groupId, String left, String right) {
		ContentValues v = new ContentValues();
		v.put("group_id", groupId);
		v.put("left", left);
		v.put("right", right);
		
		return db.insert("scale", null, v);
	}
	public boolean updateScale(long rowId, long groupId, String left, String right) {
		ContentValues v = new ContentValues();
		v.put("group_id", groupId);
		v.put("left", left);
		v.put("right", right);
		
		return this.db.update("scale", v, "_id="+rowId, null) > 0;
	}
	public boolean deleteScale(long rowId) {
		this.db.delete("result", "scale_id="+rowId, null);
		return this.db.delete("scale", "_id="+rowId, null) > 0;
	}
	public Cursor selectAllScales() {
		return this.db.query("scale", new String[]{"_id", "group_id", "left", "right"}, null, null, null, null, null);
	}
	public Cursor selectAllScales(long groupId) {
		return this.db.query("scale", new String[]{"_id", "group_id", "left", "right"}, "group_id="+groupId, null, null, null, null);
	}
	public Cursor selectScale(long rowId) throws SQLException {
		Cursor c = this.db.query("scale", new String[]{"_id", "group_id", "left", "right"}, "_id="+rowId, null, null, null, null);
		
		if(c != null) {
			c.moveToFirst();
		}
		
		return c;
	}
	
	
	public long insertResult(long scaleId, long timestamp, int value) {
		ContentValues v = new ContentValues();
		v.put("scale_id", scaleId);
		v.put("timestamp", timestamp);
		v.put("value", value);
		
		return this.db.insert("result", null, v);
	}
	public boolean updateResult(long rowId, long scaleId, long timestamp, int value) {
		ContentValues v = new ContentValues();
		v.put("scale_id", scaleId);
		v.put("timestamp", timestamp);
		v.put("value", value);
		
		return this.db.update("result", v, "_id="+rowId, null) > 0;
	}
	public boolean deleteResult(long rowId) {
		return this.db.delete("result", "_id="+rowId, null) > 0;
	}
	public Cursor selectAllResults() {
		return this.db.query("result", new String[]{"_id", "timestamp", "value"}, null, null, null, null, null);
	}
	public Cursor selectResult(long rowId) throws SQLException {
		Cursor c = this.db.query("result", new String[]{"_id", "timestamp", "value"}, "_id="+rowId, null, null, null, null);
		
		if(c != null) {
			c.moveToFirst();
		}
		
		return c;
	}
	
	
	
	
	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context c, String dbName, int dbVersion) {
			super(c, dbName, null, dbVersion);
		}
		
		public void onCreate(SQLiteDatabase db) {
			Log.v(TAG, "Creating and popilating tables with default data.");
			db.execSQL("CREATE TABLE `group` (_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL)");
			db.execSQL("CREATE TABLE scale (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, left TEXT NOT NULL, right TEXT NOT NULL)");
			db.execSQL("CREATE TABLE result (_id INTEGER PRIMARY KEY AUTOINCREMENT, scale_id INTEGER NOT NULL, timestamp INTEGER NOT NULL, value INTEGER NOT NULL)");
			
			long groupId = _insertGroup(db, "How are you feeling today?");
			_insertScale(db, groupId, "Happy", "Sad");
			_insertScale(db, groupId, "Tired", "Energetic");
			_insertScale(db, groupId, "Worried", "Calm");
			_insertScale(db, groupId, "Tense", "Relaxed");
			_insertScale(db, groupId, "Pessimistic", "Optimistic");
			_insertScale(db, groupId, "Hopeless", "Hopeful");
			_insertScale(db, groupId, "Alone", "Connected");
			_insertScale(db, groupId, "Unsafe", "Safe");
			_insertScale(db, groupId, "Unloved", "Loved");
			_insertScale(db, groupId, "Angry", "Content");
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			this.dropTables(db);
			this.onCreate(db);
		}
		
		public void dropTables(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS `group`");
			db.execSQL("DROP TABLE IF EXISTS scale");
			db.execSQL("DROP TABLE IF EXISTS result");	
		}
	}
	
	
}

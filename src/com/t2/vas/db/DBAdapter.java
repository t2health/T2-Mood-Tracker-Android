package com.t2.vas.db;

import java.util.LinkedHashMap;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.t2.vas.Global;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class DBAdapter extends SQLiteOpenHelper {
	private static final String TAG = DBAdapter.class.getName();
	private Context context;
	private LinkedHashMap<String,AbsTable> tables = new LinkedHashMap<String,AbsTable>();
	
	private SQLiteDatabase database;
	
	public DBAdapter(Context c, String dbName, int dbVersion) {
		super(c, dbName, null, dbVersion);
		this.context = c;
		this.init();
	}
	
	private void init() {
		AbsTable t;
		
		t = new Group(this);
		this.tables.put(t.getTableName(), t);
		
		t = new Scale(this);
		this.tables.put(t.getTableName(), t);
		
		t = new Result(this);
		this.tables.put(t.getTableName(), t);
		
		t = new Note(this);
		this.tables.put(t.getTableName(), t);
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public SQLiteDatabase getDatabase() {
		//Log.v(TAG, "GETDATABASE");
		if(!this.isOpen()) {
			this.open();
		}
		return this.database;
	}
	
	public DBAdapter open() {
		//Log.v(TAG, "OPEN");
		this.database = this.getWritableDatabase();
		//Log.v(TAG, "OPEN DB:"+this.database);
		return this;
	}

	public boolean isOpen() {
		if(this.database == null) {
			return false;
		}
		return this.database.isOpen();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.database = db;
		
		//Log.v(TAG, "ON CREATE");
		Set<String> keys = this.tables.keySet();
		
		for(String key: keys) {
			this.tables.get(key).onCreate();
		}
		
		// Install the base and test data
		InstallDB.onCreate(this, Global.Database.CREATE_FAKE_DATA);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion < 2 && newVersion >= 2) {
			this.dbUpgradeVersion2(db);
		}
	}
	
	private void dbUpgradeVersion2(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE `group` ADD COLUMN `visible` INTEGER;");
		db.execSQL("UPDATE `group` SET `visible`=1;");
	}
	
	public void onDrop(SQLiteDatabase db) {
		//Log.v(TAG, "ON UPGRADE");
		Set<String> keys = this.tables.keySet();
		
		for(String key: keys) {
			this.tables.get(key).onDrop();
		}
	}
	
	public Set<String> getTableNames() {
		return this.tables.keySet();
	}
	
	public AbsTable getTable(String name) {
		return this.tables.get(name);
	}
	
	public static ContentValues buildContentValues(String[] keys, String[] values) {
		ContentValues v = new ContentValues();
		for(int i = 0; i < keys.length; i++) {
			v.put(keys[i], values[i]);
		}
		return v;
	}
}

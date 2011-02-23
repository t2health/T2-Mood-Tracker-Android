package com.t2.vas.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {
	private static final String TAG = DBAdapter.class.getName();
	private Context context;
	//private ArrayList<AbsTable> tables = new ArrayList<AbsTable>();
	private SQLiteDatabase database;
	private OnDatabaseCreatedListener createListener;
	
	public DBAdapter(Context c, String dbName, int dbVersion) {
		super(c, dbName, null, dbVersion);
		this.context = c;
		this.init();
	}
	
	private void init() {
		
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
		
		/*for(int i = 0; i < this.tables.size(); ++i) {
			this.tables.get(i).onCreate();
		}*/
		
		if(this.createListener != null) {
			this.createListener.onDatabaseCreated();
		}
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
	
	/*public void onDrop(SQLiteDatabase db) {
		//Log.v(TAG, "ON UPGRADE");
		for(int i = 0; i < this.tables.size(); ++i) {
			this.tables.get(i).onDrop();
		}
	}*/
	
	public static ContentValues buildContentValues(String[] keys, String[] values) {
		ContentValues v = new ContentValues();
		for(int i = 0; i < keys.length; i++) {
			v.put(keys[i], values[i]);
		}
		return v;
	}
	
	public void setOnCreateListener(OnDatabaseCreatedListener l) {
		this.createListener = l;
	}
	
	public interface OnDatabaseCreatedListener {
		public void onDatabaseCreated();
	}
}

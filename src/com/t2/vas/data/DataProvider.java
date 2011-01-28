package com.t2.vas.data;

import java.util.LinkedHashMap;

import com.t2.vas.db.DBAdapter;

public abstract class DataProvider {
	protected DBAdapter dbAdapter;

	public DataProvider(DBAdapter db) {
		this.dbAdapter = db;
	}
	
	public abstract LinkedHashMap<Long,Double> getData(long id, long startTime, long endTime);
}

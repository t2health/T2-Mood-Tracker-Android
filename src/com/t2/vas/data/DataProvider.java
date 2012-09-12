package com.t2.vas.data;

import java.util.LinkedHashMap;

import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public abstract class DataProvider {
	protected DBAdapter dbAdapter;

	public DataProvider(DBAdapter db) {
		this.dbAdapter = db;
	}
	
	public abstract LinkedHashMap<Long,Double> getData(long id, long startTime, long endTime);
	
	//Added to fix charting error - Steveody
	public double GetGroupAverage(long id, long startTime, long endTime)
	{
		double total = 0.0;
		int count = 0;

		Group group = new Group(dbAdapter);
		group._id = id;

		Cursor c = group.getResults(startTime, endTime);
		while (c.moveToNext()) {
			double value = c.getDouble(c.getColumnIndex("value"));
			if (c.getInt(c.getColumnIndex(Group.FIELD_INVERSE_RESULTS)) == 0) {
				value = 100 - value;
			}
			total += value;
			count++;
		}
		c.close();

		return total / count;
	}
}

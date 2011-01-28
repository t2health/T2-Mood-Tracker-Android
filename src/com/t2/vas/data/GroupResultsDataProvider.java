package com.t2.vas.data;

import java.util.LinkedHashMap;

import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public class GroupResultsDataProvider extends DataProvider {

	public GroupResultsDataProvider(DBAdapter db) {
		super(db);
	}

	@Override
	public LinkedHashMap<Long, Double> getData(long id, long startTime,
			long endTime) {
		LinkedHashMap<Long,Double> data = new LinkedHashMap<Long,Double>();
		Group group = new Group(dbAdapter);
		group._id = id;
		
		Cursor c = group.getResults(startTime, endTime);
		while(c.moveToNext()) {
			data.put(
					c.getLong(c.getColumnIndex("timestamp")),
					c.getDouble(c.getColumnIndex("value"))
			);
		}
		c.close();
		
		return data;
	}

}

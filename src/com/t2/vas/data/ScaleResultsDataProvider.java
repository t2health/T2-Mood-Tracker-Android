package com.t2.vas.data;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Scale;

public class ScaleResultsDataProvider extends DataProvider {

	public ScaleResultsDataProvider(DBAdapter db) {
		super(db);
	}

	@Override
	public LinkedHashMap<Long, Double> getData(long id, long startTime, long endTime) {
		LinkedHashMap<Long,Double> data = new LinkedHashMap<Long,Double>();
		Scale scale = new Scale(dbAdapter);
		scale._id = id;
		
		Cursor c = scale.getResults(startTime, endTime);
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

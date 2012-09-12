package com.t2.vas.data;

import java.util.LinkedHashMap;

import android.database.Cursor;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

public class ScaleResultsDataProvider extends DataProvider {

	public ScaleResultsDataProvider(DBAdapter db) {
		super(db);
	}

	@Override
	public LinkedHashMap<Long, Double> getData(long id, long startTime, long endTime) {
		LinkedHashMap<Long, Double> data = new LinkedHashMap<Long, Double>();
		Scale scale = new Scale(dbAdapter);
		scale._id = id;

		Cursor c = scale.getResults(startTime, endTime);
		while (c.moveToNext()) {
			double value = c.getDouble(c.getColumnIndex("value"));
			if (c.getInt(c.getColumnIndex(Group.FIELD_INVERSE_RESULTS)) == 0) {
				value = 100 - value;
			}
			data.put(
					c.getLong(c.getColumnIndex("timestamp")),
					value
					);
		}
		c.close();

		return data;
	}

}

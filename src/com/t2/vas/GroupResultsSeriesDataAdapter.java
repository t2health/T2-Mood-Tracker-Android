package com.t2.vas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.db.tables.Scale.ResultValues;
import com.t2.vas.view.chart.Label;
import com.t2.vas.view.chart.Series;
import com.t2.vas.view.chart.SeriesAdapterData;
import com.t2.vas.view.chart.Value;
import com.t2.vas.view.chart.Series.SeriesDataAdapter;

public class GroupResultsSeriesDataAdapter extends AbsResultsSeriesDataAdapter {
	private static final String TAG = GroupResultsSeriesDataAdapter.class.getName();
	protected long groupId;

	public GroupResultsSeriesDataAdapter(DBAdapter dbAdapter, long startTime, long groupId, int groupBy, String labelFormat) {
		super(dbAdapter, startTime, groupBy, labelFormat);
		this.groupId = groupId;
	}


	@Override
	protected Cursor getCursor(long startTime, String db_date_format) {
		Cursor c = dbAdapter.getDatabase().query(
				"result r ",
				new String[]{
					"MIN(strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch', 'localtime'))) label_value",
					"MIN(r.timestamp) timestamp",
					"AVG(r.value) value",
				},
				"group_id=? AND timestamp >= ?",
				new String[]{
					this.groupId+"",
					startTime+"",
				},
				"strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch', 'localtime'))",
				null,
				"label_value ASC",
				null
		);
		return c;
	}

	/*@Override
	protected boolean shouldHilight(long startTime, long endTime) {
		//Log.v(TAG, "S:"+startTimestamp+" E:"+endTimestamp);
		Cursor c = dbAdapter.getDatabase().query(
				"note",
				new String[]{
						"_id"
				},
				"timestamp >= ? AND timestamp < ?",
				new String[]{
						startTime+"",
						endTime+""
				},
				null,
				null,
				null,
				"1"
		);

		if(c.moveToNext()) {
			c.close();
			return true;
		}

		c.close();
		return false;
	}*/
}

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

public class GroupResultsSeriesDataAdapter extends ResultsSeriesDataAdapter {
	private static final String TAG = GroupResultsSeriesDataAdapter.class.getName();
	protected long groupId;

	public GroupResultsSeriesDataAdapter(DBAdapter dbAdapter, long groupId, int groupBy, String labelFormat) {
		super(dbAdapter, groupBy, labelFormat);
		this.groupId = groupId;
	}
	
	/*private boolean noteBetween(long startTimestamp, long endTimestamp) {
		//Log.v(TAG, "S:"+startTimestamp+" E:"+endTimestamp);
		Cursor c = dbAdapter.getDatabase().query(
				"note", 
				new String[]{
						"_id"
				}, 
				"timestamp >= ? AND timestamp < ?", 
				new String[]{
						startTimestamp+"",
						endTimestamp+""
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
	
	
	
	@Override
	protected Cursor getCursor(String db_date_format) {
		Cursor c = dbAdapter.getDatabase().query(
				"result r ",
				new String[]{
					"strftime('"+db_date_format+"', datetime(MIN(r.timestamp) / 1000, 'unixepoch')) label_value", 
					"MIN(r.timestamp) timestamp",
					"AVG(r.value) value",
				}, 
				"group_id=?", 
				new String[]{
					this.groupId+""
				}, 
				"strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch'))", 
				null, 
				"label_value ASC",
				null
		);
		
		return c;
	}

	@Override
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
	}
}

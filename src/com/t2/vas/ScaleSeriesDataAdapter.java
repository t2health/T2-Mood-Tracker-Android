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
import com.t2.vas.view.chart.SeriesAdapterData;
import com.t2.vas.view.chart.Value;
import com.t2.vas.view.chart.Series;
import com.t2.vas.view.chart.Series.SeriesDataAdapter;

public class ScaleSeriesDataAdapter implements SeriesDataAdapter {
	private static final String TAG = ScaleSeriesDataAdapter.class.getName();
	
	private DBAdapter dbAdapter;
	private long scaleId;
	private int groupBy;
	private String labelFormat;
	
	public static final int GROUPBY_YEAR = Calendar.YEAR;
	public static final int GROUPBY_MONTH = Calendar.MONTH;
	public static final int GROUPBY_WEEK = Calendar.WEEK_OF_YEAR;
	public static final int GROUPBY_DAY = Calendar.DAY_OF_MONTH;
	public static final int GROUPBY_HOUR = Calendar.HOUR_OF_DAY;
	
	public static final int ORDERBY_ASC = 13;
	public static final int ORDERBY_DESC = 14;

	public ScaleSeriesDataAdapter(DBAdapter dbAdapter, long scaleId, int groupBy, String labelFormat) {
		this.dbAdapter = dbAdapter;
		this.scaleId = scaleId;
		this.groupBy = groupBy;
		this.labelFormat = labelFormat;
	}
	
	@Override
	public SeriesAdapterData getData() {
		boolean openForThis = false;
		if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
			openForThis = true;
		}
		
		SimpleDateFormat labelDateFormatter = new SimpleDateFormat(labelFormat);
		String formatter_date_format = "";
		String db_date_format = "";
		
		// Determine the label format to use.
		switch(this.groupBy) {
			case GROUPBY_HOUR:
				formatter_date_format = "yyyy-MM-dd HH";
				db_date_format = "%Y-%m-%d %H";
				break;
			case GROUPBY_MONTH:
				formatter_date_format = "yyyy-MM";
				db_date_format = "%Y-%m";
				break;
			case GROUPBY_WEEK:
				formatter_date_format = "yyyy-ww";
				db_date_format = "%Y-%W";
				break;
			case GROUPBY_YEAR:
				formatter_date_format = "yyyy";
				db_date_format = "%Y";
				break;
			case GROUPBY_DAY:
			default:
				formatter_date_format = "yyyy-MM-dd";
				db_date_format = "%Y-%m-%d";
				break;
		}
		
		Cursor c = dbAdapter.getDatabase().query(
				"result r " +
				"LEFT JOIN note n ON (" +
					"strftime('"+db_date_format+"', datetime(n.timestamp / 1000, 'unixepoch')) = strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch'))" +
				")",
				new String[]{
					"strftime('"+db_date_format+"', datetime(MIN(r.timestamp) / 1000, 'unixepoch')) label_value", 
					"MIN(r.timestamp) timestamp",
					"AVG(r.value) value",
					"COUNT(n._id) has_notes",
				}, 
				"scale_id=?", 
				new String[]{
					this.scaleId+""
				}, 
				"strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch'))", 
				null, 
				"label_value ASC",
				null
		);
		//Log.v(TAG, "ROW COUNT:"+c.getCount());
		
		String rLabelValue = "";
		long rTimestamp = 0;
		double rValue = 0.00;
		boolean rHasNotes = false;
		
		SeriesAdapterData resultValues = new SeriesAdapterData();
		//ResultValues resultValues = new ResultValues();
		SimpleDateFormat groupByDateFormatter = new SimpleDateFormat(formatter_date_format);
		Date tmpDate;
		Calendar runningCal = null;
		Calendar rowCal = Calendar.getInstance();
		boolean loadNext = true;
		while(true) {
			if(loadNext) {
				if(!c.moveToNext()) {
					break;
				}
				
				rLabelValue = c.getString(c.getColumnIndex("label_value"));
				rTimestamp = c.getLong(c.getColumnIndex("timestamp"));
				rValue = c.getDouble(c.getColumnIndex("value"));
				rHasNotes = c.getInt(c.getColumnIndex("has_notes")) > 0;
				
				Calendar tmpCal = Calendar.getInstance();
				tmpCal.setTimeInMillis(rTimestamp);
				//Log.v(TAG, "DATE:"+groupByDateFormatter.format(tmpCal.getTime()));
				//Log.v(TAG, "DATE:"+rLabelValue);
				//Log.v(TAG, "  .");
				//Log.v(TAG, "TIMESTAMP:"+rTimestamp);
				
				//Log.v(TAG, "VAL:"+rValue);
				//Log.v(TAG, "HN:"+rHasNotes);
				
				if(runningCal == null) {
					runningCal = Calendar.getInstance();
					runningCal.setTimeInMillis(rTimestamp);
				}
			}
			loadNext = false;
			
			
			try {
				rowCal.setTimeInMillis(rTimestamp);
				tmpDate = rowCal.getTime();
				rowCal.setTime(
						groupByDateFormatter.parse(groupByDateFormatter.format(tmpDate))
				);
				
				tmpDate = runningCal.getTime();
				runningCal.setTime(
						groupByDateFormatter.parse(groupByDateFormatter.format(tmpDate))
				);
				//Log.v(TAG, groupByDateFormatter.format(rowCal.getTime())+", "+ groupByDateFormatter.format(runningCal.getTime()));
				//Log.v(TAG, rowCal.getTimeInMillis()+", "+ runningCal.getTimeInMillis());
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			String labelString = labelDateFormatter.format(runningCal.getTime());
			if(rowCal.getTimeInMillis() == runningCal.getTimeInMillis()) {
				resultValues.add(
						new Label<Date>(labelString, runningCal.getTime()),
						new Value(rValue, null, rHasNotes)
				);
				
				loadNext = true;
			} else {
				resultValues.add(
						new Label<Date>(labelString, runningCal.getTime()), 
						new Value(null, null, false)
				);
			}
			
			runningCal.add(this.groupBy, 1);
		}
		
		if(openForThis) {
			this.dbAdapter.close();
		}
		
		return resultValues;
	}
}

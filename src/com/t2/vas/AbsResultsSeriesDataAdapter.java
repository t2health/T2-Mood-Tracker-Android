package com.t2.vas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.database.Cursor;
import android.util.Log;

import com.t2.chart.Label;
import com.t2.chart.SeriesAdapterData;
import com.t2.chart.Value;
import com.t2.chart.Series.SeriesDataAdapter;
import com.t2.vas.db.DBAdapter;

public abstract class AbsResultsSeriesDataAdapter implements SeriesDataAdapter {
	private static final String TAG = AbsResultsSeriesDataAdapter.class.getName();

	protected int debugMode = 0;

	protected DBAdapter dbAdapter;
	//protected long scaleId;
	protected int groupBy;
	protected String labelFormat;

	private SeriesAdapterData getDataCache = null;

	private long startTime;

	public static final int GROUPBY_YEAR = Calendar.YEAR;
	public static final int GROUPBY_MONTH = Calendar.MONTH;
	public static final int GROUPBY_WEEK = Calendar.WEEK_OF_YEAR;
	public static final int GROUPBY_DAY = Calendar.DAY_OF_MONTH;
	public static final int GROUPBY_HOUR = Calendar.HOUR_OF_DAY;

	public static final int ORDERBY_ASC = 13;
	public static final int ORDERBY_DESC = 14;


	public AbsResultsSeriesDataAdapter(DBAdapter dbAdapter, long startTime, int groupBy, String labelFormat) {
		this.dbAdapter = dbAdapter;
		this.startTime = startTime;
		this.groupBy = groupBy;
		this.labelFormat = labelFormat;
	}

	protected abstract Cursor getCursor(long startTime, String db_date_format);

	private Calendar setCalendarPrecision(Calendar c, int[] calendarFields) {
		Calendar outCalendar = Calendar.getInstance();
		outCalendar.setTimeInMillis(0);

		for(int i = 0; i < calendarFields.length; i++) {
			outCalendar.set(calendarFields[i], c.get(calendarFields[i]));
		}

		return outCalendar;
	}

	@Override
	public SeriesAdapterData getData() {
		if(this.getDataCache != null) {
			return this.getDataCache;
		}

		boolean openForThis = false;
		/*if(!this.dbAdapter.isOpen()) {
			this.dbAdapter.open();
			openForThis = true;
		}*/

		SimpleDateFormat labelDateFormatter = new SimpleDateFormat(labelFormat);
		String labelFormat = "";
		String db_date_format = "";
		int[] calendarFields = new int[]{};

		// Determine the label format to use.
		switch(this.groupBy) {
			case GROUPBY_HOUR:
				labelFormat = "yyyy-MM-dd HH";
				db_date_format = "%Y-%m-%d %H";
				calendarFields = new int[]{
						Calendar.YEAR,
						Calendar.MONTH,
						Calendar.DAY_OF_MONTH,
						Calendar.HOUR_OF_DAY,
				};
				break;
			case GROUPBY_MONTH:
				labelFormat = "yyyy-MM";
				db_date_format = "%Y-%m";
				calendarFields = new int[]{
						Calendar.YEAR,
						Calendar.MONTH,
				};
				break;
			case GROUPBY_WEEK:
				labelFormat = "yyyy-ww";
				db_date_format = "%Y-%W";
				calendarFields = new int[]{
						Calendar.YEAR,
						Calendar.WEEK_OF_YEAR
				};
				break;
			case GROUPBY_YEAR:
				labelFormat = "yyyy";
				db_date_format = "%Y";
				calendarFields = new int[]{
						Calendar.YEAR,
				};
				break;
			case GROUPBY_DAY:
			default:
				labelFormat = "yyyy-MM-dd";
				db_date_format = "%Y-%m-%d";
				calendarFields = new int[]{
						Calendar.YEAR,
						Calendar.MONTH,
						Calendar.DAY_OF_MONTH,
				};
				break;
		}

		SeriesDataRow tmpSDR = null;
		SeriesDataRow lastRow;

		// Load the DB data into a grouped date hash.
		//Log.v(TAG, "Load Data start");
		HashMap<Long, SeriesDataRow> rows = new HashMap<Long, SeriesDataRow>();
		Calendar rowCal = Calendar.getInstance();
		Cursor c = this.getCursor(this.startTime, db_date_format);
		Log.v(TAG, "Row Count:"+c.getCount());
		int[] columnIndexes = new int[]{
				c.getColumnIndex("label_value"),
				c.getColumnIndex("timestamp"),
				c.getColumnIndex("value"),
		};
		long lastTime = 0;
		
		while(c.moveToNext()) {
			tmpSDR = new SeriesDataRow(
				c.getString(columnIndexes[0]),
				c.getLong(columnIndexes[1]),
				c.getDouble(columnIndexes[2])
			);

			rowCal.setTimeInMillis(tmpSDR.timestamp);

			// Put the row into the hash at the specefied time.
			long newTime = setCalendarPrecision(rowCal, calendarFields).getTimeInMillis();
			lastTime = newTime;
			rows.put(
					newTime,
					tmpSDR
			);
		}
		c.close();
		lastRow = tmpSDR;
		//Log.v(TAG, "Load Data stop");


		// Init the running calendar and set it to have the accuracy of the group by argument.
		Calendar runningCalendar = Calendar.getInstance();
		runningCalendar.setTimeInMillis(this.startTime);
		runningCalendar = setCalendarPrecision(runningCalendar, calendarFields);

		// Put the data into the proper data structure.
		SeriesAdapterData resultValues = new SeriesAdapterData();

		if(lastRow == null) {
			return resultValues;
		}

		//Log.v(TAG, "LAST TIM:"+lastRow.timestamp);
		while(runningCalendar.getTimeInMillis() <= lastTime) {
			tmpSDR = rows.get(runningCalendar.getTimeInMillis());
			String labelString = labelDateFormatter.format(runningCalendar.getTime());
			Calendar runningCalendarValue = setCalendarPrecision(runningCalendar, new int[]{
					Calendar.YEAR,
					Calendar.MONTH,
					Calendar.DAY_OF_MONTH,
			});
			runningCalendarValue.set(Calendar.HOUR_OF_DAY, 0);
			runningCalendarValue.set(Calendar.MINUTE, 0);
			runningCalendarValue.set(Calendar.SECOND, 0);

			if(tmpSDR == null) {
				resultValues.add(
						new Label<Date>(labelString, runningCalendarValue.getTime()),
						new Value(null, null, false)
				);
			} else {
				resultValues.add(
						new Label<Date>(labelString, runningCalendarValue.getTime()),
						new Value(tmpSDR.value, null, false)
				);
			}
			runningCalendar.add(this.groupBy, 1);
		}
		
		if(resultValues.size() == 1) {
			resultValues.add(
						0,
						new Label<Date>("", Calendar.getInstance().getTime()),
						new Value(0.00, null, false)
			);
		}

		return resultValues;
	}

	protected class SeriesDataRow {
		public String labelValue;
		public long timestamp;
		public double value;

		public SeriesDataRow(String labelValue, long timestamp, double value) {
			this.labelValue = labelValue;
			this.timestamp = timestamp;
			this.value = value;
		}
	}
}

package com.t2.vas.db.tables;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;

import com.t2.chart.Label;
import com.t2.chart.Value;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.Table;

public class Scale extends Table {
	private static final String TAG = Scale.class.getName();
	
	public long group_id;
	public long scale_id;
	public String max_label;
	public String min_label;
	public int weight = 0;

	/*public static final int GROUPBY_YEAR = Calendar.YEAR;
	public static final int GROUPBY_MONTH = Calendar.MONTH;
	public static final int GROUPBY_WEEK = Calendar.WEEK_OF_YEAR;
	public static final int GROUPBY_DAY = Calendar.DAY_OF_MONTH;
	public static final int GROUPBY_HOUR = Calendar.HOUR_OF_DAY;*/
	
	public static final int ORDERBY_ASC = 13;
	public static final int ORDERBY_DESC = 14;
	
	public Scale(DBAdapter d) {
		super(d);
	}

	@Override
	public String getTableName() {
		return "scale";
	}

	@Override
	public void onCreate() {
		this.dbAdapter.getDatabase().execSQL("CREATE TABLE scale (_id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER NOT NULL, min_label TEXT NOT NULL, max_label TEXT NOT NULL, weight INTEGER NOT NULL)");
	}

	@Override
	public void onUpgrade(int oldVersion, int newVersion) {
		
	}
	
	
	@Override
	public long insert() {
		ContentValues v = new ContentValues();
		v.put("group_id", this.group_id);
		v.put("max_label", this.max_label);
		v.put("min_label", this.min_label);
		v.put("weight", this.weight);
		
		return this.insert(v);
	}

	@Override
	public boolean load(Cursor c) {
		this._id = c.getLong(c.getColumnIndex("_id"));
		this.group_id = c.getLong(c.getColumnIndex("group_id"));
		this.max_label = c.getString(c.getColumnIndex("max_label"));
		this.min_label = c.getString(c.getColumnIndex("min_label"));
		this.weight = c.getInt(c.getColumnIndex("weight"));
		return true;
	}

	@Override
	public boolean update() {
		ContentValues v = new ContentValues();
		v.put("_id", this._id);
		v.put("group_id", this.group_id);
		v.put("max_label", this.max_label);
		v.put("min_label", this.min_label);
		v.put("weight", this.weight);
		
		return this.update(v);
	}
	
	@Override
	public boolean delete() {
		Result r = (Result)this.dbAdapter.getTable("result");
		ContentValues cv = new ContentValues();
		cv.put("scale_id", this._id);
		r.delete(cv);
		
		return super.delete();
	}

	@Override
	public Scale newInstance() {
		return new Scale(this.dbAdapter);
	}
	
	public Result[] getResults() {
		ContentValues v = new ContentValues();
		v.put("scale_id", this._id+"");
		
		ArrayList<Result> results = new ArrayList<Result>();
		Cursor c = this.getDBAdapter().getTable("result").select(v);

		while(c.moveToNext()) {
			
			Result r = (Result)this.getDBAdapter().getTable("result").newInstance();
			r.load(c);
			results.add(r);
		}
		
		return results.toArray(new Result[results.size()]);
	}
	
	public Note[] getNotes() {
		return this.getNotes(Scale.ORDERBY_ASC);
	}
	
	public Note[] getNotes(int order) {
		String orderBy;
		
		switch(order) {
			case Scale.ORDERBY_DESC:
				orderBy = "timestamp DESC";
				break;
			case Scale.ORDERBY_ASC:
				orderBy = "timestamp ASC";
				break;
			default:
				orderBy = "timestamp ASC";
				break;
		}
		
		ContentValues v = new ContentValues();
		v.put("scale_id", this._id+"");
		
		ArrayList<Note> notes = new ArrayList<Note>();
		Cursor c = this.getDBAdapter().getTable("note").select(v, orderBy);

		while(c.moveToNext()) {
			
			Note n = (Note)this.getDBAdapter().getTable("note").newInstance();
			n.load(c);
			notes.add(n);
		}
		
		return notes.toArray(new Note[notes.size()]);
	}
	
	/*
	public ResultValues getResultValues(int group_by, String labelFormat) {
		SimpleDateFormat labelDateFormatter = new SimpleDateFormat(labelFormat);
		String formatter_date_format = "";
		String db_date_format = "";
		
		// Determine the label format to use.
		switch(group_by) {
			case Scale.GROUPBY_HOUR:
				formatter_date_format = "yyyy-MM-dd HH";
				db_date_format = "%Y-%m-%d %H";
				break;
			case Scale.GROUPBY_MONTH:
				formatter_date_format = "yyyy-MM";
				db_date_format = "%Y-%m";
				break;
			case Scale.GROUPBY_WEEK:
				formatter_date_format = "yyyy-ww";
				db_date_format = "%Y-%W";
				break;
			case Scale.GROUPBY_YEAR:
				formatter_date_format = "yyyy";
				db_date_format = "%Y";
				break;
			case Scale.GROUPBY_DAY:
			default:
				formatter_date_format = "yyyy-MM-dd";
				db_date_format = "%Y-%m-%d";
				break;
		}
		
		Cursor c = this.getDBAdapter().getDatabase().query(
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
					this._id+""
				}, 
				"strftime('"+db_date_format+"', datetime(r.timestamp / 1000, 'unixepoch'))", 
				null, 
				"label_value ASC",
				null
		);
		Log.v(TAG, "ROW COUNT:"+c.getCount());
		
		String rLabelValue = "";
		long rTimestamp = 0;
		double rValue = 0.00;
		boolean rHasNotes = false;
		
		ResultValues resultValues = new ResultValues();
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
				//Log.v(TAG, "DD");
				resultValues.values.add(new Value(rValue, null, rHasNotes));
				resultValues.labels.add(new Label<Date>(labelString, runningCal.getTime()));
				
				loadNext = true;
			} else {
				//Log.v(TAG, "EE");
				resultValues.values.add(new Value(null, null, false));
				resultValues.labels.add(new Label<Date>(labelString, runningCal.getTime()));
			}
			
			runningCal.add(group_by, 1);
		}
		
		return resultValues;
	}*/
	
	/*public ResultValues getResultValues2(int group_by, String labelFormat) {
		Result[] results = this.getResults();
		String date_format;
		Calendar resultsAxisCal;
		SimpleDateFormat groupByDateFormatter;
		SimpleDateFormat labelDateFormatter = new SimpleDateFormat(labelFormat);
		
		if(results.length <= 0) {
			return new ResultValues();
		}
		
		// Determine the label format to use.
		switch(group_by) {
			case Scale.GROUPBY_DAY:
				date_format = Scale.GROUPBY_DAY_FORMAT;
				break;
			case Scale.GROUPBY_HOUR:
				date_format = Scale.GROUPBY_HOUR_FORMAT;
				break;
			case Scale.GROUPBY_MONTH:
				date_format = Scale.GROUPBY_MONTH_FORMAT;
				break;
			case Scale.GROUPBY_WEEK:
				date_format = Scale.GROUPBY_WEEK_FORMAT;
				break;
			case Scale.GROUPBY_YEAR:
				date_format = Scale.GROUPBY_YEAR_FORMAT;
				break;
			default:
				date_format = Scale.GROUPBY_DAY_FORMAT;
				break;
		}
		
		// Determine the first axis label
		resultsAxisCal = Calendar.getInstance();
		groupByDateFormatter = new SimpleDateFormat(date_format);
		resultsAxisCal.setTimeInMillis(results[0].timestamp);
		try {
			resultsAxisCal.setTime(
				groupByDateFormatter.parse(
						groupByDateFormatter.format(resultsAxisCal.getTime())
				)
			);
		} catch (ParseException e) {}
		
		
		// Build the results, values and labels
		ResultValues resultValues = new ResultValues();
		int currentResultIndex = 0;
		long currentTime = resultsAxisCal.getTimeInMillis();
		long nextTime = 0;
		while(true) {
			// Drop out of the loop if there are not any more results to process.
			if(currentResultIndex >= results.length) {
				break;
			}
			
			Double value = null;
			ArrayList<Result> resultsList = new ArrayList<Result>();
			
			// Add the label.
			resultValues.labels.add(
					new Label<Date>(
						labelDateFormatter.format(resultsAxisCal.getTime()),
						resultsAxisCal.getTime()
					)
			);
			//resultValues.results.add(resultsList);
			
			// Group the results together.
			currentTime = resultsAxisCal.getTimeInMillis();
			resultsAxisCal.add(group_by, 1);
			nextTime = resultsAxisCal.getTimeInMillis();
			
			for(; currentResultIndex < results.length; currentResultIndex++) {
				Result currentResult = results[currentResultIndex];
				
				// Group this result into this time group
				if(currentResult.timestamp >= currentTime && currentResult.timestamp < nextTime) {
					resultsList.add(currentResult);
				} else {
					break;
				}
			}
			
			// Average the results in this group and use the value
			if(resultsList.size() > 0) {
				value = 0.00;
				for(int i = 0; i < resultsList.size(); i++) {
					value += resultsList.get(i).value;
				}
				value /= resultsList.size();
			}
			
			
			// append the value to the values list. yes this can take a null value.
			// nulls are result groups where nothing was recorded.
			resultValues.values.add(
					new Value<ArrayList<Result>>(
						value,
						resultsList
					)
			);
			
			
			// Check if there are notes in the result range
			if(resultsList.size() > 0) {
				long startTimestamp = currentTime;
				long endTimestamp = nextTime;
				
				Log.v(TAG, "TS:"+startTimestamp+","+endTimestamp);
				
				Cursor c = ((Note)this.getDBAdapter().getTable("note")).queryForNotes(startTimestamp, endTimestamp, "timestamp DESC");
				if(c.getCount() > 0) {
					Log.v(TAG, "HAS NOTES");
					resultValues.values.get(resultValues.values.size() - 1).setHilight(true);
				}
				c.close();
			}
		}
		
		return resultValues;
	}*/
	
	public class ResultValues {
		public ArrayList<Value> values = new ArrayList<Value>();
		public ArrayList<Label> labels = new ArrayList<Label>();
	}
}

package com.t2.vas.db.tables;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.AbsTable;
import com.t2.vas.db.Table;
import com.t2.vas.view.chart.Label;
import com.t2.vas.view.chart.Value;

public class Scale extends Table {
	private static final String TAG = "SCALE";
	
	public long group_id;
	public long scale_id;
	public String max_label;
	public String min_label;
	public int weight;

	public static final int GROUPBY_YEAR = Calendar.YEAR;
	public static final int GROUPBY_MONTH = Calendar.MONTH;
	public static final int GROUPBY_WEEK = Calendar.WEEK_OF_YEAR;
	public static final int GROUPBY_DAY = Calendar.DAY_OF_MONTH;
	public static final int GROUPBY_HOUR = Calendar.HOUR_OF_DAY;
	
	private static final String GROUPBY_YEAR_FORMAT = "yyyy";
	private static final String GROUPBY_MONTH_FORMAT = "yyyy-MM";
	private static final String GROUPBY_DAY_FORMAT = "yyyy-MM-dd";
	private static final String GROUPBY_HOUR_FORMAT = "yyyy-MM-dd HH";
	private static final String GROUPBY_WEEK_FORMAT = "yyyy-ww";
	
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
		
		
		AbsTable t = this.dbAdapter.getTable("group");
		Cursor c = t.selectNewest("_id");
		
		long groupid = c.getLong(c.getColumnIndex("_id"));
		
		ContentValues v;
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Happy");
		v.put("min_label", "Sad");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Energetic");
		v.put("min_label", "Tired");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Calm");
		v.put("min_label", "Worried");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Relaxed");
		v.put("min_label", "Tense");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Optimistic");
		v.put("min_label", "Pessimistic");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Hopeful");
		v.put("min_label", "Hopeless");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Connected");
		v.put("min_label", "Alone");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Safe");
		v.put("min_label", "Unsafe");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Loved");
		v.put("min_label", "Unloved");
		v.put("weight", "0");
		this.insert(v);
		
		v = new ContentValues();
		v.put("group_id", groupid);
		v.put("max_label", "Content");
		v.put("min_label", "Angry");
		v.put("weight", "0");
		this.insert(v);
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
	
	
	public ResultValues getResultValues(int group_by, String labelFormat) {
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
			// nulls are reult groups where nothing was recorded.
			resultValues.values.add(
					new Value<ArrayList<Result>>(
						value,
						resultsList
					)
			);
		}
		
		return resultValues;
	}
	
	public class ResultValues {
		public ArrayList<Value> values = new ArrayList<Value>();
		public ArrayList<Label> labels = new ArrayList<Label>();
	}
}

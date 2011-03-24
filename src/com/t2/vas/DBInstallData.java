package com.t2.vas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBInstallData {
	private static final String TAG = DBInstallData.class.getSimpleName();
	
	public static void install(Context context, DBAdapter dbAdapter, SQLiteDatabase db) {
		new Group(dbAdapter).onCreate(db);
		new Scale(dbAdapter).onCreate(db);
		new Result(dbAdapter).onCreate(db);
		new Note(dbAdapter).onCreate(db);
	}
	
	public static void update(Context c, DBAdapter dbAdapter, SQLiteDatabase db, int oldVersion, int newVersion) {
		new Group(dbAdapter).onUpgrade(db, oldVersion, newVersion);
		new Scale(dbAdapter).onUpgrade(db, oldVersion, newVersion);
		new Result(dbAdapter).onUpgrade(db, oldVersion, newVersion);
		new Note(dbAdapter).onUpgrade(db, oldVersion, newVersion);
	}
	
	public static void createInitialData(DBAdapter dbAdapter, boolean generateFakeResults) {
		// Install the first group of scales
		//boolean generateFake = Global.Database.CREATE_FAKE_DATA;
		//Group group = (Group)dbAdapter.getTable("group");
		//Scale scale = (Scale)dbAdapter.getTable("scale");
		new Group(dbAdapter).empty();
		new Note(dbAdapter).empty();
		new Result(dbAdapter).empty();
		new Scale(dbAdapter).empty();

		Resources res = dbAdapter.getContext().getResources();
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group1),
				res.getInteger(R.integer.group1_reverse),
				res.getStringArray(R.array.group1_min),
				res.getStringArray(R.array.group1_max),
				generateFakeResults
		);

		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group2),
				res.getInteger(R.integer.group2_reverse),
				res.getStringArray(R.array.group2_min),
				res.getStringArray(R.array.group2_max),
				generateFakeResults
		);

		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group3),
				res.getInteger(R.integer.group3_reverse),
				res.getStringArray(R.array.group3_min),
				res.getStringArray(R.array.group3_max),
				generateFakeResults
		);

		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group4),
				res.getInteger(R.integer.group4_reverse),
				res.getStringArray(R.array.group4_min),
				res.getStringArray(R.array.group4_max),
				generateFakeResults
		);

		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group5),
				res.getInteger(R.integer.group5_reverse),
				res.getStringArray(R.array.group5_min),
				res.getStringArray(R.array.group5_max),
				generateFakeResults
		);

		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group6),
				res.getInteger(R.integer.group6_reverse),
				res.getStringArray(R.array.group6_min),
				res.getStringArray(R.array.group6_max),
				generateFakeResults
		);
		
		// Add a bunch of fake notes.
		if(generateFakeResults) {
			Log.v(TAG, "Generating Notes");
			int daysOfResults = 2000;
			Random rand = new Random();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -2);
			
			for(int i = daysOfResults; i >= 0; --i) {
				// create a note 20% of the time.
				if(rand.nextInt(10) < 8) {
					continue;
				}
				
				cal.set(Calendar.HOUR_OF_DAY, i % 24);
				cal.set(Calendar.MINUTE, i % 60);
				
				Note note = new Note(dbAdapter);
				note.note = "Test Note "+i;
				note.timestamp = cal.getTimeInMillis();
				note.save();
				
				cal.add(Calendar.DAY_OF_YEAR, -1);
			}
		}
	}
	
	private static ArrayList<Scale> createGroupAndScales(DBAdapter dbAdapter, String groupName, int isReverseData, String[] minValues, String[] maxValues, boolean generateFake) {
		ArrayList<Scale> scales = new ArrayList<Scale>();

		// Install the first group of scales
		Group group = new Group(dbAdapter);
		Scale scale = new Scale(dbAdapter);

		// Create the group
		Log.v(TAG, "Generating group");
		group = new Group(dbAdapter);
		group.title = groupName;
		group.immutable = 1;
		group.inverseResults = isReverseData > 0?true:false;
		group.save();

		// Create the scales
		Log.v(TAG, "Generating scales");
		int maxIndex = (minValues.length < maxValues.length)?minValues.length:maxValues.length;
		for(int i = 0; i < maxIndex; i++) {
			scale = new Scale(dbAdapter);
			scale.group_id = group._id;
			scale.max_label = maxValues[i];
			scale.min_label = minValues[i];
			scale.save();
			scales.add(scale);
		}

		if(generateFake) {
			Log.v(TAG, "Generating results");
			generateFakeData(dbAdapter, scales);
		}

		return scales;
	}

	public static void generateFakeData(DBAdapter dbAdapter, ArrayList<Scale> scales) {
		// Create bogus data for the generated scales
		//Log.v(TAG, "Generating results");
		Result result = new Result(dbAdapter);
		ContentValues c = new ContentValues();
		int daysOfResults = 1000;
		Random rand = new Random();

		int skipDay = rand.nextInt(27) + 1;
		/*boolean[] skipRecord = new boolean[daysOfResults];
		for(int i = 0; i < skipRecord.length; i++) {
			skipRecord[i] = false;
		}*/

		for(int i = 0; i < scales.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_YEAR, (daysOfResults + 1)*-1);

			Scale tmpScale = scales.get(i);
			int prevValue = 50;

			//Log.v(TAG, "Scale:"+tmpScale._id);
			for(int j = 0; j < daysOfResults; j++) {
				cal.add(Calendar.DAY_OF_YEAR, 1);

				// Skip a day every so often
				if(cal.get(Calendar.DAY_OF_MONTH) % skipDay == 0) {
					continue;
				}
				/*
				if(skipRecord[j] || (j > 0 && j < daysOfResults-1 && rand.nextInt(11) < 2)) {
					if(i == 0) {
						skipRecord[j] = true;
					}
					continue;
				}*/
				int value = prevValue + 10 - rand.nextInt(21);
				value = (value < 0)?0:value;
				value = (value > 100)?100:value;
				//Log.v(TAG, "V:"+value);

				c = new ContentValues();
				c.put("group_id", tmpScale.group_id);
				c.put("scale_id", tmpScale._id);
				c.put("timestamp", cal.getTimeInMillis());
				c.put("value", value);
				result.insert(c);

				//Log.v(TAG, "gid:"+tmpScale.group_id+" sid:"+tmpScale._id+" v:"+value+" ts:"+cal.getTimeInMillis());

				prevValue = value;
			}
		}
	}
}

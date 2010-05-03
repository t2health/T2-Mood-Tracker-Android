package com.t2.vas.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import com.t2.vas.R;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

import android.content.ContentValues;
import android.content.res.Resources;
import android.util.Log;

public class InstallDB {
	private static final String TAG = InstallDB.class.getName();
	
	private static ArrayList<Scale> createGroupAndScales(DBAdapter dbAdapter, String groupName, String[] minValues, String[] maxValues, boolean generateFake) {
		ArrayList<Scale> scales = new ArrayList<Scale>();
		
		// Install the first group of scales
		Group group = (Group)dbAdapter.getTable("group");
		Scale scale = (Scale)dbAdapter.getTable("scale");
		
		// Create the group
		Log.v(TAG, "Generating group");
		group = group.newInstance();
		group.title = groupName;
		group.immutable = 1;
		group.save();
		
		// Create the scales
		Log.v(TAG, "Generating scales");
		int maxIndex = (minValues.length < maxValues.length)?minValues.length:maxValues.length;
		for(int i = 0; i < maxIndex; i++) {
			scale = scale.newInstance();
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
	
	public static void onCreate(DBAdapter dbAdapter) {

		// Install the first group of scales
		boolean generateFake = true;
		Group group = (Group)dbAdapter.getTable("group");
		Scale scale = (Scale)dbAdapter.getTable("scale");
		
		Resources res = dbAdapter.getContext().getResources();
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group1),
				res.getStringArray(R.array.group1_min),
				res.getStringArray(R.array.group1_max),
				generateFake
		);
		
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group2),
				res.getStringArray(R.array.group2_min),
				res.getStringArray(R.array.group2_max),
				generateFake
		);
		
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group3),
				res.getStringArray(R.array.group3_min),
				res.getStringArray(R.array.group3_max),
				generateFake
		);
		
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group4),
				res.getStringArray(R.array.group4_min),
				res.getStringArray(R.array.group4_max),
				generateFake
		);
		
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group5),
				res.getStringArray(R.array.group5_min),
				res.getStringArray(R.array.group5_max),
				generateFake
		);
		
		createGroupAndScales(
				dbAdapter,
				res.getString(R.string.group6),
				res.getStringArray(R.array.group6_min),
				res.getStringArray(R.array.group6_max),
				generateFake
		);
	}
	
	private static void generateFakeData(DBAdapter dbAdapter, ArrayList<Scale> scales) {
		// Create bogus data for the generated scales
		//Log.v(TAG, "Generating results");
		Result result = ((Result)dbAdapter.getTable("result")).newInstance();
		ContentValues c = new ContentValues();
		int resultCount = 20;
		Random rand = new Random();
		for(int i = 0; i < scales.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, (resultCount + 1)*-1);
			
			Scale tmpScale = scales.get(i);
			int prevValue = 50;
			
			//Log.v(TAG, "Scale:"+tmpScale._id);
			for(int j = 0; j < resultCount; j++) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				
				// Skip a day 10% of the time
				if(j > 0 && j < resultCount && rand.nextInt(11) < 2) {
					continue;
				}
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

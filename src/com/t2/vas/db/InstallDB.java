package com.t2.vas.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

import android.util.Log;

public class InstallDB {
	private static final String TAG = InstallDB.class.getName();
	
	public static void onCreate(DBAdapter dbAdapter) {

		// Install the first group of scales
		Group group = (Group)dbAdapter.getTable("group");
		Scale scale = (Scale)dbAdapter.getTable("scale");
		
		// Create the group
		Log.v(TAG, "Generating group");
		group = group.newInstance();
		group.title = "How are you feeling today?";
		group.save();
		
		// Create the scales
		Log.v(TAG, "Generating scales");
		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Happy";
		scale.min_label = "Sad";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Energetic";
		scale.min_label = "Tired";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Calm";
		scale.min_label = "Worried";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Relaxed";
		scale.min_label = "Tense";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Optimistic";
		scale.min_label = "Pessimistic";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Hopeful";
		scale.min_label = "Hopeless";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Connected";
		scale.min_label = "Alone";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Safe";
		scale.min_label = "Unsafe";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Loved";
		scale.min_label = "Unloved";
		scale.weight = 0;
		scale.save();

		scale = scale.newInstance();
		scale.group_id = group._id;
		scale.max_label = "Content";
		scale.min_label = "Angry";
		scale.weight = 0;
		scale.save();
		
		// Create bogus data for the generated scales
		Log.v(TAG, "Generating results");
		int resultCount = 20;
		Random rand = new Random();
		ArrayList<Scale> scales = group.getScales();
		for(int i = 0; i < scales.size(); i++) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, resultCount*-1);
			
			Scale tmpScale = scales.get(i);
			int prevValue = 50;
			
			Log.v(TAG, "Scale:"+tmpScale._id);
			for(int j = 0; j < resultCount; j++) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				
				// Skip a day 10% of the time
				if(rand.nextInt(11) < 2) {
					continue;
				}
				int value = prevValue + 10 - rand.nextInt(21);
				
				Result tmpResult = ((Result)dbAdapter.getTable("result")).newInstance();
				tmpResult.group_id = group._id;
				tmpResult.scale_id = tmpScale._id;
				tmpResult.timestamp = cal.getTimeInMillis();
				tmpResult.value = value;
				tmpResult.save();
				
				Log.v(TAG, "   Result:"+tmpResult._id+" group_id="+tmpResult.group_id+" scale_id="+tmpResult.scale_id+" timestamp="+tmpResult.timestamp+" value="+tmpResult.value);
				
				prevValue = value;
			}
		}
	}
}

/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.t2.vas.R;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.ScaleResultsDataProvider;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

public class ScaleResultsActivity extends ABSResultsActivity {
	public static final String EXTRA_GROUP_ID = "groupId";
	private long groupId;
	private Group group;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Intent intent = this.getIntent();
		groupId = intent.getLongExtra(EXTRA_GROUP_ID, 0);
		if(groupId <= 0) {
			finish();
			return;
		}
		
		super.onCreate(savedInstanceState);
		
		group = new Group(this.dbAdapter);
		group._id = groupId;
		if(!group.load()) {
			finish();
			return;
		}
		
		this.reverseLabels = !group.inverseResults;
		
		this.setTitle(group.title);
	}
	
	@Override
	protected double getValue(KeyItem item, double value) {
		return value;
	}

	@Override
	protected String getSettingSuffix() {
		return "scale";
	}

	@Override
	protected ArrayList<KeyItem> getKeyItems() {
		group = new Group(this.dbAdapter);
		group._id = groupId;
		group.load();
		
		ArrayList<Scale> scales = group.getScales();
		ArrayList<KeyItem> items = new ArrayList<KeyItem>();
		
		for(int i = 0; i < scales.size(); ++i) {
			Scale scale = scales.get(i);
			items.add(new KeyItem(
					scale._id,
					scale.max_label,
					scale.min_label
			));
		}
		return items;
	}

	protected boolean isKeyItemsClickable() {
		return false;
	}
	
	@Override
	protected int getKeyItemViewType() {
		return KeyItemAdapter.VIEW_TYPE_TWO_LINE;
	}

	@Override
	protected void onKeysItemClicked(KeyItem keyItem, View view, int pos,
			long id) {
		return;
	}

	@Override
	protected DataProvider getDataProvider() {
		return new ScaleResultsDataProvider(this.dbAdapter);
	}

	@Override
	protected String getKeyTabText() {
		return getString(R.string.scales_tab);
	}
}

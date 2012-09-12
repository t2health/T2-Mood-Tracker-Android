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

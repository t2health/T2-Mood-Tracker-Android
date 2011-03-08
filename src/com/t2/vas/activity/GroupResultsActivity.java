package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.tables.Group;

public class GroupResultsActivity extends ABSResultsActivity {
	private static final String TAG = "GroupResultsActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected String getSettingSuffix() {
		return "group";
	}
	
	@Override
	protected double getValue(KeyItem item, double value) {
		if(item.reverseData) {
			return value;
		}
		return 100 - value;
	}

	@Override
	protected ArrayList<KeyItem> getKeyItems() {
		ArrayList<KeyItem> items = new ArrayList<KeyItem>();
		List<Long> hiddenGids = SharedPref.getHiddenGroups(sharedPref);
		
		Cursor cursor = new Group(this.dbAdapter).getGroupsWithScalesCursor();
		while(cursor.moveToNext()) {
			Group group = new Group(dbAdapter);
			group.load(cursor);
			
			if(!hiddenGids.contains(group._id)) {
				KeyItem item = new KeyItem(
						group._id,
						group.title,
						null
				);
				item.reverseData = group.inverseResults;
				items.add(item);
			}
		}
		cursor.close();
		
		return items;
	}
	
	@Override
	protected int getKeyItemViewType() {
		return KeyItemAdapter.VIEW_TYPE_ONE_LINE;
	}

	@Override
	protected void onKeysItemClicked(KeyItem keyItem, View view, int pos,
			long id) {
		Intent i = new Intent(this, ScaleResultsActivity.class);
		i.putExtra(ScaleResultsActivity.EXTRA_GROUP_ID, keyItem.id);
		i.putExtra(ScaleResultsActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		i.putExtra(ScaleResultsActivity.EXTRA_TIME_START, this.startCal.getTimeInMillis());
		i.putExtra(ScaleResultsActivity.EXTRA_CALENDAR_FIELD, this.calendarField);
		this.startActivity(i);
	}
	
	@Override
	protected DataProvider getDataProvider() {
		return new GroupResultsDataProvider(this.dbAdapter);
	}

	@Override
	protected boolean isKeyItemsClickable() {
		return true;
	}

	@Override
	protected String getKeyTabText() {
		return getString(R.string.groups_tab);
	}
}

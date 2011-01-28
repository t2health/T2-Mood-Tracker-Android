package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewAnimator;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.VASAnalytics;
import com.t2.vas.data.DataProvider;
import com.t2.vas.data.GroupResultsDataProvider;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class GroupResultsActivity extends ABSResultsActivity {
	private static final String TAG = "GroupResultsActivity";
	
	@Override
	protected String getSettingSuffix() {
		return "group";
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
				items.add(new KeyItem(
						group._id,
						group.title,
						null
				));
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
}

package com.t2.vas.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimerTask;

import com.t2.chart.widget.Chart;
import com.t2.vas.Global;
import com.t2.vas.GroupResultsSeriesDataAdapter;
import com.t2.vas.GroupNotesSeriesDataAdapter;
import com.t2.vas.R;
import com.t2.vas.ScaleKeyAdapter;
import com.t2.vas.ScaleResultsSeriesDataAdapter;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.db.tables.Scale.ResultValues;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.ResultsLayout;
import com.t2.vas.view.ResultsLayoutAdapter;
import com.t2.vas.view.ResultsAnimator;
import com.t2.vas.view.VASGallerySimpleAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ResultsActivity extends ABSActivity implements OnClickListener, OnItemSelectedListener {
	private static final String TAG = "ResultsActivity";
	private static final int NOTES_MANAGE = 234;
	private static final int SHARE_RESULTS = 452;

	private LinkedHashMap<Long, ChartLayout> chartLayouts = new LinkedHashMap<Long, ChartLayout>();

	private long activeGroupId;
	private LayoutInflater layoutInflater;

	private ListView keyListView;
	private ScaleKeyAdapter keyListAdapter;

	private int resultsGroupBy = ScaleResultsSeriesDataAdapter.GROUPBY_DAY;
	private ChartLayout currentChartLayout;
	private FrameLayout chartsContainer;
	private Animation chartInAnimation;
	private Animation chartOutAnimation;
	private AnimationSet flashAnimation;
	private ChartLayout groupChartLayout;
	private Group activeGroup;
//	private DBAdapter dbAdapter;
	private Group currentGroup;
	private ResultsAnimator resultsAnimator;
	private ArrayList<Group> groupList;
	private long startGroupId;
	private Gallery groupGallery;
	private ArrayList<HashMap<String, Object>> groupAdapterList;


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.results_activity);
        VASAnalytics.onEvent(VASAnalytics.EVENT_RESULTS_ACTIVITY);

        this.findViewById(R.id.addNoteButton).setOnClickListener(this);
        this.findViewById(R.id.notesButton).setOnClickListener(this);

        ViewGroup globalBar = (ViewGroup)this.findViewById(R.id.globalButtonBar);
        if(globalBar != null) {
	        globalBar.removeView(globalBar.findViewById(R.id.notesButton));
        }

        startGroupId = this.getIntent().getLongExtra("group_id", 0);

        // init the chart layout animator
        resultsAnimator = (ResultsAnimator)this.findViewById(R.id.resultsAnimator);

        // get a list of the current list of groups
        currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
        groupList = currentGroup.getGroupsWithResults();

        if(groupList.size() == 0) {
        	this.finish();
        }

        // Configure the group gallery
        groupGallery = (Gallery)this.findViewById(R.id.galleryList);
        groupGallery.setOnItemSelectedListener(this);
        groupGallery.setCallbackDuringFling(false);

        groupAdapterList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> groupItem;

        for(int i = 0; i < groupList.size(); i++) {
        	groupItem = new HashMap<String, Object>();
        	groupItem.put("groupId", groupList.get(i)._id);
        	groupItem.put("text1", groupList.get(i).title);
        	groupAdapterList.add(groupItem);
        }

        groupGallery.setAdapter(new VASGallerySimpleAdapter(
            	this,
            	groupAdapterList,
            	R.layout.main_activity2_gallery_list_item,
            	new String[]{
            			"text1",
            	},
            	new int[] {
            			R.id.text1,
            	}
        ));



        resultsAnimator.setAdapter(new ResultsLayoutAdapter(
        		this,
        		this.dbAdapter,
        		groupList
        ));

        // Tell the flipper to start at a specific index.
        int startIndex = 0;
        for(int i = 0; i < groupList.size(); i++) {
        	Group g = groupList.get(i);
        	if(g._id == startGroupId) {
        		startIndex = i;
        		break;
        	}
        }

        this.groupGallery.setSelection(startIndex);


        // Show help toast.
        Toast toast = Toast.makeText(this, R.string.results_choose_chart, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();


        toast = Toast.makeText(this, R.string.add_notes_popup, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != NOTES_MANAGE) {
			return;
		}

		Log.v(TAG, "REFRESH");
		Chart chart = (Chart)this.resultsAnimator.getCurrentView().findViewById(R.id.groupChart).findViewById(R.id.chart);
		chart.updateChart();


		super.onActivityResult(requestCode, resultCode, data);
	}



	private long getSelectedGroupId() {
		HashMap<String, Object> ob = (HashMap<String, Object>)this.groupGallery.getSelectedItem();
		Long val = (Long)ob.get("groupId");
		if(val == null) {
			return -1;
		}
		return val;
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.notesButton:
				this.viewNotesPressed();
				return;

			case R.id.addNoteButton:
				this.addNotesPressed();
				return;
		}

		super.onClick(v);
	}

	private ResultsLayout getSelectedResultsLayout() {
		return (ResultsLayout)this.resultsAnimator.getCurrentView();
	}

	private void addNotesPressed() {
		ResultsLayout rl = this.getSelectedResultsLayout();

		long[] range = rl.getActiveChartTimeRange();
		Intent i = new Intent(this, NoteActivity.class);
		i.putExtra("timestamp", range[0]);

		this.startActivityForResult(i, NOTES_MANAGE);
	}

	private void viewNotesPressed() {
		ResultsLayout rl = this.getSelectedResultsLayout();

		long[] range = rl.getActiveChartTimeRange();
		Intent i = new Intent(this, NotesDialogActivity.class);
		i.putExtra("start_timestamp", range[0]);
		i.putExtra("end_timestamp", range[1]);

		this.startActivityForResult(i, NOTES_MANAGE);
	}



	@Override
	public int getHelpResId() {
		return R.string.results_help;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		VASAnalytics.onEvent(VASAnalytics.EVENT_GROUP_SELECTED);
		resultsAnimator.showAt(arg2);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.putExtra("group_id", this.getSelectedGroupId());
			this.setResult(Activity.RESULT_OK, i);
			this.finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}

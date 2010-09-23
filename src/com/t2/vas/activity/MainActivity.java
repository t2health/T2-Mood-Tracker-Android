package com.t2.vas.activity;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.t2.vas.Global;
import com.t2.vas.GroupResultsSeriesDataAdapter;
import com.t2.vas.R;
import com.t2.vas.ScaleResultsSeriesDataAdapter;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.editor.ScaleListActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.GroupGallery;
import com.t2.vas.view.VASGallerySimpleAdapter;

public class MainActivity extends ABSActivity implements OnItemSelectedListener, OnClickListener, OnItemClickListener {
	private static final String TAG = MainActivity.class.getName();

	private Group currentGroup;
	private GroupGallery groupGallery;
	private ArrayList<Group> groupList;
	private ArrayList<HashMap<String, Object>> groupAdapterList = new ArrayList<HashMap<String,Object>>();

	private Animation categoryTasksInAnimaton;

	private Animation categoryTasksOutAnimaton;

	private LayoutInflater layoutInflater;

	private HashMap<String, Object> prevSelectedObject;

	private ViewAnimator taskAnimimator;


	private int previousSelectedIndex = -1;

	private VASGallerySimpleAdapter galleryAdapter;

	private Toast slideToast;

	private TimerTask slideToastTask;

	private Timer slideToastTimer;

	private Animation flashAnimation;
	
//	private SelectGroupIndexHandler selectGroupIndexHandler = new SelectGroupIndexHandler();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.main_activity);
        VASAnalytics.onEvent(VASAnalytics.EVENT_MAIN_ACTIVITY);
        
        layoutInflater = this.getLayoutInflater();

        currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();

        groupGallery = (GroupGallery)this.findViewById(R.id.galleryList);
        groupGallery.setCallbackDuringFling(false);
        
       /* this.galleryAdapter = new VASGallerySimpleAdapter(
            	this,
            	groupAdapterList,
            	R.layout.main_activity_gallery_list_item,
            	new String[]{
            			"text1",
            	},
            	new int[] {
            			R.id.text1,
            	}
        );
        groupGallery.setAdapter(this.galleryAdapter);*/

        // Init the list of items.
        this.initAdapterData();
        
        groupGallery.setOnItemSelectedListener(this);
        groupGallery.dispatchSetSelected(true);
        groupGallery.setSelection(0);
        
    	this.taskAnimimator = (ViewAnimator)this.findViewById(R.id.taskAnimator);
    	this.taskAnimimator.setVisibility(View.VISIBLE);
	}
	
	


	private void initAdapterData() {
        Group currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
        groupList = currentGroup.getGroupsOrderByLastResult();

        groupAdapterList.clear();
//        groupAdapterList = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> groupItem;

        groupItem = new HashMap<String, Object>();
        groupItem.put("layoutResId", R.layout.main_activity_intro_task);
    	groupItem.put("text1", "Introduction");
    	groupAdapterList.add(groupItem);

        for(int i = 0; i < groupList.size(); i++) {
        	groupItem = new HashMap<String, Object>();
        	groupItem.put("layoutResId", R.layout.main_activity_group_task);
        	groupItem.put("groupId", groupList.get(i)._id);
        	groupItem.put("text1", groupList.get(i).title);
        	groupAdapterList.add(groupItem);
        }

        groupItem = new HashMap<String, Object>();
        groupItem.put("layoutResId", R.layout.main_activity_add_group_task);
    	groupItem.put("text1", "Add Custom");
    	groupAdapterList.add(groupItem);

//    	this.galleryAdapter.notifyDataSetChanged();
    	
    	this.galleryAdapter = new VASGallerySimpleAdapter(
            	this,
            	groupAdapterList,
            	R.layout.main_activity_gallery_list_item,
            	new String[]{
            			"text1",
            	},
            	new int[] {
            			R.id.text1,
            	}
        );
        groupGallery.setAdapter(this.galleryAdapter);
	}

	private void showGroupGalleryItem(int arg2, boolean animate) {
//		Log.v(TAG, "Selected!");
		HashMap<String, Object> ob = (HashMap<String, Object>) this.groupGallery.getItemAtPosition(arg2);

		int layoutResId = (Integer)ob.get("layoutResId");
		ViewGroup v = (ViewGroup)this.layoutInflater.inflate(layoutResId, null);

		if(layoutResId == R.layout.main_activity_group_task) {
			VASAnalytics.onEvent(VASAnalytics.EVENT_GROUP_SELECTED);

			Group group = ((Group)this.dbAdapter.getTable("group")).newInstance();
			group._id = this.getSelectedGroupId();
			group.load();

			// Set the last rated date.
			long latestResult = group.getLatestResultTimestamp();
			String latestResultString = "Never";
			if(latestResult > 0) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(latestResult);
				SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");

				latestResultString = sdf.format(cal.getTime());
			}
			((TextView)v.findViewById(R.id.lastRatedDate)).setText(latestResultString);


			/*// Set the background of this view to be a chart.
			Display display = this.getWindowManager().getDefaultDisplay();
			((ImageView)v.findViewById(R.id.background)).setBackgroundDrawable(
					this.averageChart(group, display.getWidth(), display.getHeight())
			);*/
			((TextView)v.findViewById(R.id.groupTitle)).setText(group.title);

			View formButton = v.findViewById(R.id.formButton);
        	formButton.setOnClickListener(this);

        	View resultsButton = v.findViewById(R.id.resultsButton);
        	resultsButton.setOnClickListener(this);

        	View editGroupButton = v.findViewById(R.id.editGroupButton);
        	editGroupButton.setOnClickListener(this);

        	/*View manageScalesButton = v.findViewById(R.id.manageScalesButton);
        	manageScalesButton.setOnClickListener(this);

        	if(group.immutable > 0) {
        		//editGroupButton.setVisibility(View.GONE);
        		manageScalesButton.setVisibility(View.GONE);
        	}*/
        	
        	if(!group.hasScales()) {
        		formButton.setEnabled(false);
        	}

        	if(!group.hasResults()) {
        		resultsButton.setEnabled(false);
        	}
        	
		} else if(layoutResId == R.layout.main_activity_add_group_task) {
			VASAnalytics.onEvent(VASAnalytics.EVENT_ADD_GROUP_SELECTED);
			v.findViewById(R.id.addGroupButton).setOnClickListener(this);

		} else if(layoutResId == R.layout.main_activity_intro_task) {
			VASAnalytics.onEvent(VASAnalytics.EVENT_INTRO_SELECTED);
			v.findViewById(R.id.aboutButton).setOnClickListener(this);
		}

		
		this.taskAnimimator.addView(v);

//		this.taskAnimimator.showNext();
		// Show the next view.
		if(animate) {
			Log.v(TAG, "Do animate");
			
			this.taskAnimimator.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.select_group));
			this.taskAnimimator.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.deselect_group));
			
			this.taskAnimimator.showNext();
		} else {
			Log.v(TAG, "Don't animate");
			
			this.taskAnimimator.setInAnimation(new AlphaAnimation(1.0f, 1.0f));
			this.taskAnimimator.setOutAnimation(new AlphaAnimation(1.0f, 1.0f));
			
			this.taskAnimimator.showNext();
		}
		
		if(this.taskAnimimator.getChildCount() > 1) {
			this.taskAnimimator.removeViewAt(0);
		}

		this.previousSelectedIndex = arg2;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(this.previousSelectedIndex == arg2) {
			showGroupGalleryItem(arg2, false);
		} else {
			showGroupGalleryItem(arg2, true);
		}
	}

	/*private BitmapDrawable averageChart(Group group, int width, int height) {
		Calendar startTimeCal = Calendar.getInstance();
		startTimeCal.add(Calendar.DAY_OF_MONTH, -10);

		LineSeries lineSeries = new LineSeries("");
		lineSeries.setLineFillColor(Color.parseColor("#FF004190"));
		lineSeries.setLineStrokeColor(Color.parseColor("#00002859"));
		lineSeries.setFillColor(Color.parseColor("#FF004190"));
		lineSeries.setStrokeColor(Color.parseColor("#FF002859"));
        lineSeries.setSeriesDataAdapter(new GroupResultsSeriesDataAdapter(
        		this.dbHelper,
        		startTimeCal.getTimeInMillis(),
        		group._id,
        		ScaleResultsSeriesDataAdapter.GROUPBY_DAY,
        		Global.CHART_LABEL_DATE_FORMAT
        ));

        Chart chart = new Chart(this);
        chart.addSeries("main", lineSeries);
        chart.setShowYHilight(false);
        chart.setShowAxis(false);
        chart.setInteractiveModeEnabled(false);
        chart.setDrawingCacheEnabled(true);
        chart.layout(0, 0, width, height);
        chart.buildDrawingCache();


        BitmapDrawable bd = new BitmapDrawable(ChartBitmapFactory.getBitmap(
				this,
				chart,
				width,
				height
		));
        chart.setDrawingCacheEnabled(false);
        return bd;
	}*/

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.formButton:
			case R.id.resultsButton:
			case R.id.addGroupButton:
			case R.id.editGroupButton:
			//case R.id.manageScalesButton:
			case R.id.aboutButton:
				this.localOnClick(arg0);
				return;
		}

		super.onClick(arg0);
	}

	private void localOnClick(View arg0) {
		Intent i = new Intent();

		switch(arg0.getId()) {
			case R.id.formButton:
				i.setAction("com.t2.vas.FormActivity");
				i.putExtra("group_id", this.getSelectedGroupId());
				this.startActivityForResult(i, FORM_ACTIVITY);
				return;

			case R.id.resultsButton:
				this.startResultsActivity();
				return;

			case R.id.addGroupButton:
				i.setAction("com.t2.vas.editor.GroupActivity");
				this.startActivityForResult(i, ADD_GROUP_ACTIVITY);
				return;

			case R.id.editGroupButton:
				i.setAction("com.t2.vas.editor.EditGroupActivity");
				i.putExtra("group_id", this.getSelectedGroupId());
				this.startActivityForResult(i, EDIT_GROUP_ACTIVITY);
				return;

			case R.id.aboutButton:
				i = new Intent(this, AboutActivity.class);
				this.startActivityForResult(i, ABOUT_ACTIVITY);
				return;
		}
	}

	private long getSelectedGroupId() {
		HashMap<String, Object> ob = (HashMap<String, Object>)this.groupGallery.getSelectedItem();
		if(ob == null || ob.get("groupId") == null) {
			return 0;
		}

		return (Long)ob.get("groupId");
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		int selectedIndex = this.groupGallery.getSelectedItemPosition();

		if(selectedIndex != arg2) {
			return;
		}

		Long groupId = this.getSelectedGroupId();

		if(groupId == null) {
			return;
		}

		String action = this.getIntent().getStringExtra("action");
		Intent i = new Intent();
		i.setAction(action);
		i.putExtra("group_id", groupId);

		if(action != null && action.length() > 0) {
			this.startActivity(i);
			this.finish();
		}
		//Log.v(TAG, "Currently Slected: "+ arg0.getSelectedItemPosition());
		//Log.v(TAG, "Click "+ arg2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If the user is coming form the form activity, start the results activity.
		if(requestCode == FORM_ACTIVITY && resultCode == Activity.RESULT_OK) {
			this.startResultsActivity();
			return;
		}

		long retGroupId = 0;
		int retGroupIndex = -1;
		if(data != null) {
			retGroupId = data.getLongExtra("group_id", 0);
		}
		
		long selGroupId = this.getSelectedGroupId();
		int selIndex = this.groupGallery.getSelectedItemPosition();
		
		this.initAdapterData();
		
		retGroupIndex = this.getGalleryIndexForGroupId(retGroupId);
		int selGroupIndex = this.getGalleryIndexForGroupId(selGroupId);
		
		this.groupGallery.setSelection(Gallery.INVALID_POSITION);
		
		if(requestCode == EDIT_GROUP_ACTIVITY) {
			// Re-select the group.
			if(selGroupIndex >= 0) {
				Log.v(TAG, "Sel group after edit.");
				this.groupGallery.setSelection(selGroupIndex);
				
				return;
				/*if(this.previousSelectedIndex == selGroupIndex) {
					showGroupGalleryItem
				}*/
				
			// This group was deleted, select the last selected index.
			} else {
				if(selIndex > this.groupGallery.getCount()) {
					selIndex = this.groupGallery.getCount() -1;
				}
				Log.v(TAG, "Group was deleted, sel "+ selIndex);
				this.groupGallery.setSelection(selIndex);
				return;
			}
		}
		
		if(requestCode == ADD_GROUP_ACTIVITY) {
			// A new group was added, select it.
			if(retGroupIndex >= 0) {
				Log.v(TAG, "New group added.");
				this.groupGallery.setSelection(retGroupIndex);
				return;
			} else {
				Log.v(TAG, "Add group cancelled.");
				this.groupGallery.setSelection(this.groupGallery.getCount() - 1);
				return;
			}
		}
		
		// We are returning from some other activity.
		if(retGroupIndex >= 0) {
			Log.v(TAG, "Group id returned from activity.");
			this.previousSelectedIndex = retGroupIndex;
			this.groupGallery.setSelection(retGroupIndex);
			return;
		}
		
		// We are returning from some other activity.
		if(selGroupIndex >= 0) {
			Log.v(TAG, "Selecting the previously selected group.");
			this.groupGallery.setSelection(selGroupIndex);
			return;
		}
	}

	private int getGalleryIndexForGroupId(long groupId) {
		for(int i = 0; i < this.groupAdapterList.size(); i++) {
			HashMap<String, Object> obj = this.groupAdapterList.get(i);
			Long lGroupId = (Long)obj.get("groupId");

			if(lGroupId != null && lGroupId == groupId) {
				return i;
			}
		}
		return -1;
	}

	private void startResultsActivity() {
		Intent i = new Intent();
		i.setAction("com.t2.vas.ResultsActivity");
		i.putExtra("group_id", this.getSelectedGroupId());
		this.startActivityForResult(i, RESULTS_ACTIVITY);
	}
}

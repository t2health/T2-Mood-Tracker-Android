package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ScaleKeyAdapter;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.db.tables.Scale.ResultValues;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.chart.LineSeries;
import com.t2.vas.view.chart.NotesSeries;
import com.t2.vas.view.chart.ScaleSeriesDataAdapter;
import com.t2.vas.view.chart.Series;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final String TAG = "ResultsActivity";
	private static final int NOTES_MANAGE = 234;
	
	private LinkedHashMap<Long, ChartLayout> chartLayouts = new LinkedHashMap<Long, ChartLayout>();
	
	private long activeGroupId;
	private LayoutInflater layoutInflater;
	
	private ListView keyListView;
	private ScaleKeyAdapter keyListAdapter;
	
	private int resultsGroupBy = ScaleSeriesDataAdapter.GROUPBY_DAY;
	private ChartLayout currentChartLayout;
	private FrameLayout chartsContainer;
	private Animation chartInAnimation;
	private AnimationSet flashAnimation;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        activeGroupId = this.getIntent().getLongExtra("group_id", -1);
        
        ArrayList<Series> chartSeries = new ArrayList<Series>();
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        LinearLayout l = (LinearLayout)layoutInflater.inflate(R.layout.results_activity, null);
        keyListView = (ListView)l.findViewById(R.id.list);
        chartsContainer = (FrameLayout)l.findViewById(R.id.charts);
        chartInAnimation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
        
        // Build the animation that let the user know they already have selected
        // that chart.
        flashAnimation = new AnimationSet(true);
		AlphaAnimation alphaAnim;
		alphaAnim = new AlphaAnimation(1.0f, 0.5f);
		alphaAnim.setDuration(250);
		flashAnimation.addAnimation(alphaAnim);
		alphaAnim = new AlphaAnimation(0.5f, 1.0f);
		alphaAnim.setDuration(250);
		alphaAnim.setStartOffset(250);
		flashAnimation.addAnimation(alphaAnim);
        
		
        DBAdapter db = new DBAdapter(this, Global.Database.name, Global.Database.version);
        db.open();
        
        // ensure the group privided exists.
        Group g = (Group)db.getTable("group").newInstance();
        g._id = activeGroupId;
        if(!g.load()) {
        	this.finish();
        	return;
        }
        
        // Create the chart for each scale.
        ArrayList<Scale> scales = g.getScales();
        int scaleCount = scales.size();
        for(int i = 0; i < scaleCount ; i++) {
        	Scale s = scales.get(i);
        	NotesSeries notesSeries = new NotesSeries("Notes");
        	LineSeries lineSeries = new LineSeries(s.max_label +" - "+ s.min_label);
        	
        	lineSeries.setSeriesDataAdapter(new ScaleSeriesDataAdapter(
        			db,
        			s._id,
        			resultsGroupBy,
        			Global.CHART_LABEL_DATE_FORMAT
        	));
        	
        	notesSeries.setSeriesDataAdapter(new ScaleSeriesDataAdapter(
        			db,
        			s._id,
        			resultsGroupBy,
        			Global.CHART_LABEL_DATE_FORMAT
        	));
        	
        	ChartLayout chartLayout = (ChartLayout)layoutInflater.inflate(R.layout.chart_layout, null);
        	chartLayout.setYMaxLabel(s.max_label);
        	chartLayout.setYMinLabel(s.min_label);
        	chartLayout.setTag((Long)s._id);
        	
        	/*lineSeries.setSelectable(false);*/
        	
        	// Style the colors of the lines and points.
        	float hue = i / (1.00f * scaleCount) * 360.00f;
        	lineSeries.setFillColor(Color.HSVToColor(
        			255, 
        			new float[]{
        				hue,
        				1.0f,
        				1.0f
        			}
        	));
        	lineSeries.setStrokeColor(Color.HSVToColor(
        			255, 
        			new float[]{
        				hue,
        				1.0f,
        				0.25f
        			}
        	));
        	
        	lineSeries.setLineFillColor(Color.HSVToColor(
        			255, 
        			new float[]{
        				hue,
        				0.65f,
        				1.0f
        			}
        	));
        	lineSeries.setLineStrokeColor(Color.HSVToColor(
        			255, 
        			new float[]{
        				hue,
        				0.5f,
        				0.5f
        			}
        	));
        	
        	// Add the series and add the chart to the list of charts.
        	chartLayout.getChart().addSeries("main", lineSeries);
        	chartLayout.getChart().addSeries("notes", notesSeries);
        	//chartLayouts.add(chartLayout);
        	chartLayouts.put(s._id, chartLayout);
        }
        
        ArrayList<ChartLayout> chartLayoutsList = new ArrayList<ChartLayout>();
        chartLayoutsList.addAll(chartLayouts.values());
        
        //keyListAdapter = new ScaleKeyAdapter(this, R.layout.key_box_adapter_list_label_right, chartLayouts);
        keyListAdapter = new ScaleKeyAdapter(this, R.layout.key_box_adapter_list_label_right, chartLayoutsList);
        keyListView.setAdapter(keyListAdapter);
        keyListView.setOnItemClickListener(this);
        
        this.setContentView(l);
        
        this.findViewById(R.id.notesButton).setOnClickListener(this);
        this.findViewById(R.id.notesButton).setVisibility(View.INVISIBLE);
        
        this.findViewById(R.id.addNoteButton).setOnClickListener(this);
        this.findViewById(R.id.addNoteButton).setVisibility(View.INVISIBLE);
        
        
        db.close();
        
        // Restore some of the data
        if(savedInstanceState != null) {
        	// Remember which charts are visible
        	long[] chartScaleIds = savedInstanceState.getLongArray("chartScaleIds");
        	for(int i = 0; i < chartScaleIds.length; i++) {
        		ChartLayout tmpChartLayout = this.chartLayouts.get(chartScaleIds[i]);
        		if(tmpChartLayout != null) {
        			this.showChart(tmpChartLayout, false);
        		}
        	}
        	
        	// Remember the scroll position
	        int listFirstVisible = savedInstanceState.getInt("listFirstVisible");
	        keyListView.setSelection(listFirstVisible);
        }
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Determine which charts are visible.
		ArrayList<Long> list = new ArrayList<Long>();
		for(int i = 0; i < this.chartsContainer.getChildCount(); i++) {
			View childView = this.chartsContainer.getChildAt(i);
			try {
				ChartLayout childChart = (ChartLayout)childView;
				long scaleId = (Long)childChart.getTag();
				list.add(scaleId);
			} catch (ClassCastException cce) {}
		}
		
		long[] scaleIds = new long[list.size()];
		for(int i = 0; i < scaleIds.length; i++) {
			scaleIds[i] = list.get(i);
		}
		outState.putLongArray("chartScaleIds", scaleIds);
		
		
		//store the scoll position of the key list
		outState.putInt("listFirstVisible", keyListView.getFirstVisiblePosition());
		
		
		super.onSaveInstanceState(outState);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Refresh current chart to show notes.
		if(requestCode == NOTES_MANAGE) {
			currentChartLayout.getChart().updateChart();
			currentChartLayout.invalidate();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		this.showChart((ChartLayout)arg1.getTag(), true);
	}
	
	public void showChart(ChartLayout chartLayout, boolean animate) {
		int chartBackgroundCount = 2;
		
		// Flash the chart if it already selected
		if(currentChartLayout != null && currentChartLayout.equals(chartLayout)) {
			if(animate) {
				currentChartLayout.startAnimation(flashAnimation);
			}
			return;
		}
		
		currentChartLayout = chartLayout;
		currentChartLayout.setShowLabels(true);
		currentChartLayout.setLabelsColor(Color.WHITE);
		currentChartLayout.getChart().setShowYHilight(true);
		
		// Have the parent remove the child view. This prevents an issue in the next code block.
		ViewGroup parent = (ViewGroup)currentChartLayout.getParent();
		if(parent != null) {
			parent.removeView(currentChartLayout);
		}
		
		// Remove any child views that are in excess
		this.chartsContainer.addView(currentChartLayout);
		int removeCount = this.chartsContainer.getChildCount() - chartBackgroundCount - 1;
		for(int i = 0; i < removeCount; i++) {
			this.chartsContainer.removeViewAt(0);
		}
		
		// Progressivly fade out each view behind the current one
		int childCount = chartsContainer.getChildCount();
		float startAlpha = 0.50f;
		float endAlpha = 0.25f;
		float alphaStep = (startAlpha - endAlpha) / chartBackgroundCount;
		for(int i = childCount-2; i >= 0; i--) {
			int multiplier = childCount - i - 1;
			float alphaEnd = 1 - (alphaStep * multiplier) - startAlpha;
			float alphaStart = alphaEnd + alphaStep;
			View childView = chartsContainer.getChildAt(i);
			
			AlphaAnimation alphaAnimation = new AlphaAnimation(
				alphaStart,
				alphaEnd
			);
			if(animate) {
				alphaAnimation.setDuration(1000);
			}
			alphaAnimation.setFillAfter(true);
			
			chartsContainer.getChildAt(i).startAnimation(alphaAnimation);
			
			// If the child view is a chartlayout, remove some of the extra drawables.
			try {
				ChartLayout childChart = (ChartLayout)childView;
				childChart.setShowLabels(false);
				childChart.getChart().setDropShadowEnabled(false);
				childChart.getChart().setShowYHilight(false);
			} catch (ClassCastException cce) {}
		}
		
		// Slide the new chart in.
		if(animate) {
			this.chartsContainer.getChildAt(chartsContainer.getChildCount()-1).startAnimation(chartInAnimation);
		}
		
		// Since a chart is now selected, show the notes buttons.
		this.findViewById(R.id.addNoteButton).setVisibility(View.VISIBLE);
		this.findViewById(R.id.notesButton).setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.notesButton:
			case R.id.addNoteButton:
				notesActivity(v);
				break;
		}
	}
	
	private void notesActivity(View v) {
		ChartLayout chartLayout;
		
		try {
			//chartLayout = (ChartLayout)chartViewAnimator.getCurrentView();
			chartLayout = (ChartLayout)chartsContainer.getChildAt(0);
		} catch(ClassCastException cce) {
			return;
		}
		
		if(chartLayout == null) {
			return;
		}
		
		
		Calendar cal = Calendar.getInstance();
		Date tmpDate;
		long startTimestamp = -1;
		long endTimestamp = -1;
		
		int seriesIndex = chartLayout.getChart().getYHilightSeriesIndex();
		if(seriesIndex >= 0) {
			tmpDate = (Date)chartLayout.getChart().getSeriesAt(0).getLabels().get(seriesIndex).getLabelValue();
			cal.setTime(tmpDate);
			startTimestamp = cal.getTimeInMillis();
			
			if(seriesIndex+1 < chartLayout.getChart().getSeriesAt(0).getLabels().size()) {
				tmpDate = (Date)chartLayout.getChart().getSeriesAt(0).getLabels().get(seriesIndex+1).getLabelValue();
				cal.setTime(tmpDate);
				endTimestamp = cal.getTimeInMillis();
			}
		}
		
		// stat the notes list activity
		if(v.getId() == R.id.notesButton) {
			Intent i = new Intent(this, NotesDialogActivity.class);
			i.putExtra("start_timestamp", startTimestamp);
			i.putExtra("end_timestamp", endTimestamp);
			
			this.startActivityForResult(i, NOTES_MANAGE);
			
		// Start the add note activity
		} else if(v.getId() == R.id.addNoteButton) {
			Intent i = new Intent(this, NoteActivity.class);
			i.putExtra("timstamp", startTimestamp);
			
			this.startActivityForResult(i, NOTES_MANAGE);
		}
	}
}

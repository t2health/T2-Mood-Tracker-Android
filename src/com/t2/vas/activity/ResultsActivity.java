package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.t2.chart.KeyBoxData;
import com.t2.chart.LineSeries;
import com.t2.chart.NotesSeries;
import com.t2.chart.widget.Chart;
import com.t2.chart.widget.Chart.ChartEventListener;
import com.t2.vas.Global;
import com.t2.vas.GroupNotesSeriesDataAdapter;
import com.t2.vas.GroupResultsSeriesDataAdapter;
import com.t2.vas.R;
import com.t2.vas.ScaleResultsSeriesDataAdapter;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.ChartLayout;

public class ResultsActivity extends CustomTitle implements OnClickListener, ChartEventListener, OnItemClickListener {
	public static final String EXTRA_GROUP_ID = "group_id";
	
	private static final int NOTES_MANAGE = 234;
	private static final int SHARE_RESULTS = 452;
	
	private static final String TAG = "ResultsActivity";
	private int resultsGroupBy = ScaleResultsSeriesDataAdapter.GROUPBY_DAY;
	
	private ListView keyListView;
	private ViewAnimator chartsContainer;
	private LinkedHashMap<Long, ChartLayout> chartLayouts = new LinkedHashMap<Long, ChartLayout>();
	private ArrayList<ChartLayout> chartLayoutsList = new ArrayList<ChartLayout>();
	private ChartLayout groupChartLayout;
	private ChartLayout currentChartLayout;
	private boolean initialized = false;

	private Group groupTable;

	private ScaleKeyAdapter keyListAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		long groupId = intent.getLongExtra(EXTRA_GROUP_ID, 0);
		groupTable = new Group(this.dbAdapter);
		groupTable._id = groupId;
		if(!groupTable.load()) {
			this.finish();
		}
		
		this.setContentView(R.layout.results_activity);
		this.setTitle(groupTable.title);
		
		this.findViewById(R.id.addNoteButton).setOnClickListener(this);
		this.findViewById(R.id.notesButton).setOnClickListener(this);
		
		
		this.init(groupTable, this.dbAdapter);
		
		
		showChartAtIndex(1);
	}

	public void init(Group activeGroup, DBAdapter db) {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        keyListView = (ListView)this.findViewById(R.id.chartList);
        chartsContainer = (ViewAnimator)this.findViewById(R.id.chartsAnimator);
        chartsContainer.setAnimateFirstView(false);

        // Determine from which data to pull data.
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.add(Calendar.MONTH, -1);

        Result resultTable = (Result)db.getTable("result").newInstance();
        
        long resultTimestamp = resultTable.getMostRecentTimestamp(activeGroup._id);
        startTimeCal.setTimeInMillis(resultTimestamp);
        startTimeCal.add(Calendar.MONTH, -1);
        long startTime = startTimeCal.getTimeInMillis();
        long endTime = System.currentTimeMillis();

//        Log.v(TAG, "GROUP BY:"+resultsGroupBy);
        // Create the chart for each scale.
        ArrayList<Scale> scales = activeGroup.getScales();
        int scaleCount = scales.size();
        for(int i = 0; i < scaleCount ; i++) {
        	Scale s = scales.get(i);
        	LineSeries lineSeries = new LineSeries(s.max_label +" - "+ s.min_label);

        	lineSeries.setSeriesDataAdapter(new ScaleResultsSeriesDataAdapter(
        			db,
        			startTime,
        			endTime,
        			s._id,
        			resultsGroupBy,
        			Global.CHART_LABEL_DATE_FORMAT
        	));

        	ChartLayout chartLayout = (ChartLayout)layoutInflater.inflate(R.layout.chart_layout, null);
        	chartLayout.setScale(s);
        	chartLayout.setYMaxLabel(s.max_label);
        	chartLayout.setYMinLabel(s.min_label);
        	//chartLayout.setTag((Long)s._id);
        	chartLayout.setMaxYValue(100);

        	// Style the colors of the lines and points.
        	setLineSeriesColor(lineSeries, i+1, scaleCount+1);

        	// Add the series and add the chart to the list of charts.
        	chartLayout.getChart().setChartEventListener(this);
        	chartLayout.getChart().addSeries("main", lineSeries);
        	chartLayout.getChart().setOption(Chart.OPTION_STATIC_POINT_SPACING, 15);
        	chartLayout.getChart().setOption(Chart.OPTION_STATIC_POINT_WIDTH, 15);
        	chartLayouts.put(s._id, chartLayout);
        	
        	chartLayoutsList.add(chartLayout);
        }

        // Create the chart for the group in general
        NotesSeries notesSeries = new NotesSeries("Notes");
        notesSeries.setImageResource(R.drawable.notes_blue, this);
        notesSeries.setSeriesDataAdapter(new GroupNotesSeriesDataAdapter(
        		db,
        		startTime,
        		endTime,
        		resultsGroupBy,
        		Global.CHART_LABEL_DATE_FORMAT
        ));
        LineSeries lineSeries = new LineSeries("");
        lineSeries.setSeriesDataAdapter(new GroupResultsSeriesDataAdapter(
        		db,
        		startTime,
        		endTime,
        		activeGroup._id,
        		resultsGroupBy,
        		Global.CHART_LABEL_DATE_FORMAT
        ));
        setLineSeriesColor(lineSeries, 0, scaleCount+1);

        Scale groupScale = new Scale(this.dbAdapter);
        groupScale.max_label = getString(R.string.average_for);
        groupScale.min_label = activeGroup.title;
        groupChartLayout = (ChartLayout)this.findViewById(R.id.groupChart);
        groupChartLayout.setScale(groupScale);
        groupChartLayout.setYMaxLabel(activeGroup.title);
        groupChartLayout.getChart().setChartEventListener(this);
        groupChartLayout.getChart().addSeries("notes", notesSeries);
        groupChartLayout.getChart().addSeries("main", lineSeries);
        groupChartLayout.setShowLabels(false);
		groupChartLayout.getChart().setDropShadowEnabled(false);
		groupChartLayout.getChart().setShowYHilight(false);
		groupChartLayout.getChart().setOption(Chart.OPTION_STATIC_POINT_SPACING, 15);
		groupChartLayout.getChart().setOption(Chart.OPTION_STATIC_POINT_WIDTH, 15);
		groupChartLayout.setMaxYValue(100);
		
        this.currentChartLayout = groupChartLayout;
        chartLayoutsList.add(0, groupChartLayout);

        // Populate the key with data.
        keyListAdapter = new ScaleKeyAdapter(
        		this, 
        		R.layout.key_box_adapter_list_label_right, 
        		chartLayoutsList
		);
        keyListView.setAdapter(keyListAdapter);
        keyListView.setOnItemClickListener(this);

        this.initialized = true;
	}

	private void setLineSeriesColor(LineSeries lineSeries, int currentIndex, int totalCount) {
		// Style the colors of the lines and points.
    	float hue = currentIndex / (1.00f * totalCount) * 360.00f;
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
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == NOTES_MANAGE) {
			groupChartLayout.getChart().updateChart();
		}
	}

	@Override
	public boolean onChartClick(Chart c, MotionEvent event) {
		return false;
	}

	@Override
	public boolean onChartLongClick(Chart c, MotionEvent event) {
		return false;
	}


	public void showChart(ChartLayout chartLayout, boolean animate) {
		if(this.chartsContainer.getChildCount() > 1) {
			this.chartsContainer.removeViewAt(0);
		}

		chartLayout.setShowLabels(true);
		chartLayout.getChart().setDropShadowEnabled(true);
		chartLayout.getChart().setShowYHilight(true);

		ChartLayout currentChartLayout = (ChartLayout)this.chartsContainer.getCurrentView();
		if(currentChartLayout != null) {
			currentChartLayout.setShowLabels(false);
			currentChartLayout.getChart().setDropShadowEnabled(false);
			currentChartLayout.getChart().setShowYHilight(false);
			
			chartLayout.getChart().setChartContainer(currentChartLayout.getChart().getChartContainer());
			chartLayout.getChart().updateChart();
			chartLayout.getChart().moveYHilightTo(currentChartLayout.getChart().getYHilightBounds().left);
		}

		// Make sure this chart layout isn't connected to a parent view.
		ViewGroup parent = (ViewGroup)chartLayout.getParent();
		if(parent != null) {
			parent.removeView(chartLayout);
		}

		this.currentChartLayout = chartLayout;
		this.chartsContainer.addView(chartLayout);
		this.chartsContainer.showNext();

		VASAnalytics.onEvent(VASAnalytics.EVENT_SCALE_SELECTED);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ChartLayout cl = this.chartLayoutsList.get(arg2);
		
		if(cl != groupChartLayout && cl != currentChartLayout) {
			showChartAtIndex(arg2);
		}
	}
	
	private void showChartAtIndex(int index) {
		this.showChart(chartLayoutsList.get(index), true);
		
		this.keyListAdapter.setSelected(index);
		this.keyListAdapter.notifyDataSetInvalidated();
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

	private void addNotesPressed() {
		long[] range = getActiveChartTimeRange();
		Intent i = new Intent(this, NoteActivity.class);
		i.putExtra("timestamp", range[0]);

		this.startActivityForResult(i, NOTES_MANAGE);
	}

	private void viewNotesPressed() {
		long[] range = getActiveChartTimeRange();
		//Intent i = new Intent(this, NotesDialogActivity.class);
		Intent i = new Intent();
		i.setAction("com.t2.vas.NotesListDialog");
		i.putExtra("start_timestamp", range[0]);
		i.putExtra("end_timestamp", (range[0]+(86400*1000)));

		this.startActivityForResult(i, NOTES_MANAGE);
	}
	
	
	public long[] getActiveChartTimeRange() {
		ChartLayout chartLayout = currentChartLayout;

		if(chartLayout == null) {
			return null;
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
		
		return new long[]{
			startTimestamp,
			endTimestamp
		};
	}
	
	@Override
	public int getHelpResId() {
		return R.string.results_help;
	}
	
	
	
	private class ScaleKeyAdapter extends ArrayAdapter<ChartLayout> {
//		private static final String TAG = ScaleKeyAdapter.class.getName();

		private LayoutInflater inflater;
		private int layoutResId;
		private ArrayList<ChartLayout> items;
		private int selectedIndex = 0;

		private int mGalleryItemBackground;

		public ScaleKeyAdapter(Context context, int textViewResourceId, ArrayList<ChartLayout> objects) {
			super(context, textViewResourceId, objects);

			this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
			this.layoutResId = textViewResourceId;
			this.items = objects;

			/*TypedArray a = context.obtainStyledAttributes(R.styleable.CustomTheme);
	        mGalleryItemBackground = a.getResourceId(
	                R.styleable.CustomTheme_android_galleryItemBackground, 0);
	        a.recycle();*/
		}
		
		public void setSelected(int index) {
			this.selectedIndex = index;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ChartLayout chartLayout = this.items.get(position);
			ArrayList<KeyBoxData> key = chartLayout.getChart().getKey();

			if(key.size() < 1) {
				return null;
			}

			KeyBoxData data = key.get(0);
			String minLabelText = chartLayout.getScale().min_label;
			String maxLabelText = chartLayout.getScale().max_label;

			LinearLayout v = (LinearLayout)inflater.inflate(this.layoutResId, null);

			LinearLayout outer = (LinearLayout)v.findViewById(R.id.outerBox);
			LinearLayout inner = (LinearLayout)v.findViewById(R.id.innerBox);
			TextView minLabel = (TextView)v.findViewById(R.id.minLabel);
			TextView maxLabel = (TextView)v.findViewById(R.id.maxLabel);

			outer.setBackgroundColor(data.getStrokeColor());
			inner.setBackgroundColor(data.getFillColor());
			minLabel.setText(minLabelText);
			maxLabel.setText(maxLabelText);

			if(position == this.selectedIndex || position == 0) {
				v.setBackgroundResource(R.drawable.chart_background);
			}

			return v;
		}
	}
}

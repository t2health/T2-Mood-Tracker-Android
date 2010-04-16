package com.t2.vas.activity;

import java.util.ArrayList;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ScaleKeyAdapter;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.db.tables.Scale.ResultValues;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.chart.LineSeries;
import com.t2.vas.view.chart.Series;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final String TAG = "ResultsActivity";
	ArrayList<ChartLayout> chartLayouts = new ArrayList<ChartLayout>();
	
	private int activeGroupId;
	LayoutInflater layoutInflater;
	
	ViewSwitcher chartViewAnimator;
	ListView keyListView;
	private ScaleKeyAdapter keyListAdapter;
	private int lastActiveListIndex = -1;
	
	private Toast tooltipToast;
	private FrameLayout fadeOutCharts;
	
	private int resultsGroupBy = Scale.GROUPBY_DAY;
	private View notesButton;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tooltipToast = Toast.makeText(this, R.string.click_chart_tooltip, 3000);
        tooltipToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        
        activeGroupId = this.getIntent().getIntExtra("group_id", 1);
        
        ArrayList<Series> chartSeries = new ArrayList<Series>();
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        LinearLayout l = (LinearLayout)layoutInflater.inflate(R.layout.results_activity, null);
        keyListView = (ListView)l.findViewById(R.id.list);
        chartViewAnimator = (ViewSwitcher)l.findViewById(R.id.charts);
        fadeOutCharts = (FrameLayout)l.findViewById(R.id.fadeOutCharts);
        notesButton = (View)l.findViewById(R.id.notesButton);
        notesButton.setOnClickListener(this);
        notesButton.setVisibility(View.INVISIBLE);
        
        DBAdapter db = new DBAdapter(this, Global.Database.name, Global.Database.version);
        db.open();
        
        // Build a test linechart 
        Group g = (Group)db.getTable("group").newInstance();
        g._id = activeGroupId;
        g.load();
        
        ArrayList<Scale> scales = g.getScales();
        int scaleCount = scales.size();
        for(int i = 0; i < scaleCount ; i++) {
        	Scale s = scales.get(i);
        	LineSeries lineSeries = new LineSeries(s.max_label +" - "+ s.min_label);
        	ResultValues scaleValues = s.getResultValues(resultsGroupBy, Global.CHART_LABEL_DATE_FORMAT);
        	ChartLayout chartLayout = (ChartLayout)layoutInflater.inflate(R.layout.chart_layout, null);
        	chartLayout.setYMaxLabel(s.max_label);
        	chartLayout.setYMinLabel(s.min_label);
        	chartLayout.setTag(s._id+"");
        	//chartLayout.setOnClickListener(this);
        	
        	lineSeries.setSelectable(false);
        	
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
        	
        	lineSeries.addAllValues(scaleValues.values);
        	lineSeries.addAllLabels(scaleValues.labels);
        	chartLayout.getChart().addSeries("main", lineSeries);
        	
        	chartLayouts.add(chartLayout);
        }
        
        keyListAdapter = new ScaleKeyAdapter(this, R.layout.key_box_adapter_list_label_right, chartLayouts);
        keyListView.setAdapter(keyListAdapter);
        keyListView.setOnItemClickListener(this);
        
        this.setContentView(l);
        
        db.close();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ChartLayout chartLayout = (ChartLayout)arg1.getTag();
		chartLayout.setShowLabels(true);
		chartLayout.setLabelsColor(Color.WHITE);
		chartLayout.getChart().setShowVerticalPositionBar(true);
		
		tooltipToast.cancel();
		//tooltipToast.show();
		
		// Flash the chart if it already selected
		if(arg2 == lastActiveListIndex) {
			AlphaAnimation fadeDown = new AlphaAnimation(1.0f, 0.0f);
			fadeDown.setDuration(250);
			
			chartLayout.startAnimation(fadeDown);
			return;
		}
		
		// Have the parent remove the child view. This prevents an issue in the next code block.
		ViewGroup parent = (ViewGroup)chartLayout.getParent();
		if(parent != null) {
			parent.removeView(chartLayout);
		}
		
		// Progressivley stack the previous charts and slowly fade them out.
		float alphaStep = 0.34f;
		int removeAfterIndex = (int)(1 / alphaStep);
		View v = this.chartViewAnimator.getChildAt(0);
		this.chartViewAnimator.removeViewAt(0);
		this.fadeOutCharts.addView(v, 0);
		for(int i = this.fadeOutCharts.getChildCount()-1; i >= 0; i--) {
			if(i >= removeAfterIndex) {
				this.fadeOutCharts.removeViewAt(i);
				continue;
			}
			
			View child = this.fadeOutCharts.getChildAt(i);
			float startAlpha = 0.00f;
			float endAlpha = 0.00f;
			
			startAlpha = 1.00f - (alphaStep * (i)); 
			endAlpha = 1.00f - (alphaStep * (i + 1));
			
			startAlpha = (startAlpha > 1)?1.00f:startAlpha;
			endAlpha = (endAlpha < 0)?0.00f:endAlpha;
			
			AlphaAnimation fadeDown = new AlphaAnimation(startAlpha, endAlpha);
			fadeDown.setDuration(250);
			fadeDown.setFillAfter(true);
			fadeDown.setFillBefore(true);
			child.startAnimation(fadeDown);
			
			try {
				ChartLayout childChart = (ChartLayout)child;
				childChart.setShowLabels(false);
				childChart.getChart().setDropShadowEnabled(false);
				childChart.getChart().setShowVerticalPositionBar(false);
			} catch (ClassCastException cce) {}
		}
		
		this.chartViewAnimator.addView(chartLayout);
		this.chartViewAnimator.showNext();
		
		
		lastActiveListIndex = arg2;
		
		notesButton.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.notesButton) {
			ChartLayout chartLayout;
			
			try {
				chartLayout = (ChartLayout)chartViewAnimator.getCurrentView();
			} catch(ClassCastException cce) {
				return;
			}
			
			if(chartLayout == null) {
				return;
			}
			
			LineSeries series = (LineSeries)chartLayout.getChart().getSeries("main");
			long scaleId = Long.parseLong((String)chartLayout.getTag());
			
			Intent i = new Intent(this, NotesActivity.class);
			
			i.putExtra("scale_id", scaleId);
			i.putExtra("seriesType", "LineSeries");
			i.putExtra("resultsGroupBy", resultsGroupBy);
			i.putExtra("seriesFillColor", series.getFillColor());
			i.putExtra("seriesStrokeColor", series.getStrokeColor());
			i.putExtra("seriesLineFillColor", series.getLineFillColor());
			i.putExtra("seriesLineStrokeColor", series.getLineStrokeColor());
			i.putExtra("chartLabelsColor", chartLayout.getLabelsColor());
			
			this.startActivity(i);
		}
		
		
	}
}

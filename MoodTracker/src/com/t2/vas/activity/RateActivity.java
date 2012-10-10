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

import java.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.t2.vas.Analytics;
import com.t2.vas.R;
import com.t2.vas.ScaleAdapter;
import com.t2.vas.Slider;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

public class RateActivity extends ABSActivity  implements Slider.OnSeekBarChangeListener, OnScrollListener {
	public static final String EXTRA_GROUP_ID = "group_id";

	private ScaleAdapter scaleAdapter;
	//private ListView listView;
	private Group group;
	private Button btn_reset;
	private Button btn_save;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.rate_activity);

		//this.setRightButtonText(getString(R.string.save));

		group = new Group(dbAdapter);
		group._id = getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
		if(!group.load()) {
			this.finish();
			return;
		}

		//listView = (ListView)this.findViewById(R.id.list);

		//Buttons
		btn_reset = (Button)this.findViewById(R.id.btn_reset);
		btn_reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for(int i = 0; i < scaleAdapter.getCount(); i++) {
					scaleAdapter.setProgressValueAt(i, 50);
				}
				scaleAdapter.notifyDataSetChanged();
				inflateSliderView();
			}
		});
		
		btn_save = (Button)this.findViewById(R.id.btn_save);
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveResults();
			}
		});

		this.setTitle(group.title);

		
		scaleAdapter = new ScaleAdapter(this, R.layout.slider_overlay_widget, group.getScales());
		inflateSliderView();
		
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		for(int i= 0; i< scaleAdapter.getCount(); i++)
		{
			savedInstanceState.putInt(""+i,((Slider)this.getWindow().getDecorView().findViewWithTag(i)).getProgress());
		}
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		for(int i= 0; i< scaleAdapter.getCount(); i++)
		{
			((Slider)this.getWindow().getDecorView().findViewWithTag(i)).setProgress(savedInstanceState.getInt(""+i));
		}
	}
	
	public void inflateSliderView()
	{
		LinearLayout item = (LinearLayout)findViewById(R.id.ll_sliders);
		item.removeAllViews();
		
		for(int i= 0; i< scaleAdapter.getCount(); i++)
		{
			
			View child = getLayoutInflater().inflate(R.layout.slidercontrol, null);
			item.addView(child);

			TextView low = (TextView) child.findViewById(R.id.tv_lowvalue);
			low.setText(scaleAdapter.getItem(i).min_label);

			TextView high = (TextView) child.findViewById(R.id.tv_highvalue);
			high.setText(scaleAdapter.getItem(i).max_label);
			Slider seeker = (Slider) child.findViewById(R.id.seekbar);
			seeker.setTag(i);
			seeker.setOnSeekBarChangeListener(this);
			seeker.setProgress(50);
			scaleAdapter.setProgressValueAt(i, 50);
			onProgressChanged(seeker, 50, true);
		}
	}
	
	private void saveResults() {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		
		StringBuilder modifiedScales = new StringBuilder();
		for(int i = 0; i < this.scaleAdapter.getCount(); i++) {
			Scale s = this.scaleAdapter.getItem(i);
			int value = this.scaleAdapter.getProgressValuesAt(i);

			Result r = new Result(this.dbAdapter);

			if (value != 50) {
			    if (modifiedScales.length() > 0) {
			        modifiedScales.append(",");
			    }
			    modifiedScales.append(s.min_label).append("-").append(s.max_label);
			}
			
			r.group_id = group._id;
			r.scale_id = s._id;
			r.timestamp = currentTime;
			r.value = value;

			r.save();
			
		}
		
		if (modifiedScales.length() > 0) {
		    Analytics.onEvent(this, "Ratings Saved," + modifiedScales.toString());
		}
		finish();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		int index = (Integer) seekBar.getTag();
		this.scaleAdapter.setProgressValueAt(index, progress);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
}
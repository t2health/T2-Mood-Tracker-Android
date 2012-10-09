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
package com.t2.vas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.t2.vas.R;

public class SliderWidget extends LinearLayout implements OnSeekBarChangeListener {
	private TextView minLabel;
	private TextView maxLabel;
	private SeekBar slideBar;

	private OnSliderWidgetChangeListener onSliderWidgetChangeListener;

	public SliderWidget(Context context) {
		super(context);
	}

	public SliderWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		this.minLabel = (TextView)this.findViewById(R.id.minLabel);
		this.maxLabel = (TextView)this.findViewById(R.id.maxLabel);
		this.slideBar = (SeekBar)this.findViewById(R.id.slideBar);
		this.slideBar.setProgress(50);
		this.slideBar.setOnSeekBarChangeListener(this);
	}

	public void setMinLabelText(CharSequence s) {
		this.minLabel.setText(s);
	}
	
	public CharSequence getMinLabelText() {
		return this.minLabel.getText();
	}
	
	public void setMaxLabelText(CharSequence s) {
		this.maxLabel.setText(s);
	}
	
	public CharSequence getMaxLabelText() {
		return this.maxLabel.getText();
	}
	
	public void setProgress(int p) {
		this.slideBar.setProgress(p);
	}
	
	public int getProgress() {
		return this.slideBar.getProgress();
	}
	
	public void setOnSliderWidgetChangeListener(OnSliderWidgetChangeListener l) {
		this.onSliderWidgetChangeListener = l;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onProgressChanged(this, progress, fromUser);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onStartTrackingTouch(this);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(onSliderWidgetChangeListener != null) {
			onSliderWidgetChangeListener.onStopTrackingTouch(this);
		}
	}
	
	
	
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
    	this.requestDisallowInterceptTouchEvent(false);
    	return super.dispatchTouchEvent(ev);
    }
    
    public interface OnSliderWidgetChangeListener {
    	public void onProgressChanged(SliderWidget sliderWidget, int progress,
    			boolean fromUser);
    	public void onStartTrackingTouch(SliderWidget sliderWidget);
    	public void onStopTrackingTouch(SliderWidget sliderWidget);
    }

}

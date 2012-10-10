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
package com.t2.vas;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.SeekBar;

public class Slider extends SeekBar {

	Drawable mThumb;
	private boolean canSlide = false;

	public Slider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Slider(Context context) {
		super(context);
	}

	@Override
	public void setThumb(Drawable thumb) {
		super.setThumb(thumb);
		mThumb = thumb;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Rect bounds = new Rect();
			bounds.top = mThumb.getBounds().top - 30;
			bounds.left = mThumb.getBounds().left - 30;
			bounds.bottom = mThumb.getBounds().bottom + 30;
			bounds.right = mThumb.getBounds().right + 30;

			if (bounds.contains((int) event.getX(), (int) event.getY())) {
				canSlide = true;
			}
		} else {
			if (canSlide)
				super.onTouchEvent(event); // process horizontal slide
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			canSlide = false;
		}

		if (!canSlide) {
			ViewParent parent = (ViewParent) this.getParent();
			if (parent != null) {
				parent.requestDisallowInterceptTouchEvent(false);
			}
			
		}
		return true;
	}

}


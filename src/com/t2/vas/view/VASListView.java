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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.t2.vas.FromParentTouchHandler;

public class VASListView extends ListView {
	private ArrayList<Float> historyX = new ArrayList<Float>();
	private ArrayList<Float> historyY = new ArrayList<Float>();

	private View activeChild;
	private boolean childHasFocus = false;

	private boolean isScrolling;

	public VASListView(Context context) {
		super(context);
	}

	public VASListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VASListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int maxHistorySize = 3;

		if(event.getAction() == MotionEvent.ACTION_UP) {

			// Send the up event to the child if it was not a movement.
			// This will all one to interact with the child view via a single touch.
			if(historyX.size() < maxHistorySize) {
				dispatchToChildren(event);
			}

			this.activeChild = null;
			this.childHasFocus = false;
			this.isScrolling = false;

			return super.onTouchEvent(event);
		}

		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			historyX.clear();
			historyY.clear();

			this.activeChild = null;
			this.childHasFocus = false;
			this.isScrolling = false;

			return super.onTouchEvent(event);
		}

		// Send
		if(this.childHasFocus) {
			//Log.v(TAG, "To Child");
			return dispatchToChild(this.activeChild, event);
		} else if(this.isScrolling) {
			return super.onTouchEvent(event);
		}

		// Determine what the intention of the user is. wether they want to
		//  vertically scroll or horizontally interact with the widget.
		int historyXSize = historyX.size();
		int historyYSize = historyY.size();

		historyX.add(0, event.getX());
		historyY.add(0, event.getY());

		historyXSize++;
		historyYSize++;

		if(historyXSize > maxHistorySize || historyYSize > maxHistorySize) {
			historyX = new ArrayList<Float>(historyX.subList(0, maxHistorySize));
			historyY = new ArrayList<Float>(historyY.subList(0, maxHistorySize));

			historyXSize = maxHistorySize;
			historyYSize = maxHistorySize;
		}

		float distanceX = Math.abs(historyX.get(0) - historyX.get(historyX.size() - 1));
		float distanceY = Math.abs(historyY.get(0) - historyY.get(historyY.size() - 1));

		// Dertermine if we are scrolling the listview of the seekbars
		if(historyXSize >= maxHistorySize && distanceY < distanceX) {
			//Log.v(TAG, "Child focus");
			this.childHasFocus = true;
			this.activeChild = dispatchToChildren(event);
			return true;
		} else if(historyXSize >= maxHistorySize && distanceY > distanceX) {
			//Log.v(TAG, "Scrolling");
			this.isScrolling = true;
			super.onTouchEvent(event);
			return true;
		}

		return false;
	}

	private boolean dispatchToChild(View child, MotionEvent event) {
		int[] pos = new int[2];

		this.getLocationOnScreen(pos);

//		MotionEvent newEvent = MotionEvent.obtainNoHistory(event);
		MotionEvent newEvent = MotionEvent.obtain(event);
		newEvent.setLocation(event.getX() + pos[0], event.getY() + pos[1]);

		try {
			FromParentTouchHandler th = (FromParentTouchHandler)child;
			if(th != null && th.onTouchEventFromParent(newEvent)) {
				return true;
			}
		} catch(ClassCastException cce) {
			return false;
		}

		return false;
	}

	private View dispatchToChildren(MotionEvent event) {
		Rect rect = new Rect();
		int[] pos = new int[2];
		ArrayList<View> children = new ArrayList<View>();

		// Add the children to the list.
		for(int i = 0; i < this.getChildCount(); i++) {
			children.add(this.getChildAt(i));
		}

		this.getLocationOnScreen(pos);

//		MotionEvent newEvent = MotionEvent.obtainNoHistory(event);
		MotionEvent newEvent = MotionEvent.obtain(event);
		newEvent.setLocation(event.getX() + pos[0], event.getY() + pos[1]);

		//Log.v(TAG, "dispatch");

		int currentIndex = 0;
		while(true) {
			if(currentIndex >= children.size()) {
				break;
			}

			View child = children.get(currentIndex);

			try {
				ViewGroup childViewGroup = (ViewGroup)child;

				// Add the children to the list.
				for(int i = 0; i < childViewGroup.getChildCount(); i++) {
					children.add(childViewGroup.getChildAt(i));
				}
			} catch (ClassCastException cce) {
				//Log.v(TAG, "Caught an exception.");
			}


			child.getGlobalVisibleRect(rect);
			if(child.getVisibility() == View.VISIBLE && rect.contains((int)newEvent.getX(), (int)newEvent.getY())) {
				try {
					FromParentTouchHandler th = (FromParentTouchHandler)child;
					if(th.onTouchEventFromParent(newEvent)) {
						return child;
					}
				} catch(ClassCastException cce) {
					//Log.v(TAG, "Caught an exception.");
				}
			}
			currentIndex++;
		}

		return null;
	}
}

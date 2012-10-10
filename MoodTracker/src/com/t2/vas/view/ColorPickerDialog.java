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

import com.t2.vas.R;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class ColorPickerDialog {
	public interface OnColorPickerListener {
		void onCancel(ColorPickerDialog dialog);
		void onOk(ColorPickerDialog dialog, int color, int resID);
	}

	final AlertDialog dialog;
	final OnColorPickerListener listener;
	final View viewHue;
	final ColorPickerView viewSatVal;
	final ImageView viewCursor;
	final ImageView viewNewColor;
	final ImageView viewTarget;
	final ImageView ivPrev;
	final ImageView ivNext;
	final ViewGroup cursorContainer;
	final ViewGroup targetContainer;
	final float[] currentColorHsv = new float[3];
	int selResID = 0;
	int selIndex = 0;
	int[] mresid;

	/**
	 * create an ColorPickerDialog. call this only from OnCreateDialog() or from a background thread.
	 * 
	 * @param context
	 *            current context
	 * @param color
	 *            current color
	 * @param listener
	 *            an OnColorPickerListener, allowing you to get back error or
	 */
	public ColorPickerDialog(final Context context, int color, int[] resourceIDS, OnColorPickerListener listener, int defResID) {
		this.listener = listener;
		
		if(defResID == 0)
			selResID = R.drawable.fivestar;
		else
		selResID = defResID;
		
		Color.colorToHSV(color, currentColorHsv);
		mresid = resourceIDS;
		final View view = LayoutInflater.from(context).inflate(R.layout.colorpickerdialog, null);
		viewHue = view.findViewById(R.id.colorpicker_viewHue);
		viewSatVal = (ColorPickerView) view.findViewById(R.id.colorpicker_viewSatBri);
		viewCursor = (ImageView) view.findViewById(R.id.colorpicker_cursor);
		viewNewColor = (ImageView) view.findViewById(R.id.colorpicker_warnaBaru);
		viewNewColor.setImageResource(mresid[0]);
		viewNewColor.setColorFilter(color);
		viewTarget = (ImageView) view.findViewById(R.id.colorpicker_target);
		ivPrev = (ImageView) view.findViewById(R.id.btnPrevIcon);
		ivPrev.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(selIndex>0)
					selIndex--;
				else
					selIndex = mresid.length -1;
				selResID = mresid[selIndex];
				viewNewColor.setImageResource(selResID);
			}
		});

		ivNext = (ImageView) view.findViewById(R.id.btnNextIcon);
		ivNext.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(selIndex<(mresid.length-1))
					selIndex++;
				else
					selIndex = 0;
				selResID = mresid[selIndex];
				viewNewColor.setImageResource(selResID);
			}
		});

		viewNewColor.setImageResource(selResID);
		
		targetContainer = (ViewGroup) view.findViewById(R.id.targetLayout);
		cursorContainer = (ViewGroup) view.findViewById(R.id.cursorLayout);

		viewSatVal.setHue(getHue());
		viewNewColor.setBackgroundColor(Color.TRANSPARENT);

		viewHue.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float y = event.getY();
					if (y < 0.f) y = 0.f;
					if (y > viewHue.getMeasuredHeight()) y = viewHue.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
					float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
					if (hue == 360.f) hue = 0.f;
					setHue(hue);

					// update view
					viewSatVal.setHue(getHue());
					moveCursor(event);
					viewNewColor.setColorFilter(getColor());

					return true;
				}
				return false;
			}
		});
		viewSatVal.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float x = event.getX(); // touch event are in dp units.
					float y = event.getY();

					if (x < 0.f) x = 0.f;
					if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
					if (y < 0.f) y = 0.f;
					if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

					setSat(1.f / viewSatVal.getMeasuredWidth() * x);
					setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

					// update view
					moveTarget(event);
					viewNewColor.setColorFilter(getColor());

					return true;
				}
				return false;
			}
		});

		dialog = new AlertDialog.Builder(context)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				if (ColorPickerDialog.this.listener != null) {
					ColorPickerDialog.this.listener.onOk(ColorPickerDialog.this, getColor(), selResID);
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				if (ColorPickerDialog.this.listener != null) {
					ColorPickerDialog.this.listener.onCancel(ColorPickerDialog.this);
				}
			}
		})
		.setOnCancelListener(new OnCancelListener() {
			// if back button is used, call back our listener.
			@Override public void onCancel(DialogInterface paramDialogInterface) {
				if (ColorPickerDialog.this.listener != null) {
					ColorPickerDialog.this.listener.onCancel(ColorPickerDialog.this);
				}

			}
		})
		.create();
		// kill all padding from the dialog window
		dialog.setView(view, 0, 0, 0, 0);

		// move cursor & target on first draw
		ViewTreeObserver vto = view.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override public void onGlobalLayout() {
				//moveCursor();
				//moveTarget();
				view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}

	protected void moveCursor(MotionEvent event) {

		//int fx = (int) event.getX();
		int fy = (int) event.getY();
		viewCursor.setPadding(0,fy, 0, 0);
	}

	protected void moveTarget(MotionEvent event) {

		int fx = (int) event.getX();
		int fy = (int) event.getY();
		viewTarget.setPadding(fx,fy, 0, 0);
	}

	private int getColor() {
		return Color.HSVToColor(currentColorHsv);
	}

	private float getHue() {
		return currentColorHsv[0];
	}

	/*private float getSat() {
		return currentColorHsv[1];
	}

	private float getVal() {
		return currentColorHsv[2];
	}*/

	private void setHue(float hue) {
		currentColorHsv[0] = hue;
	}

	private void setSat(float sat) {
		currentColorHsv[1] = sat;
	}

	private void setVal(float val) {
		currentColorHsv[2] = val;
	}

	public void show() {
		dialog.show();
	}

	public AlertDialog getDialog() {
		return dialog;
	}
}
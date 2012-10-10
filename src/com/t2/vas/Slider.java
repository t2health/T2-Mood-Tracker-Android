/*
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


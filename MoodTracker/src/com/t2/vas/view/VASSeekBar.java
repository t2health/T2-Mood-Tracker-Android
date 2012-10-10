/*
 * 
 */
package com.t2.vas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.t2.vas.FromParentTouchHandler;

public class VASSeekBar extends SeekBar implements FromParentTouchHandler {
	private boolean eventFromParent = false;

	public VASSeekBar(Context context) {
		super(context);
	}

	public VASSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VASSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!eventFromParent ) {
			return false;
		}
		eventFromParent = false;
		
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEventFromParent(MotionEvent event) {
		eventFromParent = true;
		return this.onTouchEvent(event);
	}
}

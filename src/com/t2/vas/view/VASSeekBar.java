package com.t2.vas.view;

import com.t2.vas.FromParentTouchHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VASSeekBar extends SeekBar implements FromParentTouchHandler {
	private static final String TAG = VASSeekBar.class.getName();
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
		
		//Log.v(TAG, "RECV");
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEventFromParent(MotionEvent event) {
		eventFromParent = true;
		return this.onTouchEvent(event);
	}
	
	
}

package com.t2.vas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

public class ContentGallery extends Gallery {
	private boolean flingEnabled = false;

	public ContentGallery(Context context) {
		super(context);
	}

	public ContentGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ContentGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(this.flingEnabled) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}
		return false;
	}
}

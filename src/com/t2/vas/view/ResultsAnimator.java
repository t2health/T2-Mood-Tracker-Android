package com.t2.vas.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Adapter;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;

public class ResultsAnimator extends ViewAnimator {
	private static final String TAG = ResultsAnimator.class.getName();

	private Adapter adapter;
	private int currentItemIndex = -1;

	public ResultsAnimator(Context context) {
		super(context);
	}

	public ResultsAnimator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setAdapter(Adapter a) {
		this.adapter = a;
	}

	public Adapter getAdapter() {
		return this.adapter;
	}

	public int getCurrentItemIndex() {
		return this.currentItemIndex;
	}

	@Override
	public void showNext() {
		this.showAt(currentItemIndex + 1);
	}

	@Override
	public void showPrevious() {
		this.showAt(currentItemIndex - 1);
	}

	public void showAt(int i) {
		if(i == this.currentItemIndex || i < 0 || i >= this.adapter.getCount()) {
			return;
		}

		View nextView = this.adapter.getView(i, null, null);
		if(nextView == null) {
			return;
		}

		if(this.getChildCount() > 1) {
			this.removeViewAt(0);
		}
		this.addView(nextView);

		super.showNext();
		this.currentItemIndex = i;
	}
}

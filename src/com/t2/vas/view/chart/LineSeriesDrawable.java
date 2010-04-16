package com.t2.vas.view.chart;

import android.graphics.Rect;
import android.graphics.drawable.shapes.OvalShape;

public class LineSeriesDrawable extends SeriesDrawable {
	public LineSeriesDrawable(Rect bounds) {
		super(bounds);
		
		int left = bounds.left;
		int top  = bounds.top;
		int right = bounds.right;
		int bottom = bounds.bottom;
		
		int pointDiameter = right - left;
		
		this.fillDrawable.setShape(new OvalShape());
		this.fillDrawable.getPaint().setAntiAlias(true);
		
		this.strokeDrawable.setShape(new OvalShape());
		this.strokeDrawable.getPaint().setAntiAlias(true);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, top + (right - left));
	}

	@Override
	public void setBounds(Rect bounds) {
		Rect newBounds = new Rect(bounds);
		newBounds.bottom = newBounds.top + (newBounds.right - newBounds.left);
		super.setBounds(newBounds);
	}
	
	
}

package com.t2.vas.view.chart;

import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;

public class BarSeriesDrawable extends SeriesDrawable {
	public BarSeriesDrawable(Rect bounds) {
		super(bounds);
		
		this.fillDrawable.setShape(new RectShape());
		this.strokeDrawable.setShape(new RectShape());
	}
}

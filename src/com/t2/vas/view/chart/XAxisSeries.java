package com.t2.vas.view.chart;

import java.util.ArrayList;

import android.graphics.drawable.ShapeDrawable;

public class XAxisSeries extends Series {

	public XAxisSeries(String name) {
		super(name);
	}

	@Override
	protected ArrayList<ShapeDrawable> loadDrawables(ArrayList<ChartRect> areas, int width, int height) {
		return null;
	}

}

package com.t2.vas.view.chart;

import java.util.ArrayList;

import com.t2.vas.db.tables.Scale.ResultValues;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;

public class BarSeries extends Series {

	public BarSeries(String name) {
		super(name);
	}
	
	public BarSeries(String name, ArrayList<Label> labels, ArrayList<Value> values) {
		super(name, labels, values);
	}

	protected ArrayList<ShapeDrawable> loadDrawables(ArrayList<ChartRect> areas, int width, int height) {
		ArrayList<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
		
		for(int i = 0; i < areas.size(); i++) {
			BarSeriesDrawable point = new BarSeriesDrawable(areas.get(i).toRect());
			
			point.setFillColor(this.getFillColor());
			point.setStrokeColor(this.getStrokeColor());
			
			drawables.add(point);
		}
		
		return drawables;
	}
}

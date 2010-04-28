package com.t2.vas.view.chart;

import java.util.ArrayList;

import android.graphics.Rect;
import android.util.Log;

public class NotesSeries extends Series {
	private static final String TAG = NotesSeries.class.getName();

	public NotesSeries(String name, ArrayList<Label> labels, ArrayList<Value> values) {
		super(name, labels, values);
	}

	@Override
	protected SeriesDrawable onLoadDrawable(Value v, int pos, int width, int height) {
		if(!v.isHilight()) {
			return null;
		}
		
		Rect bounds = v.getBounds();
		return new SeriesDrawable(new Rect(bounds.left, 0, bounds.right, 10));
	}
}

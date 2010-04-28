package com.t2.vas.view.chart;

import android.graphics.Rect;

public class Value<T> {
	private Double value;
	private T metaData;
	private boolean isHilight = false;
	private Rect bounds = new Rect();
	
	public Value(Double value, T metaData) {
		this.setValue(value);
		this.setMetaData(metaData);
	}
	
	public Value(Double value, T metaData, boolean hilight) {
		this.setValue(value);
		this.setMetaData(metaData);
		this.setHilight(hilight);
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

	public void setMetaData(T metaData) {
		this.metaData = metaData;
	}

	public T getMetaData() {
		return metaData;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	public Rect getBounds() {
		return bounds;
	}
	
	public Rect getBoundsCopy() {
		return new Rect(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}

	public void setHilight(boolean isHilight) {
		this.isHilight = isHilight;
	}

	public boolean isHilight() {
		return isHilight;
	}
}

package com.t2.vas.view.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;

public class SeriesDrawable extends ShapeDrawable {
	private Value value = null;
	
	private int fillColor = Color.RED;
	private int strokeColor = Color.GREEN;
	
	/*private int selectedFillColor = Color.YELLOW;
	private int selectedStrokeColor = Color.RED;*/
	
	protected ShapeDrawable fillDrawable;
	protected ShapeDrawable strokeDrawable;
	
	private boolean isHilightEnabled = false;
	/*private boolean isSelected = false;
	private boolean isSelectable = true;*/
	
	public SeriesDrawable(Rect bounds) {
		this.init();
		this.setBounds(bounds);
	}
	
	private void init() {
		this.fillDrawable = new ShapeDrawable();
		this.fillDrawable.getPaint().setStyle(Style.FILL);
		
		this.strokeDrawable = new ShapeDrawable();
		this.strokeDrawable.getPaint().setStyle(Style.STROKE);
		
		/*this.setDeseleted();*/
	}
	
	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
		this.fillDrawable.getPaint().setColor(fillColor);
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
		this.strokeDrawable.getPaint().setColor(strokeColor);
	}

	public int getStrokeColor() {
		return strokeColor;
	}

	/*public void setSelectedFillColor(int selectedFillColor) {
		this.selectedFillColor = selectedFillColor;
	}

	public int getSelectedFillColor() {
		return selectedFillColor;
	}

	public void setSelectedStrokeColor(int selectedStrokeColor) {
		this.selectedStrokeColor = selectedStrokeColor;
	}

	public int getSelectedStrokeColor() {
		return selectedStrokeColor;
	}*/
	
	public void setHilightEnabled(boolean isHilightEnabled) {
		this.isHilightEnabled = isHilightEnabled;
	}

	public boolean isHilightEnabled() {
		return isHilightEnabled;
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		
		this.fillDrawable.setBounds(new Rect(left, top, right, bottom));
		this.strokeDrawable.setBounds(new Rect(left, top, right, bottom));
	}

	@Override
	public void setBounds(Rect bounds) {
		this.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}
	
	@Override
	public void draw(Canvas canvas) {
		this.fillDrawable.draw(canvas);
		this.strokeDrawable.draw(canvas);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		
	}

	public void setValue(Value value) {
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	/*public void setSeleted() {
		if(!this.isSelectable) {
			return;
		}
		
		this.fillDrawable.getPaint().setColor(this.selectedFillColor);
		this.strokeDrawable.getPaint().setColor(this.selectedStrokeColor);
		this.isSelected = true;
	}
	
	public void setDeseleted() {
		this.fillDrawable.getPaint().setColor(this.fillColor);
		this.strokeDrawable.getPaint().setColor(this.strokeColor);
		this.isSelected = false;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	public boolean isSelectable() {
		return isSelectable;
	}*/
}

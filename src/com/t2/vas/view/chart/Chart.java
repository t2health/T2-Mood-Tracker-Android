package com.t2.vas.view.chart;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Chart extends View {
	private static final String TAG = "VAS";
	protected ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	protected LinkedHashMap<String, Series> seriesList = new LinkedHashMap<String, Series>();
	
	private int pointWidth = 10;
	private int pointPaddingRight = 5;
	
	private boolean dropShadowEnabled = true;
	private boolean interactiveModeEnabled = true;
	private Rect chartContainer;
	
	private boolean showYHilight = true;
	private ShapeDrawable yHilightDrawable = new ShapeDrawable(new RectShape());
	private boolean yHilightSnap = true;
	private String yHilightDrawableLabel = "";
	private int yHilightSeriesIndex = -1;
	
	public Chart(Context context) {
		super(context);
		this.init();
	}

	public Chart(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}
	
	private void init() {
		this.setMinimumHeight(100);
	}
	
	public void setDropShadowEnabled(boolean dropShadowEnabled) {
		this.dropShadowEnabled = dropShadowEnabled;
	}

	public boolean isDropShadowEnabled() {
		return dropShadowEnabled;
	}

	public void setShowYHilight(boolean showYHilight) {
		this.showYHilight = showYHilight;
	}

	public boolean isShowYHilight() {
		return showYHilight;
	}

	public void setyHilightSnap(boolean yHilightSnap) {
		this.yHilightSnap = yHilightSnap;
	}

	public boolean isyHilightSnap() {
		return yHilightSnap;
	}

	public void setInteractiveModeEnabled(boolean interactiveModeEnabled) {
		this.interactiveModeEnabled = interactiveModeEnabled;
	}

	public boolean isInteractiveModeEnabled() {
		return interactiveModeEnabled;
	}

	public void addSeries(String name, Series serie) {
		this.seriesList.put(name, serie);
	}
	
	public Series getSeries(String name) {
		return this.seriesList.get(name);
	}
	
	public Series getSeriesAt(int index) {
		int i = 0;
		for(String key: this.seriesList.keySet()) {
			if(i == index) {
				return this.seriesList.get(key);
			}
			i++;
		}
		return null;
	}
	
	public Series getLargestSeries() {
		int max = 0;
		int runningSize = 0;
		String largestIndex = null;
		
		for(String s: this.seriesList.keySet()) {
			runningSize = this.seriesList.get(s).size();
			if(runningSize > max) {
				max = runningSize;
				largestIndex = s;
			}
		}
		
		if(this.seriesList.size() <= 0) {
			return null;
		}
		
		return this.seriesList.get(largestIndex);
	}
	
	
	private void initChartDrawables() {
		//Log.v(TAG, "initChartDrawables");
		
		this.drawables.clear();
		
		for(String s: this.seriesList.keySet()) {
			Series series = seriesList.get(s);
			this.setDrawablePointRects(series, this.chartContainer);
			
			ArrayList<Drawable> tmpDrawables = this.getSeriesDrawables(series, this.chartContainer);
			
			this.drawables.addAll(tmpDrawables);
		}
		
		if(showYHilight) {
			this.drawables.add(yHilightDrawable);
		}
	}
	
	private void setDrawablePointRects(Series series, Rect container) {
		int maxHeight = container.height();
		Log.v(TAG, "CT:"+ container.top);
		Log.v(TAG, "H1:"+ (container.bottom-container.top));
		Log.v(TAG, "H2:"+maxHeight);
		
		ArrayList<ChartRect> drawablePointAreas = new ArrayList<ChartRect>();
		ArrayList<Value> values = series.getValues();
		
		// If the right most point will end up off the screen, shift the chart left
		// just enough to keep the right point visible. This of course hides some of the left points.
		int pointXShift = 0;
		int lastX = getPointX(values.size() - 1, pointWidth, pointPaddingRight, container.left);
		if(lastX  > container.right) {
			pointXShift = container.right - lastX;
		}
		
		/*
		 * Create the point drawable areas
		 */
		Value valueObj = null;
		Double value = null;
		Double calcValue = null;
		for(int i = 0; i < values.size(); i++) {
			valueObj = values.get(i);
			value = valueObj.getValue();
			calcValue = value;

			if(calcValue == null) {
				calcValue = 0.00;
			}
			
			int x = getPointX(i, pointWidth, pointPaddingRight, container.left) + pointXShift;
			int y = getPointY(maxHeight, calcValue);
			
			int left = x;
			int top  = maxHeight - y;
			int right = left + pointWidth;
			int bottom = maxHeight;
			
			valueObj.setBounds(new Rect(left, top, right, bottom));
		}
	}
	
	
	private ArrayList<Drawable> getSeriesDrawables(Series series, Rect container) {
		ArrayList<SeriesDrawable> shapeDrawables = series.getSeriesDrawables(container.width(), container.height());
		ArrayList<Drawable> extraDrawables = series.getExtraDrawables(container.width(), container.height());
		ArrayList<Drawable> allDrawables = new ArrayList<Drawable>();
		
		/*// Add markers to denote where notes exist.
		for(int i = 0; i < shapeDrawables.size(); i++) {
			SeriesDrawable sd = shapeDrawables.get(i);
			
			if(sd.isHilightEnabled()) {
				ShapeDrawable s = new ShapeDrawable(new RectShape());
				s.setBounds(sd.getBounds().left, container.top, sd.getBounds().right, container.top + 10);
				allDrawables.add(s);
			}
		}*/
		
		allDrawables.addAll(extraDrawables);
		allDrawables.addAll(shapeDrawables);
		
		/*for(int i = 0; i < allDrawables.size(); i++) {
			allDrawables.get(i).getBounds().top += container.top;
		}*/
		
		return allDrawables;
	}
	
	private static int getPointX(int pointNum, int pointWidth, int pointPaddingRight, int containerLeft) {
		return (pointNum * (pointWidth + pointPaddingRight)) + containerLeft;
	}
	
	private static int getPointY(int maxHeight, double value) {
		return (int) (maxHeight * value / 100);
	}

	@Override
	public void onDraw(Canvas canvas) {
		for(int i = 0; i < this.drawables.size(); i++) {
			this.drawables.get(i).draw(canvas);
		}
		
		if(isShowYHilight()) {
			drawYHilightBar(canvas);
		}
		
		drawAxis(canvas);
	}
	
	private void drawAxis(Canvas canvas) {
		int axisOffset = 3;
		int hashWidth = 10;
		int lineThickness = 3;
		ShapeDrawable shape;
		
		int top = this.chartContainer.top;
		int bottom = this.chartContainer.bottom;
		int height = this.chartContainer.height();
		
		// Draw the vertical axis
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			top, 
			axisOffset + lineThickness, 
			bottom
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the top hash mark
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			top, 
			axisOffset + hashWidth, 
			top+lineThickness
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the center hash mark
		shape = new ShapeDrawable(new RectShape());
		int centerTop = Math.round(((height + top) / 2) - (lineThickness / 2));
		shape.setBounds(
			axisOffset, 
			(int)centerTop, 
			axisOffset + hashWidth, 
			(int)centerTop + lineThickness
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the bottom hash mark
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			bottom - lineThickness, 
			axisOffset + hashWidth, 
			bottom
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
	}
	
	private void drawYHilightBar(Canvas canvas) {
		if(yHilightDrawableLabel == null || yHilightDrawableLabel.length() == 0) {
			return;
		}
		
		int textMargin = 5;
		int textSize = this.getHeight() / 15;
		
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Style.FILL);
		p.setAntiAlias(true);
		p.setTextSize(textSize);
		p.setShadowLayer(5.0f, 2.0f, 2.0f, Color.BLACK);
		
		Rect bounds = this.yHilightDrawable.getBounds();
		int textWidth = (int)p.measureText(yHilightDrawableLabel);
		int leftPos = bounds.right + textMargin;
		
		if(leftPos + textWidth > this.getWidth()) {
			leftPos = bounds.left - textWidth - textMargin;
		}
		
		canvas.drawText(
			yHilightDrawableLabel, 
			leftPos,
			bounds.bottom - (textSize / 2), 
			p
		);
		
		canvas.drawText(
			yHilightDrawableLabel, 
			leftPos,
			bounds.top + textSize + (textSize / 2), 
			p
		);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Series largestSeries = this.getLargestSeries();
		
		if(largestSeries == null) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			return;
		}
		
		int width = (pointWidth + pointPaddingRight) * largestSeries.size();
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(height == 0) {
			height = 100;
		}
		
		this.setMeasuredDimension(
			getMeasurement(widthMeasureSpec, width), 
			getMeasurement(heightMeasureSpec, height)
		);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		this.chartContainer = new Rect(15, 0, this.getWidth() - 15, this.getHeight());
		
		this.pointWidth = this.chartContainer.width() / 30;
		this.pointPaddingRight = pointWidth / 2;
		
		this.initChartDrawables();
		
		// Style the hilight.
		yHilightDrawable.getPaint().setStyle(Style.FILL);
		yHilightDrawable.getPaint().setColor(Color.argb(100, 0, 255, 0));
		yHilightDrawable.setBounds(
			this.chartContainer.left, 
			this.chartContainer.top, 
			this.chartContainer.left + this.pointWidth,
			this.chartContainer.bottom
		);
		
		if(this.getYHilightBounds().left <= this.chartContainer.left) {
			this.moveYHilightTo(this.chartContainer.right);
		}
	}
	
	
	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		
		switch(MeasureSpec.getMode(measureSpec)) {
			case MeasureSpec.EXACTLY:
				return specSize;
			case MeasureSpec.AT_MOST:
				return Math.min(preferred, specSize);
			default:
				return preferred;
		}
	}
	
	public ArrayList<KeyBoxData> getKey() {
		ArrayList<KeyBoxData> output = new ArrayList<KeyBoxData>();
		
		for(String s: this.seriesList.keySet()) {
			int fillColor = this.seriesList.get(s).getFillColor();
			int strokeColor = this.seriesList.get(s).getStrokeColor();
			String name = this.seriesList.get(s).getName();
			
			KeyBoxData keyBox = new KeyBoxData(name, fillColor, strokeColor);
			output.add(keyBox);
		}
		
		return output;
		
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(isShowYHilight()) {
				this.moveYHilightTo((int)event.getX());
			}
			
		} else if(event.getHistorySize() < 3 && event.getAction() == MotionEvent.ACTION_UP) {
			/*if(dispatchClickToSeriesDrawable(event)) {
				Log.v(TAG, "Dispatched");
			} else */if(isShowYHilight()) {
				this.moveYHilightTo((int)event.getX());
			}
		}
				
		this.invalidate();
		return true;
	}
	

	private int getSeriesXBetween(int leftX, int rightX, Series firstSeries) {
		ArrayList<Value> values = firstSeries.getValues();
		
		int currX = 0;
		for(int i = values.size()-1; i >= 0; i--) {
			Rect currBounds = values.get(i).getBounds();
			currX = currBounds.left + ((currBounds.right - currBounds.left) / 2);
			
			if(rightX >= currX) {
				return currBounds.left;
			}
		}
		
		return -1;
	}

	private int getSeriesIndexBetween(int leftX, int rightX, Series firstSeries) {
		ArrayList<Value> values = firstSeries.getValues();
		
		int currX = 0;
		for(int i = values.size()-1; i >= 0; i--) {
			Rect currBounds = values.get(i).getBounds();
			currX = currBounds.left + ((currBounds.right - currBounds.left) / 2);
			
			if(rightX >= currX) {
				return i;
			}
		}
		
		return -1;
	}
	
	public Rect getYHilightBounds() {
		return this.yHilightDrawable.copyBounds();
	}
	
	public int getYHilightSeriesIndex() {
		return this.yHilightSeriesIndex;
	}
	
	public void moveYHilightTo(int x) {
		// Find the first series for this chart.
		Series firstSeries = null;
		for(String key: this.seriesList.keySet()) {
			firstSeries = this.seriesList.get(key);
			break;
		}
		
		ArrayList<Value> values = firstSeries.getValues();
		if(values == null || values.size() <= 0) {
			return;
		}
		
		Rect lastPoint = values.get(values.size() - 1).getBounds();
		Rect firstPoint = values.get(0).getBounds();
		int seriesIndex = -1;
		
		// Make sure the hilight staying within the container of the chart.
		if(x < firstPoint.left) {
			x = firstPoint.left;
		}
		if(x > lastPoint.left) {
			x = lastPoint.left;
		}
		
		if(this.yHilightSnap) {
			x = getSeriesXBetween(
				x,
				x + this.pointWidth,
				firstSeries
			);
		}
		
		// Figure out which series index is at this position
		seriesIndex = getSeriesIndexBetween(
			x,
			x + this.pointWidth,
			firstSeries
		);
		
		// Move the hilight
		Rect bounds = this.yHilightDrawable.copyBounds();
		this.yHilightDrawable.setBounds(
			x, 
			bounds.top, 
			x + bounds.width(),
			bounds.bottom
		);
		
		// register the series index and label for the hilight.
		yHilightSeriesIndex = seriesIndex;
		yHilightDrawableLabel = firstSeries.getLabels().get(seriesIndex).getLabelString();
	}
}

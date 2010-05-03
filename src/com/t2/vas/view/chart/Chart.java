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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class Chart extends View implements OnLongClickListener, OnClickListener {
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
	private boolean eventMoving = false;
	private MotionEvent lastEvent = null;
	private ChartEventListener chartEventListener;
	
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
		this.setOnLongClickListener(this);
		this.setOnClickListener(this);
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
		
		allDrawables.addAll(extraDrawables);
		allDrawables.addAll(shapeDrawables);
		
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

	public void updateChart() {
		this.refreshData();
		this.initChartDrawables();
		this.invalidate();
	}
	
	public void refreshData() {
		// Tell the series to refresh their data based on their sources.
		for(String key: this.seriesList.keySet()) {
			this.seriesList.get(key).refreshData();
		}
	}
	
	/*@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		this.refreshData();
		
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
	}*/

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		// Update the chart data.
		this.refreshData();
		
		this.chartContainer = new Rect(15, 0, this.getWidth() - 15, this.getHeight());
		
		// Calculate the point width
		int conWidth = this.chartContainer.width();
		int conHeight = this.chartContainer.height();
		int maxValue = (conWidth < conHeight)?conHeight:conWidth;
		
		this.pointWidth = maxValue / 30;
		this.pointPaddingRight = pointWidth / 2;
		
		// Create the drawables.
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
	
	
	/*private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		
		switch(MeasureSpec.getMode(measureSpec)) {
			case MeasureSpec.EXACTLY:
				return specSize;
			case MeasureSpec.AT_MOST:
				return Math.min(preferred, specSize);
			default:
				return preferred;
		}
	}*/
	
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
	public void onClick(View v) {
		if(this.eventMoving) {
			return;
		}
		
		//Log.v(TAG, "click "+"LX:"+this.lastEvent.getX()+" LY:"+this.lastEvent.getY());
		if(isShowYHilight()) {
			this.moveYHilightTo((int)this.lastEvent.getX());
			this.invalidate();
		}
		
		if(this.chartEventListener != null) {
			this.chartEventListener.onChartClick(this, this.lastEvent);
		}
	}
	
	@Override
	public boolean onLongClick(View v) {
		if(this.eventMoving) {
			return false;
		}
		
		//Log.v(TAG, "longclick "+"LX:"+this.lastEvent.getX()+" LY:"+this.lastEvent.getY());
		if(isShowYHilight()) {
			this.moveYHilightTo((int)this.lastEvent.getX());
			this.invalidate();
		}
		
		if(this.chartEventListener != null) {
			this.chartEventListener.onChartLongClick(this, this.lastEvent);
		}
		
		return true;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.lastEvent = event;
		//Log.v(TAG, "event "+"LX:"+this.lastEvent.getX()+" LY:"+this.lastEvent.getY());
		
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			this.eventMoving = true;
			if(isShowYHilight()) {
				this.moveYHilightTo((int)event.getX());
				this.invalidate();
				return true;
			}
		}
		
		boolean ret = super.onTouchEvent(event);
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			this.eventMoving = false;			
		}
		
		return ret;
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
	
	public void setChartEventListener(ChartEventListener c) {
		this.chartEventListener = c;
	}
	
	public ChartEventListener getChartEventListenter() {
		return this.chartEventListener;
	}
	
	
	public interface ChartEventListener {
		public boolean onChartLongClick(Chart c, MotionEvent event);
		public boolean onChartClick(Chart c, MotionEvent event);
	}
}

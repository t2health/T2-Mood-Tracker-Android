package com.t2.vas.view.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
			ArrayList<ChartRect> points = getDrawablePointRects(series, this.chartContainer);
			ArrayList<ShapeDrawable> tmpDrawables = this.getSeriesDrawables(series, points, this.chartContainer);
			
			this.drawables.addAll(tmpDrawables);
		}
		
		if(showYHilight) {
			this.drawables.add(yHilightDrawable);
		}
		
		//Series firstSeries = this.seriesList.get(0);
		
	}
	
	private ArrayList<ChartRect> getDrawablePointRects(Series series, Rect container) {
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
		Double prevValue = null;
		Double value = null;
		Double calcValue = null;
		for(int i = 0; i < values.size(); i++) {
			prevValue = value;
			value = values.get(i).getValue();
			calcValue = value;

			if(calcValue == null) {
				calcValue = 0.00;
			}
			
			int x = getPointX(i, pointWidth, pointPaddingRight, container.left) + pointXShift;
			int y = getPointY(maxHeight, calcValue);
			int nextX = getPointX(i+1, pointWidth, pointPaddingRight, container.left) + pointXShift;
			
			int left = x;
			int top  = maxHeight - y;
			int right = left + pointWidth;
			int bottom = maxHeight;
			
			drawablePointAreas.add(new ChartRect(left, top, right, bottom, value));
		}
		
		return drawablePointAreas;
	}
	
	private ArrayList<ShapeDrawable> getSeriesDrawables(Series series, ArrayList<ChartRect> drawablePointAreas, Rect container) {
		int maxHeight = container.height();
		
		ArrayList<ShapeDrawable> drawables = series.getDrawables(
				drawablePointAreas, 
				this.getWidth(), 
				maxHeight
		);
		
		// Set some other nice effects to the drawables.
		for(int i = 0; i < drawables.size(); i++) {
			drawables.get(i).getPaint().setAntiAlias(true);
			
			if(isDropShadowEnabled()) {
				drawables.get(i).getPaint().setShadowLayer(5, 3, 3, Color.argb(100, 0, 0, 0));
			}
		}
		
		return drawables;
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
		
		// Draw the vertical axis
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			0, 
			axisOffset + lineThickness, 
			this.getHeight()
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the top hash mark
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			0, 
			axisOffset + hashWidth, 
			lineThickness
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the center hash mark
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			(int)this.getHeight() / 2, 
			axisOffset + hashWidth, 
			((int)this.getHeight() / 2) + lineThickness
		);
		shape.getPaint().setColor(Color.BLACK);
		shape.getPaint().setStyle(Style.FILL);
		shape.draw(canvas);
		
		// Draw the bottom hash mark
		shape = new ShapeDrawable(new RectShape());
		shape.setBounds(
			axisOffset, 
			this.getHeight() - lineThickness, 
			axisOffset + hashWidth, 
			this.getHeight()
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
		
		this.chartContainer = new Rect(15, 0, this.getWidth() - 10, this.getHeight() - 20);
		
		this.pointWidth = this.chartContainer.width() / 30;
		this.pointPaddingRight = pointWidth / 2;
		
		this.chartContainer.bottom -= this.pointWidth;
		
		this.initChartDrawables();
		
		// Style the hilight.
		yHilightDrawable.getPaint().setStyle(Style.FILL);
		yHilightDrawable.getPaint().setColor(Color.argb(100, 0, 255, 0));
		
		if(this.getYHilightBounds().left <= 0) {
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

	private boolean selectSeriesDrawableAt(int x, int y, int boundsPadding) {
		for(Series s: this.seriesList.values()) {
			if(s.selectDrawableValueAt(x, y, boundsPadding)) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(isShowYHilight()) {
				this.moveYHilightTo((int)event.getX());
			}
			
		} else if(event.getHistorySize() < 3 && event.getAction() == MotionEvent.ACTION_UP) {
			if(dispatchClickToSeriesDrawable(event)) {
				Log.v(TAG, "Dispatched");
			} else if(isShowYHilight()) {
				this.moveYHilightTo((int)event.getX());
			}
		}
				
		this.invalidate();
		return true;
	}
	
	public boolean dispatchClickToSeriesDrawable(MotionEvent event) {
		int padding = (int)Math.ceil(this.pointPaddingRight / 2);
		
		for(int i = 0; i < event.getPointerCount(); i++) {
			//Log.v(TAG, "TOUCH Pointer:"+i);
			int pointerId = event.getPointerId(i);
			int x = (int)Math.round(event.getX(i));
			int y = (int)Math.round(event.getY(i));
			
			if(!selectSeriesDrawableAt(x, y, 0)) {
				if(!selectSeriesDrawableAt(x, y, padding)) {
					if(!selectSeriesDrawableAt(x, y, padding*2)) {
						return selectSeriesDrawableAt(x, y, padding*4);
					} else {
						return true;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}
	
	

	private int getSeriesXBetween(int leftX, int rightX, Series firstSeries) {
		ArrayList<ChartRect> points = getDrawablePointRects(firstSeries, this.chartContainer);
		
		int currX = 0;
		for(int i = points.size()-1; i >= 0; i--) {
			ChartRect currBounds = points.get(i);
			currX = currBounds.left + ((currBounds.right - currBounds.left) / 2);
			
			if(rightX >= currX) {
				return currBounds.left;
			}
		}
		
		return -1;
	}

	private int getSeriesIndexBetween(int leftX, int rightX, Series firstSeries) {
		ArrayList<ChartRect> points = getDrawablePointRects(firstSeries, this.chartContainer);
		
		int currX = 0;
		for(int i = points.size()-1; i >= 0; i--) {
			ChartRect currBounds = points.get(i);
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
		
		ArrayList<ChartRect> drawablePointRects = this.getDrawablePointRects(firstSeries, this.chartContainer);
		ChartRect lastPoint = drawablePointRects.get(drawablePointRects.size() - 1);
		ChartRect firstPoint = drawablePointRects.get(0);
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
		this.yHilightDrawable.setBounds(
			x, 
			0, 
			x + this.pointWidth,
			this.getHeight()
		);
		
		// register the series index and label for the hilight.
		yHilightSeriesIndex = seriesIndex;
		yHilightDrawableLabel = firstSeries.getLabels().get(seriesIndex).getLabelString();
	}
}

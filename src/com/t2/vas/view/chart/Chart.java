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
	
	private boolean showVerticalPositionBar = true;
	private ShapeDrawable verticalPositionBar = new ShapeDrawable(new RectShape());
	private String verticalPositionBarString = "";
	
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

	public void setShowVerticalPositionBar(boolean showVerticalPositionBar) {
		this.showVerticalPositionBar = showVerticalPositionBar;
	}

	public boolean isShowVerticalPositionBar() {
		return showVerticalPositionBar;
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
			ArrayList<ShapeDrawable> tmpDrawables = this.getSeriesDrawables(series, this.chartContainer);
			
			this.drawables.addAll(tmpDrawables);
		}
		
		if(showVerticalPositionBar) {
			this.drawables.add(verticalPositionBar);
		}
		
		Series firstSeries = this.seriesList.get(0);
		
	}
	
	private ArrayList<ShapeDrawable> getSeriesDrawables(Series series, Rect container) {
		//Log.v(TAG, "GetDrawables:"+ this.getWidth() +"x"+this.getHeight());
		int maxHeight = this.getHeight() - (pointWidth);
		
		// Factor in the chart should not goto the bottom (if specified)
		maxHeight -= (this.getHeight() - container.bottom);
		
		
		ArrayList<ChartRect> drawablePointAreas = new ArrayList<ChartRect>();
		ArrayList<Double> values = series.getValues();
		
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
			value = values.get(i);
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
		
		ArrayList<ShapeDrawable> drawables = series.getDrawables(drawablePointAreas, this.getWidth(), maxHeight);
		
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
		
		if(isShowVerticalPositionBar()) {
			drawVerticalPositionBarText(canvas);
		}
	}
	
	private void drawVerticalPositionBarText(Canvas canvas) {
		if(verticalPositionBarString == null || verticalPositionBarString.length() == 0) {
			return;
		}
		
		int textMargin = 5;
		int textSize = this.getHeight() / 15;
		
		Paint p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Style.FILL);
		p.setAntiAlias(true);
		p.setTextSize(textSize);
		
		Rect bounds = this.verticalPositionBar.getBounds();
		int textWidth = (int)p.measureText(verticalPositionBarString);
		int leftPos = bounds.right + textMargin;
		
		if(leftPos + textWidth > this.getWidth()) {
			leftPos = bounds.left - textWidth - textMargin;
		}
		
		canvas.drawText(
			verticalPositionBarString, 
			leftPos,
			bounds.bottom - (textSize / 2), 
			p
		);
		
		canvas.drawText(
			verticalPositionBarString, 
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
		
		this.chartContainer = new Rect(0, 0, this.getWidth(), this.getHeight() - 20);
		
		this.pointWidth = this.getWidth() / 30;
		this.pointPaddingRight = pointWidth / 2;
		
		this.initChartDrawables();
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

	
	public String getSeriesLabelAt(int leftX, int rightX, Series firstSeries) {
		int currX = 0;
		for(int i = firstSeries.drawablesSize()-1; i >= 0; i--) {
			SeriesDrawable d = firstSeries.getDrawableValueAt(i);
			
			Rect currBounds = d.getBounds();
			currX = currBounds.left + ((currBounds.right - currBounds.left) / 2);
			//currX = currBounds.left;
			
			if(rightX >= currX) {
				return firstSeries.getLabels().get(i);
			}
		}
		return null;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(isShowVerticalPositionBar()) {
				int actionX = (int)event.getX();
				this.verticalPositionBar.setBounds(
					actionX, 
					0, 
					actionX + this.pointWidth,
					this.getHeight()
				);
				verticalPositionBar.getPaint().setStyle(Style.FILL);
				verticalPositionBar.getPaint().setColor(Color.argb(100, 0, 255, 0));
				
				for(String key: this.seriesList.keySet()) {
					verticalPositionBarString = getSeriesLabelAt(
							actionX, 
							actionX + this.pointWidth, 
							this.seriesList.get(key)
					);
					break;
				}
			}
			
			
			
		} else if(event.getHistorySize() < 3 && event.getAction() == MotionEvent.ACTION_UP) {
			if(dispatchClickToSeriesDrawable(event)) {
				Log.v(TAG, "Dispatched");
				return true;
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
}

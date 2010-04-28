package com.t2.vas.view.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.t2.vas.db.tables.Scale.ResultValues;

import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.util.Log;

public class LineSeries extends Series {
	private static final String TAG = LineSeries.class.getName();
	
	public LineSeries(String name) {
		super(name);
	}

	public LineSeries(String name, ArrayList<Label> labels, ArrayList<Value> values) {
		super(name, labels, values);
	}
	
	private int lineFillColor = Color.BLUE;
	private int lineStrokeColor = Color.RED;
	
	public void setLineStrokeColor(int lineStrokeColor) {
		this.lineStrokeColor = lineStrokeColor;
	}

	public int getLineStrokeColor() {
		return lineStrokeColor;
	}
	
	public void setLineFillColor(int lineFillColor) {
		this.lineFillColor = lineFillColor;
	}

	public int getLineFillColor() {
		return lineFillColor;
	}

	protected SeriesDrawable onLoadDrawable(Value v, int pos, int width, int height) {
		if(v.getValue() == null) {
			return null;
		}
		
		LineSeriesDrawable point = new LineSeriesDrawable(v.getBoundsCopy());
		point.setFillColor(this.getFillColor());
		point.setStrokeColor(this.getStrokeColor());
		
		return point;
	}
	
	protected ArrayList<Drawable> onLoadExtraDrawables(int width, int height) {
		ArrayList<Drawable> drawables = new ArrayList<Drawable>();
		Path currentLinePath = new Path();
		
		ArrayList<Boolean> solidLinePaths = new ArrayList<Boolean>();
		ArrayList<Path> linePaths = new ArrayList<Path>();
		//Path linePath = new Path();
		
		if(this.values == null || this.values.size() <= 0) {
			return drawables;
		}
		
		solidLinePaths.add(true);
		linePaths.add(currentLinePath);
		
		Rect firstRect = this.values.get(0).getBoundsCopy();
		Rect tmpRect;
		int pointDiameter = firstRect.width();
		
		Value prevValue = null;
		Value currentValue = null;
		for(int i = 0; i < this.values.size(); i++) {
			prevValue = currentValue;
			currentValue = this.values.get(i);
			
			Rect currentValueBounds = currentValue.getBoundsCopy();
			Rect prevValueBounds = null;
			//LineSeriesDrawable point = new LineSeriesDrawable(currentValue.getBoundsCopy());
			
			int cLeft = currentValueBounds.left;
			int cTop  = currentValueBounds.top;
			int cRight = currentValueBounds.right;
			int cBottom = currentValueBounds.bottom;

			int pLeft = 0;
			int pTop = 0;
			int pRight = 0;
			int pBottom = 0;

			if(prevValue != null) {
				prevValueBounds = prevValue.getBoundsCopy();
				pLeft = prevValueBounds.left;
				pTop  = prevValueBounds.top;
				pRight = prevValueBounds.right;
				pBottom = prevValueBounds.bottom;
			}
			
			/*point.setFillColor(this.getFillColor());
			point.setStrokeColor(this.getStrokeColor());
			
			if(currentValue.getValue() != null) {
				drawables.add(point);
			}*/
			
			
			
			if(i == 0 || prevValue == null) {
				currentLinePath.moveTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
				
			// create a dashed path
			} else if(currentValue.getValue() == null || prevValue.getValue() == null) {
				if(prevValue.getValue() != null) {
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(false);
					
					currentLinePath.moveTo(pLeft + (pointDiameter / 2), pTop + (pointDiameter / 2));
				}
				
				if(currentValue.getValue() != null) {
					currentLinePath.lineTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
					
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(true);
					
					currentLinePath.moveTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
				}
			} else {
				currentLinePath.lineTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
			}
		}
		
		//Log.v(TAG, "PATH COUNT:"+linePaths.size());
		for(int i = 0; i < linePaths.size(); i++) {
			Path linePath = linePaths.get(i);
			boolean solidPath = solidLinePaths.get(i);
			
			ShapeDrawable psd;
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineFillColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter / 2);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineStrokeColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
		}
		
		return drawables;
	}
	
	protected ArrayList<ShapeDrawable> loadDrawables(int width, int height) {
		ArrayList<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
		Path currentLinePath = new Path();
		
		ArrayList<Boolean> solidLinePaths = new ArrayList<Boolean>();
		ArrayList<Path> linePaths = new ArrayList<Path>();
		//Path linePath = new Path();
		
		if(this.values == null || this.values.size() <= 0) {
			return drawables;
		}
		
		solidLinePaths.add(true);
		linePaths.add(currentLinePath);
		
		Rect firstRect = this.values.get(0).getBoundsCopy();
		Rect tmpRect;
		int pointDiameter = firstRect.width();
		
		Value prevValue = null;
		Value currentValue = null;
		for(int i = 0; i < this.values.size(); i++) {
			prevValue = currentValue;
			currentValue = this.values.get(i);
			
			Rect currentValueBounds = currentValue.getBoundsCopy();
			Rect prevValueBounds = null;
			LineSeriesDrawable point = new LineSeriesDrawable(currentValue.getBoundsCopy());
			
			int cLeft = currentValueBounds.left;
			int cTop  = currentValueBounds.top;
			int cRight = currentValueBounds.right;
			int cBottom = currentValueBounds.bottom;

			int pLeft = 0;
			int pTop = 0;
			int pRight = 0;
			int pBottom = 0;

			if(prevValue != null) {
				prevValueBounds = prevValue.getBoundsCopy();
				pLeft = prevValueBounds.left;
				pTop  = prevValueBounds.top;
				pRight = prevValueBounds.right;
				pBottom = prevValueBounds.bottom;
			}
			
			point.setFillColor(this.getFillColor());
			point.setStrokeColor(this.getStrokeColor());
			
			if(currentValue.getValue() != null) {
				drawables.add(point);
			}
			
			
			
			if(i == 0 || prevValue == null) {
				currentLinePath.moveTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
				
			// create a dashed path
			} else if(currentValue.getValue() == null || prevValue.getValue() == null) {
				if(prevValue.getValue() != null) {
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(false);
					
					currentLinePath.moveTo(pLeft + (pointDiameter / 2), pTop + (pointDiameter / 2));
				}
				
				if(currentValue.getValue() != null) {
					currentLinePath.lineTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
					
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(true);
					
					currentLinePath.moveTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
				}
			} else {
				currentLinePath.lineTo(cLeft + (pointDiameter / 2), cTop + (pointDiameter / 2));
			}
		}
		
		//Log.v(TAG, "PATH COUNT:"+linePaths.size());
		for(int i = 0; i < linePaths.size(); i++) {
			Path linePath = linePaths.get(i);
			boolean solidPath = solidLinePaths.get(i);
			
			ShapeDrawable psd;
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineFillColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter / 2);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineStrokeColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
		}
		
		return drawables;
	}
	/*protected ArrayList<ShapeDrawable> loadDrawables(ArrayList<ChartRect> areas, int width, int height) {
		ArrayList<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
		Path currentLinePath = new Path();
		
		ArrayList<Boolean> solidLinePaths = new ArrayList<Boolean>();
		ArrayList<Path> linePaths = new ArrayList<Path>();
		//Path linePath = new Path();
		
		if(areas == null || areas.size() <= 0) {
			return drawables;
		}
		
		solidLinePaths.add(true);
		linePaths.add(currentLinePath);
		
		ChartRect firstRect = areas.get(0).copy();
		Rect tmpRect;
		int pointDiameter = firstRect.width();
		
		ChartRect prevChartRect = null;
		ChartRect currentChartRect = null;
		for(int i = 0; i < areas.size(); i++) {
			prevChartRect = currentChartRect;
			currentChartRect = areas.get(i).copy();
			
			LineSeriesDrawable point = new LineSeriesDrawable(currentChartRect.toRect());
			
			
			point.setFillColor(this.getFillColor());
			point.setStrokeColor(this.getStrokeColor());
			
			if(currentChartRect.value != null) {
				drawables.add(point);
			}
			
			tmpRect = currentChartRect.toRect();
			int left = tmpRect.left;
			int top  = tmpRect.top;
			int right = tmpRect.right;
			int bottom = tmpRect.bottom;
			
			if(i == 0 || prevChartRect == null) {
				currentLinePath.moveTo(left + (pointDiameter / 2), top + (pointDiameter / 2));
				
			// create a dashed path
			} else if(currentChartRect.value == null || prevChartRect.value == null) {
				if(prevChartRect.value != null) {
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(false);
					
					currentLinePath.moveTo(prevChartRect.left + (pointDiameter / 2), prevChartRect.top + (pointDiameter / 2));
				}
				
				if(currentChartRect.value != null) {
					currentLinePath.lineTo(currentChartRect.left + (pointDiameter / 2), currentChartRect.top + (pointDiameter / 2));
					
					currentLinePath = new Path();
					linePaths.add(currentLinePath);
					solidLinePaths.add(true);
					
					currentLinePath.moveTo(currentChartRect.left + (pointDiameter / 2), currentChartRect.top + (pointDiameter / 2));
				}
			} else {
				currentLinePath.lineTo(left + (pointDiameter / 2), top + (pointDiameter / 2));
			}
		}
		
		//Log.v(TAG, "PATH COUNT:"+linePaths.size());
		for(int i = 0; i < linePaths.size(); i++) {
			Path linePath = linePaths.get(i);
			boolean solidPath = solidLinePaths.get(i);
			
			ShapeDrawable psd;
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineFillColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter / 2);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
			
			psd = new ShapeDrawable(new PathShape(linePath, width, height));
			psd.getPaint().setPathEffect(new CornerPathEffect(pointDiameter / 4));
			psd.getPaint().setColor(lineStrokeColor);
			psd.getPaint().setStyle(Style.STROKE);
			psd.getPaint().setStrokeWidth(pointDiameter);
			psd.setBounds(0, 0, width, height);
			if(!solidPath) {
				psd.getPaint().setPathEffect(new DashPathEffect(new float[]{5.0f, 5.0f}, 0));
			}
			drawables.add(0, psd);
		}
		
		return drawables;
	}*/
}

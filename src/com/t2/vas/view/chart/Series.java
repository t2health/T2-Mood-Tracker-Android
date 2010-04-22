package com.t2.vas.view.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.t2.vas.db.tables.Scale.ResultValues;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.PathShape;
import android.util.Log;

public abstract class Series {
	private static final String TAG = Series.class.getName();
	
	private String name = "";
	protected int drawableResid;
	
	protected int fillColor = Color.RED;
	protected int strokeColor = Color.GREEN;
	
	protected ArrayList<Label> labels = new ArrayList<Label>();
	protected ArrayList<Value> values = new ArrayList<Value>();
	
	
	private ArrayList<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
	private ArrayList<SeriesDrawable> seriesDrawables = new ArrayList<SeriesDrawable>();
	
	public Series(String name) {
		this.setName(name);
	}
	
	public Series(String name, ArrayList<Label> labels, ArrayList<Value> values) {
		this.setName(name);
		this.addAllLabels(labels);
		this.addAllValues(values);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setFillColor(int fillColor) {
		this.fillColor = fillColor;
	}

	public int getFillColor() {
		return fillColor;
	}

	public void setStrokeColor(int strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getStrokeColor() {
		return strokeColor;
	}
	
	public void addValue(Value v) {
		this.values.add(v);
	}
	public void addAllValues(Value[] v) {
		for(int i = 0; i < v.length; i++) {
			this.values.add(v[i]);
		}
	}
	public void addAllValues(ArrayList<Value> v) {
		this.values.addAll(v);
	}
	public ArrayList<Value> getValues() {
		return this.values;
	}
	
	public int size() {
		return this.values.size();
	}
	
	public int drawablesSize() {
		return this.seriesDrawables.size();
	}
	
	
	public void addLabel(Label l) {
		this.labels.add(l);
	}
	public void addAllLabels(Label[] l) {
		for(int i = 0; i < l.length; i++) {
			this.labels.add(l[i]);
		}
	}
	public void addAllLabels(ArrayList<Label> l) {
		this.labels.addAll(l);
	}
	public ArrayList<Label> getLabels() {
		return this.labels;
	}
	
	
	public final ArrayList<ShapeDrawable> getDrawables(ArrayList<ChartRect> areas, int width, int height) {
		if(this.drawables.size() > 0) {
			return this.drawables;
		}
		
		this.drawables = this.loadDrawables(areas, width, height);
		
		// Create a list of just the SeriesDrawable objects.
		for(int i = 0; i < this.drawables.size(); i++) {
			Drawable d = this.drawables.get(i);
			
			try {
				this.seriesDrawables.add((SeriesDrawable)d);
			} catch(ClassCastException cce) {}
		}
		
		return this.drawables;
	}
	
	protected abstract ArrayList<ShapeDrawable> loadDrawables(ArrayList<ChartRect> areas, int width, int height);
}

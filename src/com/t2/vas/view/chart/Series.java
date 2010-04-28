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
	
	private int hilightColor = Color.YELLOW;
	
	protected ArrayList<Label> labels = new ArrayList<Label>();
	protected ArrayList<Value> values = new ArrayList<Value>();
	
	private ArrayList<Drawable> drawables = new ArrayList<Drawable>();
	private ArrayList<SeriesDrawable> seriesDrawables = new ArrayList<SeriesDrawable>();
	
	private SeriesDataAdapter dataAdapter = null;
	
	public Series(String name) {
		this.setName(name);
	}
	
	public Series(String name, ArrayList<Label> labels, ArrayList<Value> values) {
		this.setName(name);
		this.addAll(labels, values);
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
	
	public void setHilightColor(int hilightColor) {
		this.hilightColor = hilightColor;
	}

	public int getHilightColor() {
		return hilightColor;
	}

	public int size() {
		return this.values.size();
	}
	
	public int drawablesSize() {
		return this.seriesDrawables.size();
	}
	
	public void add(Label l) {
		this.add(l, null);
	}
	
	public void add(Label l, Value v) {
		this.labels.add(l);
		this.values.add(v);
	}
	
	public void addAll(ArrayList<Label> l, ArrayList<Value> v) {
		for(int i = 0; i < l.size(); i++) {
			Value val = null;
			if(i <= v.size()-1) {
				val = v.get(i);
			}
			
			this.add(l.get(i), val);
		}
	}
	
	public void addAll(Label[] l, Value[] v) {
		for(int i = 0; i < l.length; i++) {
			Value val = null;
			if(i <= v.length-1) {
				val = v[i];
			}
			
			this.add(l[i], val);
		}
	}
	
	public void clear() {
		this.labels.clear();
		this.values.clear();
	}
	
	public ArrayList<Value> getValues() {
		return this.values;
	}
	
	public ArrayList<Label> getLabels() {
		return this.labels;
	}
	
	public final ArrayList<SeriesDrawable> getSeriesDrawables(int width, int height) {
		if(this.seriesDrawables.size() > 0) {
			return this.seriesDrawables;
		}
		
		this.seriesDrawables.clear();
		
		for(int i = 0; i < this.values.size(); i++) {
			Value val = this.values.get(i);
			SeriesDrawable sD = this.onLoadDrawable(val, i, width, height);
			
			if(sD == null) {
				continue;
			}
			
			sD.setHilightEnabled(val.isHilight());
			sD.setValue(val);
			
			seriesDrawables.add(sD);
		}
		
		return seriesDrawables;
	}
	
	public final ArrayList<Drawable> getExtraDrawables(int width, int height) {
		if(this.drawables.size() > 0) {
			return this.drawables;
		}
		
		this.drawables.clear();
		
		// Add the extras to the drawable list first
		ArrayList<Drawable> extras = this.onLoadExtraDrawables(width, height);
		for(int i = 0; i < extras.size(); i++) {
			this.drawables.add(extras.get(i));
		}
		
		return this.drawables;
	}
	
	protected abstract SeriesDrawable onLoadDrawable(Value v, int pos, int width, int height);
	protected ArrayList<Drawable> onLoadExtraDrawables(int width, int height) {
		return new ArrayList<Drawable>();
	}
	
	
	public final void refreshData() {
		if(dataAdapter == null) {
			return;
		}
		
		SeriesAdapterData data = dataAdapter.getData();
		
		this.clear();
		this.addAll(data.getLabels(), data.getValues());
		
		// Clear the drawable cache
		this.drawables.clear();
		this.seriesDrawables.clear();
	}
	
	public void setSeriesDataAdapter(SeriesDataAdapter s) {
		this.dataAdapter = s;
	}
	
	public interface SeriesDataAdapter {
		public SeriesAdapterData getData();
	}
}

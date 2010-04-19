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
	
	private int selectedFillColor = Color.YELLOW;
	private int selectedStrokeColor = Color.RED;
	
	
	protected ArrayList<Label> labels = new ArrayList<Label>();
	protected ArrayList<Value> values = new ArrayList<Value>();
	/*protected ArrayList<String> labels = new ArrayList<String>();
	protected ArrayList<String> labelValues = new ArrayList<String>();*/
	/*protected ArrayList<Double> values = new ArrayList<Double>();
	protected ArrayList<Object> valuesMeta = new ArrayList<Object>();*/
	
	private boolean isSelectable = true;
	
	private ArrayList<ShapeDrawable> drawables = new ArrayList<ShapeDrawable>();
	private ArrayList<SeriesDrawable> seriesDrawables = new ArrayList<SeriesDrawable>();
	
	private SeriesDrawableListener seriesDrawableSelected;
	
	private boolean isSeriesSelected = false;
	
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

	public void setSelectedFillColor(int selectedFillColor) {
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
	}

	public void setSelectable(boolean isSelectable) {
		this.isSelectable = isSelectable;
	}

	public boolean isSelectable() {
		return isSelectable;
	}

	public void setSeriesSelected(boolean isSeriesSelected) {
		if(!this.isSelectable) {
			this.isSeriesSelected = false;
			return;
		}
		
		this.isSeriesSelected = isSeriesSelected;
		
		if(isSeriesSelected) {
			this.onSeriesSelected();
		} else {
			this.onSeriesDeselected();
		}
		
		
	}

	public void onSeriesDeselected() {
		
	}
	
	public void onSeriesSelected() {
		
	}

	public boolean isSeriesSelected() {
		return isSeriesSelected;
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
	/*public void addValue(Double value) {
		this.addValue(value, null);
	}
	public void addValue(Double value, Object meta) {
		this.values.add(value);
		this.valuesMeta.add(meta);
	}
	public void addAllValues(Double[] values) {
		for(int i = 0; i < values.length; i++) {
			this.addValue(values[i], null);
		}
	}
	public void addAllValues(Double[] values, Object[] metaValues) {
		int metaValuesLength = metaValues.length;
		for(int i = 0; i < values.length; i++) {
			if(i >= metaValuesLength) {
				this.addValue(values[i], null);
			} else {
				this.addValue(values[i], metaValues[i]);				
			}
		}
	}
	public void addAllValues(ArrayList<Double> values) {
		for(int i = 0; i < values.size(); i++) {
			this.addValue(values.get(i), null);
		}
	}
	public void addAllValues(ArrayList<Double> values, ArrayList<Object> metaValues) {
		int metaValuesLength = metaValues.size();
		for(int i = 0; i < values.size(); i++) {
			if(i >= metaValuesLength) {
				this.addValue(values.get(i), null);
			} else {
				this.addValue(values.get(i), metaValues.get(i));				
			}
		}
	}
	
	
	public ArrayList<Double> getValues() {
		return this.values;
	}
	public ArrayList<Object> getValuesMeta() {
		return this.valuesMeta;
	}*/
	
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
	/*public void addLabel(String l) {
		this.labels.add(l);
	}
	public void addAllLabels(String[] l) {
		for(int i = 0; i < l.length; i++) {
			this.labels.add(l[i]);
		}
	}
	public void addAllLabels(ArrayList<String> l) {
		this.addAllLabels(l.toArray(new String[l.size()]));
	}
	public ArrayList<String> getLabels() {
		return this.labels;
	}
	
	
	public void addLabelValue(String l) {
		this.labelValues.add(l);
	}
	public void addAllLabelValues(String[] l) {
		for(int i = 0; i < l.length; i++) {
			this.labelValues.add(l[i]);
		}
	}
	public void addAllLabelValues(ArrayList<String> l) {
		this.addAllLabelValues(l.toArray(new String[l.size()]));
	}
	public ArrayList<String> getLabelValues() {
		return this.labelValues;
	}*/
	
	
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
	
	
	private boolean selectSeriesDrawable(SeriesDrawable d) {
		d.setSeleted();
		
		if(this.seriesDrawableSelected != null) {
			int index = this.seriesDrawables.indexOf(d);
			
			this.seriesDrawableSelected.onSeriesDrawableClicked(this, index);
			//this.seriesDrawableSelected.onSeriesDrawableSelected(this, index);
		}
		
		return true;
	}
	
	private boolean deselectSeriesDrawable(SeriesDrawable d) {
		d.setDeseleted();
		
		if(this.seriesDrawableSelected != null) {
			int index = this.seriesDrawables.indexOf(d);
			
			this.seriesDrawableSelected.onSeriesDrawableClicked(this, index);
			//this.seriesDrawableSelected.onSeriesDrawableDeselected(this, index);
		}
		
		return true;
	}
	
	public boolean selectDrawableValueAt(int x, int y, int boundsPadding) {
		if(!this.isSelectable()) {
			return false;
		}
		
		SeriesDrawable drawable;
		Rect bounds;
		
		for(int i = 0; i < seriesDrawables.size(); i++) {
			drawable = seriesDrawables.get(i);
			
			bounds = new Rect(drawable.getBounds());
			bounds.top -= boundsPadding;
			bounds.left -= boundsPadding;
			bounds.bottom += boundsPadding;
			bounds.right += boundsPadding;
			
			//Log.v(TAG, "  check:"+x+","+y);
			if(bounds.contains(x, y)) {
				//Log.v(TAG, "  Contains!");
				if(drawable.isSelected()) {
					this.deselectSeriesDrawable(drawable);
				} else {
					this.selectSeriesDrawable(drawable);
				}
				return true;
			}
		}
		
		return false;
	}
	
	public void selectDrawableValueAt(int pos) {
		//return this.selectSeriesDrawable(this.getDrawableValueAt(pos));
		this.getDrawableValueAt(pos).setSeleted();
	}
	
	public SeriesDrawable getDrawableValueAt(int pos) {
		if(pos > this.seriesDrawables.size()) {
			return null;
		}
		
		return seriesDrawables.get(pos);
	}
	
	public void selectAllDrawableValues() {
		for(int i = 0; i < this.seriesDrawables.size(); i++) {
			this.seriesDrawables.get(i).setSeleted();
			//this.selectSeriesDrawable(this.seriesDrawables.get(i));
		}
	}
	
	public void deselectAllDrawableValues() {
		for(int i = 0; i < this.seriesDrawables.size(); i++) {
			this.seriesDrawables.get(i).setDeseleted();
			//this.deselectSeriesDrawable(this.seriesDrawables.get(i));
		}
	}
	
	
	public void setOnSeriesDrawbleListener(SeriesDrawableListener s) {
		this.seriesDrawableSelected = s;
	}
}

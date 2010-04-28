package com.t2.vas.view.chart;

import java.util.ArrayList;

public class SeriesAdapterData {
	private ArrayList<Value> values = new ArrayList<Value>();
	private ArrayList<Label> labels = new ArrayList<Label>();
	
	public void add(Label l, Value v) {
		this.labels.add(l);
		this.values.add(v);
	}
	
	public ArrayList<Value> getValues() {
		return this.values;
	}
	
	public ArrayList<Label> getLabels() {
		return this.labels;
	}
	
	public void clear() {
		this.labels.clear();
		this.values.clear();
	}
}

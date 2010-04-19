package com.t2.vas.view.chart;

public class Label<T> {
	private String labelString;
	private T labelValue;
	
	public Label(String label, T value) {
		this.labelString = label;
		this.labelValue = value;
	}
	
	public void setLabelString(String labelString) {
		this.labelString = labelString;
	}
	
	public String getLabelString() {
		return labelString;
	}
	
	public void setLabelValue(T labelValue) {
		this.labelValue = labelValue;
	}
	
	public T getLabelValue() {
		return labelValue;
	}

}

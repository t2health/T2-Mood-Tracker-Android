package com.t2.vas.view.chart;

public class KeyBoxData {
	private int fillColor;
	private int strokeColor;
	private String label;
	
	public KeyBoxData(String label, int fillColor, int stokeColor) {
		this.setLabel(label);
		this.setFillColor(fillColor);
		this.setStrokeColor(strokeColor);
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

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	
}

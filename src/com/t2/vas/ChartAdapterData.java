package com.t2.vas;

import java.util.ArrayList;

import com.t2.vas.view.chart.LineSeries;
import com.t2.vas.view.chart.Series;

public class ChartAdapterData {
	public ArrayList<Series> series = new ArrayList<Series>();
	public String minLabelText = "";
	public String maxLabelText = "";
	private long scaleId;
	
	public ChartAdapterData(String minLabelText, String maxLabelText) {
		this.minLabelText = minLabelText;
		this.maxLabelText = maxLabelText;
	}
	
	public ChartAdapterData(ArrayList<Series> series, String minLabelText, String maxLabelText) {
		this.series = series;
		this.minLabelText = minLabelText;
		this.maxLabelText = maxLabelText;
	}
	
	public void setScaleId(long id) {
		this.scaleId = id;
	}
	
	public long getScaleId() {
		return this.scaleId;
	}
}

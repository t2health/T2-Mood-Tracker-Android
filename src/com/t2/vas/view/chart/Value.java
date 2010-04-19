package com.t2.vas.view.chart;

public class Value<T> {
	private Double value;
	private T metaData;
	
	public Value(Double value, T metaData) {
		this.setValue(value);
		this.setMetaData(metaData);
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

	public void setMetaData(T metaData) {
		this.metaData = metaData;
	}

	public T getMetaData() {
		return metaData;
	}
}

package com.t2.vas.importexport;

public class ScaleStat {
	public String minLabel;
	public String maxLabel;
	public int resultsCount = 0;
	public long minResultTimestamp = -1;
	public long maxResultTimestamp = -1;
	
	public void addResult(long timestamp, int value) {
		++resultsCount;
		if(timestamp < minResultTimestamp || minResultTimestamp == -1) {
			minResultTimestamp = timestamp;
		}
		if(timestamp > maxResultTimestamp || maxResultTimestamp == -1) {
			maxResultTimestamp = timestamp;
		}
	}
}
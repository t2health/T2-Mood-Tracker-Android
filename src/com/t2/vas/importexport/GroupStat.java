package com.t2.vas.importexport;

import java.util.ArrayList;


public class GroupStat {
	public String title;
	public int resultsCount = 0;
	public long minResultTimestamp = -1;
	public long maxResultTimestamp = -1;
	
	public ArrayList<ScaleStat> scales = new ArrayList<ScaleStat>();
	
	public void addScale(ScaleStat s) {
		this.scales.add(s);
	}
	
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
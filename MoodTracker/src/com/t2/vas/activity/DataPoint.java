/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity;

import java.util.ArrayList;

public class DataPoint implements Comparable<DataPoint> {
	public final long time;
	private double valueSum = 0.00;
	private int count = 0;
	public double minValue = 0.00;
	public double maxValue = 0.00;
	private ArrayList<Double> values = new ArrayList<Double>();
	private double defaultValue = 0.00;

	public DataPoint(long time, double defaultValue) {
		this.time = time;
		this.defaultValue = defaultValue;
	}

	public void addValue(double val) {
		values.add(val);
		valueSum += val;
		++count;

		if (val > maxValue || count == 1) {
			maxValue = val;
		}

		if (val < minValue || count == 1) {
			minValue = val;
		}
	}

	public double getAverageValue() {
		if (valueSum == 0 && count == 0) {
			return defaultValue;
		}
		return valueSum / count;
	}

	public double[] getValues() {
		double[] out = new double[values.size()];
		for (int i = 0; i < values.size(); ++i) {
			out[i] = values.get(i);
		}
		return out;
	}

	@Override
	public int compareTo(DataPoint another) {
		if (this.time == another.time) {
			return 0;
		} else {
			return this.time > another.time ? 1 : -1;
		}
	}

}

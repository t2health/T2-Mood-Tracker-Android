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
package com.t2.vas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraysExtra {
	public static final double[] toArray(Double[] vals) {
		if(vals == null) {
			return null;
		}
		
		List<Double> arrList = Arrays.asList(vals);
		arrList.remove(null);
		
		double[] out = new double[arrList.size()];
		for(int i = 0; i < arrList.size(); ++i) {
			Double val = arrList.get(i);
			if(val != null) {
				out[i] = val;
			}
		}
		return out;
	}
	
	public static final String[] toStringArray(Object[] vals) {
		if(vals == null) {
			return null;
		}
		
		String[] out = new String[vals.length];
		for(int i = 0; i < vals.length; ++i) {
			out[i] = vals[i].toString();
		}
		return out;
	}
	
	public static final Long[] toLongArray(String[] vals) {
		if(vals == null) {
			return null;
		}
		
		ArrayList<Long> newVals = new ArrayList<Long>();
		for(int i = 0; i < vals.length; ++i) {
			try {
				Long val = Long.parseLong(vals[i]);
				newVals.add(val);
			} catch (Exception e) {}
		}
		newVals.remove(null);
		
		Long[] out = new Long[newVals.size()];
		for(int i = 0; i < newVals.size(); ++i) {
			out[i] = newVals.get(i);
		}
		
		return out;
	}
	
	public static final Integer[] toIntegerArray(String[] vals) {
		if(vals == null) {
			return null;
		}
		
		ArrayList<Integer> newVals = new ArrayList<Integer>();
		for(int i = 0; i < vals.length; ++i) {
			try {
				Integer val = Integer.parseInt(vals[i]);
				newVals.add(val);
			} catch (Exception e) {}
		}
		newVals.remove(null);
		
		Integer[] out = new Integer[newVals.size()];
		for(int i = 0; i < newVals.size(); ++i) {
			out[i] = newVals.get(i);
		}
		
		return out;
	}
	
	public static final Double[] toDoubleArray(String[] vals) {
		if(vals == null) {
			return null;
		}
		
		ArrayList<Double> newVals = new ArrayList<Double>();
		for(int i = 0; i < vals.length; ++i) {
			try {
				Double val = Double.parseDouble(vals[i]);
				newVals.add(val);
			} catch (Exception e) {}
		}
		newVals.remove(null);
		
		Double[] out = new Double[newVals.size()];
		for(int i = 0; i < newVals.size(); ++i) {
			out[i] = newVals.get(i);
		}
		
		return out;
	}
}

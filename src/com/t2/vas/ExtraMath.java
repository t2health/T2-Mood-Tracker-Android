package com.t2.vas;

public class ExtraMath {
	public static double mean(Integer[] v) {
		int total = 0;
		for(int i = 0; i < v.length; i++) {
			total += v[i];
		}
		
		return total / v.length;
	}
}

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

import android.content.Context;
import android.widget.ArrayAdapter;

import com.t2.vas.db.tables.Scale;

public class ScaleAdapter extends ArrayAdapter<Scale> {
	private ArrayList<Scale> items;
	//private LayoutInflater inflater;
	//private int layoutResId;
	private int[] values;
	
	
	public ScaleAdapter(Context context, int textViewResourceId, ArrayList<Scale> objects) {
		super(context, textViewResourceId, objects);
		
		//this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		//this.layoutResId = textViewResourceId;
		this.items = objects;
		
		values = new int[this.items.size()];
		
	}
	
	public int getProgressValuesAt(int pos) {
		return values[pos];
	}
	
	public void setProgressValueAt(int pos, int value) {
		this.values[pos] = value;
	}
}

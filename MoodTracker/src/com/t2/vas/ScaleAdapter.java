/*
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

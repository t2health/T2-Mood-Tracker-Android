package com.t2.vas.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;

public class SimpleAdapterEnableable extends SimpleAdapter {
	private HashMap<Integer,Boolean> disabledIndexes = new HashMap<Integer,Boolean>();
	
	public SimpleAdapterEnableable(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
	}
	
	public void setDisabledIndex(int i) {
		this.disabledIndexes.put(i, true);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		Boolean val = disabledIndexes.get(position);
		if(val == null) {
			return true;
		}
		
		if(val) {
			return false;
		}
		
		return true;
	}

	
}

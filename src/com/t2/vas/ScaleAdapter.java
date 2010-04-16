package com.t2.vas;

import java.util.ArrayList;

import com.t2.vas.activity.FormActivity;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.SliderWidget;
import com.t2.vas.view.SliderWidget.OnSliderWidgetChangeListener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ScaleAdapter extends ArrayAdapter<Scale> implements OnSliderWidgetChangeListener {
	private static final String TAG = ScaleAdapter.class.getName();
	private ArrayList<Scale> items;
	private LayoutInflater inflater;
	private int layoutResId;
	private int[] values;
	
	
	public ScaleAdapter(Context context, int textViewResourceId, ArrayList<Scale> objects) {
		super(context, textViewResourceId, objects);
		
		this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.layoutResId = textViewResourceId;
		this.items = objects;
		
		values = new int[this.items.size()];
		for(int i = 0; i < values.length; i++) {
			values[i] = 50;
		}
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		SliderWidget v = (SliderWidget)inflater.inflate(this.layoutResId, null);
		v.setOnSliderWidgetChangeListener(this);
		Scale data = items.get(position);
        if (data != null) {
        	v.setTag(data);
        	
        	v.setMinLabelText(data.min_label);
        	v.setMaxLabelText(data.max_label);
        	v.setProgress(values[position]);
        }
        
        return v;
	}
	
	@Override
	public void onProgressChanged(SliderWidget sliderWidget, int progress,
			boolean fromUser) {
		Scale s = (Scale)sliderWidget.getTag();
		int position = this.items.indexOf(s);
		
		values[position] = progress;
	}
	
	public int getProgressValuesAt(int pos) {
		return values[pos];
	}

	@Override
	public void onStartTrackingTouch(SliderWidget sliderWidget) {
		
	}

	@Override
	public void onStopTrackingTouch(SliderWidget sliderWidget) {
		
	}

	
}

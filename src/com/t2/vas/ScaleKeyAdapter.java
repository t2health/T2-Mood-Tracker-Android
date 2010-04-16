package com.t2.vas;

import java.util.ArrayList;

import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.SliderWidget;
import com.t2.vas.view.chart.Chart;
import com.t2.vas.view.chart.KeyBoxData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScaleKeyAdapter extends ArrayAdapter<ChartLayout> {
	private LayoutInflater inflater;
	private int layoutResId;
	private ArrayList<ChartLayout> items;

	public ScaleKeyAdapter(Context context, int textViewResourceId, ArrayList<ChartLayout> objects) {
		super(context, textViewResourceId, objects);
		
		this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
		this.layoutResId = textViewResourceId;
		this.items = objects;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ChartLayout chartLayout = this.items.get(position);
		ArrayList<KeyBoxData> key = chartLayout.getChart().getKey();
		
		if(key.size() < 1) {
			return null;
		}
		
		KeyBoxData data = key.get(0);
		String labelText = data.getLabel();
		
		String[] parts = labelText.split(" - ");
		String minLabelText = parts[0];
		String maxLabelText = parts[1];
		
		LinearLayout v = (LinearLayout)inflater.inflate(this.layoutResId, null);
		v.setTag(chartLayout);
		
		LinearLayout outer = (LinearLayout)v.findViewById(R.id.outerBox);
		LinearLayout inner = (LinearLayout)v.findViewById(R.id.innerBox);
		TextView minLabel = (TextView)v.findViewById(R.id.minLabel);
		TextView maxLabel = (TextView)v.findViewById(R.id.maxLabel);
		
		outer.setBackgroundColor(data.getStrokeColor());
		inner.setBackgroundColor(data.getFillColor());
		minLabel.setText(minLabelText);
		maxLabel.setText(maxLabelText);
		
		return v;
	}
}

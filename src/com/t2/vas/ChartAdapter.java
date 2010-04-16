package com.t2.vas;

import java.io.IOException;
import java.util.ArrayList;

import com.t2.vas.view.ChartLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ChartAdapter extends ArrayAdapter<ChartAdapterData> {
	private ArrayList<ChartAdapterData> items;
	private LayoutInflater inflater;

	public ChartAdapter(Context context, int layoutResourceId, ArrayList<ChartAdapterData> objects) {
		super(context, layoutResourceId, objects);
		this.items = objects;
		this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ChartLayout v = (ChartLayout)inflater.inflate(R.layout.chart_layout, null);
        
		ChartAdapterData data = items.get(position);
        if (data != null) {
        	v.setTag(data);
        	
        	// Set the labels of the chart
        	v.setYMaxLabel(data.maxLabelText);
        	v.setYMinLabel(data.minLabelText);
        	
        	// Add the series to the chart
        	for(int i = 0; i < data.series.size(); i++) {
        		//v.addChartSerie(Math.random()+"", data.series.get(i));
        		v.getChart().addSeries(Math.random()+"", data.series.get(i));
        	}
        }
        return v;
	}
}

package com.t2.vas.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.t2.vas.Global;
import com.t2.vas.GroupResultsSeriesDataAdapter;
import com.t2.vas.R;
import com.t2.vas.ScaleKeyAdapter;
import com.t2.vas.ScaleResultsSeriesDataAdapter;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ResultsLayoutAdapter extends ArrayAdapter<Group> {
	private Context context;
	private LayoutInflater layoutInflater;
	ArrayList<ResultsLayout> viewCache = new ArrayList<ResultsLayout>();
	private DBAdapter dbAdapter;

	public ResultsLayoutAdapter(Context context, DBAdapter dbAdapter, List<Group> objects) {
		super(context, 0, 0, objects);

		this.context = context;
		this.dbAdapter = dbAdapter;
		this.layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position < this.viewCache.size()) {
			View v = this.viewCache.get(position);
			ViewGroup parentView = (ViewGroup)v.getParent();

			if(parentView != null) {
				parentView.removeView(v);
			}
			return v;
		}

		Group activeGroup = this.getItem(position);
		ResultsLayout resultsLayout = (ResultsLayout)this.layoutInflater.inflate(R.layout.results_layout, null);

		resultsLayout.init(activeGroup, this.dbAdapter);

		this.viewCache.add(resultsLayout);

		return resultsLayout;
	}
}

package com.t2.vas.view;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.t2.vas.R;

public class VASGallerySimpleAdapter extends SimpleAdapter {

	private int mGalleryItemBackground;

	public VASGallerySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);

		TypedArray a = context.obtainStyledAttributes(R.styleable.CustomTheme);
        mGalleryItemBackground = a.getResourceId(
                R.styleable.CustomTheme_android_galleryItemBackground, 0);
        a.recycle();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		v.setBackgroundResource(mGalleryItemBackground);
		if(position == 0) {
			v.setSelected(true);
		}
		return v;
	}



}

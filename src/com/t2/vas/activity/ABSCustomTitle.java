package com.t2.vas.activity;

import com.t2.vas.R;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class ABSCustomTitle extends ABSActivity {
	private boolean initialized = false;
	
	@Override
	public void setContentView(int layoutResID) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(layoutResID);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view, params);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.setContentView(view);
		this.initCustomTitle();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		if(initialized) {
			setCustomTitle(title);
		} else {
			super.setTitle(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		if(initialized) {
			setCustomTitle(this.getResources().getString(titleId));
		} else {
			super.setTitle(titleId);
		}
	}
	
	protected void initCustomTitle() {
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		setCustomTitle(super.getTitle());
	}
	
	protected boolean isCustomTitleInitialized() {
		return initialized;
	}
	
	private void setCustomTitle(CharSequence text) {
		TextView tv = (TextView)this.findViewById(R.id.custom_title).findViewById(R.id.text1);
		tv.setText(text);
	}
}

package com.t2.vas.activity;

import java.util.Random;

import com.t2.vas.R;
import com.t2.vas.SharedPref;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public abstract class ABSStartupTipsActivity extends ABSCustomTitleActivity implements OnCheckedChangeListener {

	private TextView startupTipsView;
	protected boolean showStartupTips;
	private String[] startupTips;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startupTips = this.getResources().getStringArray(R.array.startup_tips);
		showStartupTips = SharedPref.getShowStartupTips(sharedPref);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		CheckBox startupTipsCheckbox = (CheckBox)this.findViewById(R.id.showTipsCheckbox);
		startupTipsCheckbox.setChecked(showStartupTips);
		startupTipsCheckbox.setOnCheckedChangeListener(this);
		
		// init the startup tips
		startupTipsView = (TextView)this.findViewById(R.id.startupTips);
		if(!showStartupTips) {
			startupTipsView.setVisibility(View.GONE);
		}
		startupTipsView.setText(
				startupTips[new Random().nextInt(startupTips.length)]
		);
	}



	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
		case R.id.showTipsCheckbox:
			SharedPref.setShowStartupTips(sharedPref, isChecked);
			if(isChecked) {
				startupTipsView.setVisibility(View.VISIBLE);
			} else {
				startupTipsView.setVisibility(View.GONE);
			}
		}
	}
}

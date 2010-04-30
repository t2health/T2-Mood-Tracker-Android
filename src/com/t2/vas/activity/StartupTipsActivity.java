package com.t2.vas.activity;

import java.util.Random;

import com.t2.vas.Global;
import com.t2.vas.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class StartupTipsActivity extends Activity implements OnCheckedChangeListener, OnClickListener {
	Random random = new Random();
	private SharedPreferences sharedPref;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String[] tips = this.getResources().getStringArray(R.array.startup_tips);
		if(tips == null || tips.length == 0) {
			return;
		}
		
		sharedPref = this.getSharedPreferences(Global.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		String selectedTip = tips[random.nextInt(tips.length)];
		
		this.setContentView(R.layout.startup_tips_activity);
		
		TextView textTextView = (TextView)this.findViewById(R.id.text);
		CheckBox disableCheckbox = (CheckBox)this.findViewById(R.id.hideStartupTips);
		View closeButton = this.findViewById(R.id.closeButton);
		
		textTextView.setText(selectedTip);
		
		closeButton.setOnClickListener(this);
		
		disableCheckbox.setOnCheckedChangeListener(this);
		disableCheckbox.setChecked(sharedPref.getBoolean("hide_startup_tips", false));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		sharedPref.edit().putBoolean("hide_startup_tips", isChecked).commit();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.closeButton:
				this.finish();
				break;
		}
		
	}
}

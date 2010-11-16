package com.t2.vas.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.t2.vas.R;

public class CustomTitle extends ABSActivity {
	public static final String EXTRA_BACK_BUTTON_TEXT = "previousActivityName";
	private boolean initialized = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		this.initCustomTitle();
	}

	@Override
	public void setContentView(View view) {
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
	
	public void setExtraButtonImage(int resId) {
		if(!initialized) {
			return;
		}
		
		ImageView v = (ImageView)this.findViewById(R.id.custom_title).findViewById(R.id.extraButton);
		v.setImageResource(resId);
		v.setVisibility(View.VISIBLE);
		v.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onExtraButtonPressed();
			}
		});
	}
	
	public void onExtraButtonPressed() {
		
	}
	
	
	private void initCustomTitle() {
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		setCustomTitle(super.getTitle());
		
		ImageButton backButton = (ImageButton)this.findViewById(R.id.backButton);
		if(backButton != null) {
			backButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			String name = this.getIntent().getStringExtra(EXTRA_BACK_BUTTON_TEXT);
			if(name == null) {
				backButton.setVisibility(View.GONE);
			} else {
				//backButton.setText(name);
				backButton.setVisibility(View.VISIBLE);
			}
			
		}
	}
	
	private void setCustomTitle(CharSequence text) {
		TextView tv = (TextView)this.findViewById(R.id.custom_title).findViewById(R.id.text1);
		tv.setText(text);
	}
}

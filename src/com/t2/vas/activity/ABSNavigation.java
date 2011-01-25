package com.t2.vas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.t2.vas.R;

public class ABSNavigation extends ABSSecurity {
	public static final String EXTRA_BACK_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_BACK_BUTTON_RESID = "leftButtonResId";
	
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	public static final String EXTRA_RIGHT_BUTTON_RESID = "leftButtonResId";
	
	private View leftButton;
	private View rightButton;
	
	protected void setBackButtonText(String s) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		Button b = new Button(this);
		b.setText(s);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackButtonPressed();
			}
		});
		
		this.leftButton = b;
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void setBackButtonImage(int imageRes) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		ImageButton b = new ImageButton(this);
		b.setImageResource(imageRes);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackButtonPressed();
			}
		});
		
		this.leftButton = b;
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.leftButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void onBackButtonPressed() {
		this.finish();
	}
	
	protected void setRightButtonText(String s) {
		Button b = new Button(this);
		b.setText(s);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRightButtonPresed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void setRightButtonImage(int imageRes) {
		if(!isCustomTitleInitialized()) {
			return;
		}
		
		ImageButton b = new ImageButton(this);
		b.setImageResource(imageRes);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onRightButtonPresed();
			}
		});
		
		this.rightButton = b;
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).removeAllViews();
		((ViewGroup)this.findViewById(R.id.rightButtonContainer)).addView(b, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
	
	protected void onRightButtonPresed() {
		
	}
	
	protected View getRightButton() {
		return rightButton;
	}
	
	protected View getLeftButton() {
		return leftButton;
	}

	@Override
	protected void initCustomTitle() {
		super.initCustomTitle();
		
		Intent i = this.getIntent();
		if(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT) != null) {
			this.setBackButtonText(i.getStringExtra(EXTRA_BACK_BUTTON_TEXT));
			
		} else if(i.getIntExtra(EXTRA_BACK_BUTTON_RESID, 0) != 0) {
			this.setBackButtonImage(i.getIntExtra(EXTRA_BACK_BUTTON_RESID, 0));
		}
		
		if(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT) != null) {
			this.setRightButtonText(i.getStringExtra(EXTRA_RIGHT_BUTTON_TEXT));
			
		} else if(i.getIntExtra(EXTRA_RIGHT_BUTTON_RESID, 0) != 0) {
			this.setRightButtonImage(i.getIntExtra(EXTRA_RIGHT_BUTTON_RESID, 0));
		}
	}
}

package com.t2.vas.activity.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nullwire.trace.ExceptionHandler;
import com.t2.vas.Analytics;
import com.t2.vas.Eula;
import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.activity.WebViewActivity;
import com.t2.vas.db.DBAdapter;

public class ABSPreferenceNavigation extends ABSSecurityPreference {
	private static final String TAG = ABSPreferenceNavigation.class.getSimpleName();
	
	public static final String EXTRA_BACK_BUTTON_TEXT = "leftButtonText";
	public static final String EXTRA_BACK_BUTTON_RESID = "leftButtonResId";
	
	public static final String EXTRA_RIGHT_BUTTON_TEXT = "rightButtonText";
	public static final String EXTRA_RIGHT_BUTTON_RESID = "leftButtonResId";
	private boolean initialized = false;
	
	private View leftButton;
	private View rightButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

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
	
	
	protected void setBackButtonText(String s) {
		if(!initialized) {
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
		if(!initialized) {
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
		if(!initialized) {
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
	
	private void initCustomTitle() {
		initialized = true;
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		setCustomTitle(super.getTitle());
		
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
	
	private void setCustomTitle(CharSequence text) {
		TextView tv = (TextView)this.findViewById(R.id.custom_title).findViewById(R.id.text1);
		tv.setText(text);
	}
}

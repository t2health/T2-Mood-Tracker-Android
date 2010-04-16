package com.t2.vas.activity;

import com.t2.vas.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class BaseActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onPause() {
		this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		super.onPause();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onContextItemSelected(MenuItem item) {
		Intent i;
		switch(item.getItemId()){
			case R.id.settings:
				
				return true;
			case R.id.groupEditor:
				return true;
		}
		
		return super.onContextItemSelected(item);
	}
}

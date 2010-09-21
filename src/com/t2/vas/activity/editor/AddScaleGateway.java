package com.t2.vas.activity.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.t2.vas.R;
import com.t2.vas.activity.ABSActivity;

public class AddScaleGateway extends ABSActivity {

	private long groupId;
	
	public static final int ADD_SCALE_ACTIVITY = 234;
	public static final int COPY_SCALE_ACTIVITY = 235;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.add_scale_gateway);
		
		if(this.groupId < 0) {
			this.finish();
		}
		
		this.findViewById(R.id.existingButton).setOnClickListener(this);
		this.findViewById(R.id.newButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = new Intent();
		
		switch(v.getId()) {
			case R.id.existingButton:
				intent.putExtra("action", COPY_SCALE_ACTIVITY);
				this.setResult(Activity.RESULT_OK, intent);
				this.finish();
				break;
				
			case R.id.newButton:
				intent.putExtra("action", ADD_SCALE_ACTIVITY);
				this.setResult(Activity.RESULT_OK, intent);
				this.finish();
				break;
		}
	}
}

package com.t2.vas.activity.editor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.db.tables.Scale;

public class DeleteScaleActivity extends ABSActivity implements OnClickListener {
	private Scale currentScale;
	private Toast toastPopup;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VASAnalytics.onEvent(VASAnalytics.EVENT_DELETE_SCALE_ACTIVITY);
		
		this.setContentView(R.layout.delete_group_activity);

		currentScale = ((Scale)dbAdapter.getTable("scale")).newInstance();
		toastPopup = Toast.makeText(this, R.string.scale_deleted, 2000);

		// init the local variables;
		Intent intent = this.getIntent();
		long currentScaleId = intent.getLongExtra("scale_id", -1);

		if(currentScaleId < 0) {
			this.finish();
		}

		currentScale._id = currentScaleId;
		// quit if this note doesn't exist.
		if(!currentScale.load()) {
			this.finish();
		}

		((Button)this.findViewById(R.id.yesButton)).setOnClickListener(this);
		((Button)this.findViewById(R.id.noButton)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.yesButton:

				currentScale.delete();

				toastPopup.show();

				this.finish();

				break;
			case R.id.noButton:
				this.setResult(Activity.RESULT_CANCELED);
				this.finish();
				break;
		}
	}
}

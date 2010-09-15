package com.t2.vas.activity.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Scale;

public class ScaleActivity extends ABSActivity implements OnClickListener {
	private static final String TAG = ScaleActivity.class.getName();
	private DBAdapter dbAdapter;
	private Scale currentScale;
	private Toast toastPopup;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VASAnalytics.onEvent(VASAnalytics.EVENT_ADD_EDIT_SCALE_ACTIVITY);

        // init global variables.
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		currentScale = ((Scale)dbAdapter.getTable("scale")).newInstance();
		toastPopup = Toast.makeText(this, R.string.scale_saved, 2000);

		Intent intent = this.getIntent();

		currentScale._id = intent.getLongExtra("scale_id", -1);
		currentScale.group_id = intent.getLongExtra("group_id", -1);

		// Load the note from the DB
		if(currentScale._id > 0) {
			currentScale.load();
		}

		if(currentScale.group_id < 0) {
			this.finish();
		}

		dbAdapter.close();

        this.setContentView(R.layout.scale_activity);
        this.findViewById(R.id.cancelButton).setOnClickListener(this);
		this.findViewById(R.id.saveButton).setOnClickListener(this);
		this.findViewById(R.id.deleteButton).setOnClickListener(this);


		// Set the label text
		((TextView)this.findViewById(R.id.maxLabel)).setText(currentScale.max_label);
		((TextView)this.findViewById(R.id.minLabel)).setText(currentScale.min_label);

		// This is a new note, remove the delete button.
		if(currentScale._id <= 0) {
			((ViewGroup)this.findViewById(R.id.deleteButton).getParent()).removeView(
					this.findViewById(R.id.deleteButton)
			);
		}

		// Focus on the text box will result in the keyboard appearing.
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(this.findViewById(R.id.maxLabel), 0);
		((TextView)this.findViewById(R.id.maxLabel)).requestFocus();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			// Start the delete intent
			case R.id.deleteButton:
				Intent i = new Intent(this, DeleteScaleActivity.class);
				i.putExtra("scale_id", currentScale._id);
				this.startActivity(i);
				this.finish();
				break;

			// exit this activity
			case R.id.cancelButton:
				this.setResult(Activity.RESULT_CANCELED);
				this.finish();
				break;

			// save the note and exit this activity
			case R.id.saveButton:
				dbAdapter.open();
				currentScale.max_label = ((TextView)this.findViewById(R.id.maxLabel)).getText().toString().trim();
				currentScale.min_label = ((TextView)this.findViewById(R.id.minLabel)).getText().toString().trim();
				currentScale.save();
				Log.v(TAG, "save scale:"+currentScale._id);
				dbAdapter.close();

				toastPopup.show();

				this.getIntent().putExtra("scale_id", currentScale._id);
				this.setResult(Activity.RESULT_OK, this.getIntent());
				this.finish();
				break;
		}
	}
}

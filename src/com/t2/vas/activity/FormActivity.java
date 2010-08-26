package com.t2.vas.activity;

import java.util.Calendar;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ScaleAdapter;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class FormActivity extends ABSActivity implements OnClickListener, OnLongClickListener, OnScrollListener {
	private static final String TAG = FormActivity.class.getName();
	private long activeGroupId = 1;
	private ScaleAdapter scaleAdapter;
	private String submitButtonText;
	private ListView listView;
	private Button submitButton;
	private boolean showSkipButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.setContentView(R.layout.form_activity);
        VASAnalytics.onEvent(VASAnalytics.EVENT_FORM_ACTIVITY);
        
        Intent intent = this.getIntent();
        this.activeGroupId = intent.getLongExtra("group_id", -1);
        this.submitButtonText = intent.getStringExtra("submit_button_text");
        this.showSkipButton = intent.getBooleanExtra("show_skip_button", false);

        if(this.activeGroupId < 0) {
        	this.finish();
        }



        listView = (ListView)this.findViewById(R.id.list);

        DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();

        Group group = ((Group)dbHelper.getTable("group")).newInstance();
        group._id = this.activeGroupId;
        if(!group.load()) {
        	this.finish();
        	return;
        }

        ((TextView)this.findViewById(R.id.categoryTitle)).setText(group.title);

        scaleAdapter = new ScaleAdapter(this, R.layout.slider_overlay_widget, group.getScales());
        listView.addFooterView(
        		View.inflate(this, R.layout.form_activity_submit_button, null)
		);
        listView.setAdapter(scaleAdapter);
        listView.setOnScrollListener(this);


        submitButton = (Button)this.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        submitButton.setOnLongClickListener(this);
        if(this.submitButtonText != null) {
        	submitButton.setText(this.submitButtonText);
        }

        Button skipButton = (Button)this.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(this);
        if(this.showSkipButton) {
        	skipButton.setVisibility(View.VISIBLE);
        }

        dbHelper.close();


        // Restore some of the data
        if(savedInstanceState != null) {
        	// Restore the positions of the scales
	        int[] scaleValues = savedInstanceState.getIntArray("scaleValues");
	        if(scaleValues != null) {
	        	for(int i = 0; i < scaleValues.length; i++) {
	        		this.scaleAdapter.setProgressValueAt(i, scaleValues[i]);
	        	}
	        }

	        // Remember the scroll position
	        int listFirstVisible = savedInstanceState.getInt("listFirstVisible");
        	listView.setSelection(listFirstVisible);
        }
    }


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Remember the values of the scales.
		int[] scaleValues = new int[this.scaleAdapter.getCount()];
		for(int i = 0; i < this.scaleAdapter.getCount(); i++) {
        	scaleValues[i] = this.scaleAdapter.getProgressValuesAt(i);
		}
		outState.putIntArray("scaleValues", scaleValues);

		// Remember the list's scroll position.
		outState.putInt("listFirstVisible", listView.getFirstVisiblePosition());

		super.onSaveInstanceState(outState);
	}




	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.skipButton:
				this.setResult(Activity.RESULT_CANCELED);
				this.finish();
				return;

			case R.id.submitButton:
				DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
		        dbHelper.open();

		        long currentTime = Calendar.getInstance().getTimeInMillis();
		        for(int i = 0; i < this.scaleAdapter.getCount(); i++) {
		        	Scale s = this.scaleAdapter.getItem(i);
		        	int value = this.scaleAdapter.getProgressValuesAt(i);

		        	Result r = (Result)dbHelper.getTable("result").newInstance();

					r.group_id = this.activeGroupId;
					r.scale_id = s._id;
					r.timestamp = currentTime;
					r.value = value;

					r.save();
		        }

				dbHelper.close();

				this.setResult(Activity.RESULT_OK);
				this.finish();
				return;
		}

		super.onClick(v);
	}

	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
		case R.id.submitButton:
			this.setResult(Activity.RESULT_OK);
			this.finish();
			return true;
		}

		return false;
	}


	@Override
	public int getHelpResId() {
		return R.string.form_help;
	}


	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int lastVisiblePos = firstVisibleItem + visibleItemCount;

		if(lastVisiblePos >= totalItemCount && this.submitButton != null) {
			this.submitButton.setEnabled(true);
		}
	}


	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

}
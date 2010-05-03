package com.t2.vas.activity;

import java.util.Calendar;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ScaleAdapter;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FormActivity extends BaseActivity implements OnClickListener, OnLongClickListener {
	private static final String TAG = FormActivity.class.getName();
	private long activeGroupId = 1;
	private ScaleAdapter scaleAdapter;
	private String submitButtonText;
	private ListView listView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        Intent intent = this.getIntent();
        this.activeGroupId = intent.getLongExtra("group_id", -1);
        this.submitButtonText = intent.getStringExtra("submit_button_text");
        
        if(this.activeGroupId < 0) {
        	this.finish();
        }
        
        
        LayoutInflater li = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup mainView = (ViewGroup)li.inflate(R.layout.form_activity, null);
        listView = (ListView)mainView.findViewById(R.id.list);
        
        DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();
        
        Group group = ((Group)dbHelper.getTable("group")).newInstance();
        group._id = this.activeGroupId;
        if(!group.load()) {
        	this.finish();
        	return;
        }
        
        scaleAdapter = new ScaleAdapter(this, R.layout.slider_overlay_widget, group.getScales());
        listView.addFooterView(li.inflate(R.layout.form_activity_submit_button, null));
        listView.setAdapter(scaleAdapter);
        
        
        ((TextView)mainView.findViewById(R.id.title)).setText(group.title);
        ((Button)mainView.findViewById(R.id.submitButton)).setOnClickListener(this);
        ((Button)mainView.findViewById(R.id.submitButton)).setOnLongClickListener(this);
        if(this.submitButtonText != null) {
        	((Button)mainView.findViewById(R.id.submitButton)).setText(this.submitButtonText);
        }
        
        
        dbHelper.close();
        setContentView(mainView);
        
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
		case R.id.submitButton:
			DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
	        dbHelper.open();
			
	        for(int i = 0; i < this.scaleAdapter.getCount(); i++) {
	        	//Log.v(TAG, "ADD RESULT");
	        	Scale s = this.scaleAdapter.getItem(i);
	        	int value = this.scaleAdapter.getProgressValuesAt(i);
	        	
	        	Result r = (Result)dbHelper.getTable("result").newInstance();
				
				r.group_id = this.activeGroupId;
				r.scale_id = s._id;
				r.timestamp = Calendar.getInstance().getTimeInMillis();
				r.value = value;
				
				r.save();
	        }
			
			dbHelper.close();
			
			this.setResult(Activity.RESULT_OK);
			this.finish();
			break;
		}
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
}
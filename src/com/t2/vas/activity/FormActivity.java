package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.ScaleAdapter;
import com.t2.vas.Global.Database;
import com.t2.vas.R.id;
import com.t2.vas.R.layout;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.VASDBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Result;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.SliderWidget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

public class FormActivity extends BaseActivity implements OnClickListener, OnLongClickListener {
	private static final String TAG = FormActivity.class.getName();
	private long activeGroupId = 1;
	private ScaleAdapter scaleAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater li = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup mainView = (ViewGroup)li.inflate(R.layout.form_activity, null);
        ListView listView = (ListView)mainView.findViewById(R.id.list);
        
        DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();
        
        Group group = (Group)dbHelper.getTable("group").newInstance();
        group._id = this.activeGroupId;
        group.load();
        
        scaleAdapter = new ScaleAdapter(this, R.layout.slider_overlay_widget, group.getScales());
        listView.setAdapter(scaleAdapter);
        
        ((TextView)mainView.findViewById(R.id.title)).setText(group.title);
        ((Button)mainView.findViewById(R.id.submitButton)).setOnClickListener(this);
        ((Button)mainView.findViewById(R.id.submitButton)).setOnLongClickListener(this);

        dbHelper.close();
        setContentView(mainView);
    }

	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch(v.getId()) {
		case R.id.submitButton:
			DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
	        dbHelper.open();
			
	        for(int i = 0; i < this.scaleAdapter.getCount(); i++) {
	        	Log.v(TAG, "ADD RESULT");
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
			
			intent = new Intent(this, ResultsActivity.class);
			intent.putExtra("group_id", this.activeGroupId);
			this.startActivity(intent);
			break;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Intent intent;
		
		switch(v.getId()) {
		case R.id.submitButton:
			intent = new Intent(this, ResultsActivity.class);
			intent.putExtra("group_id", this.activeGroupId);
			this.startActivity(intent);
			
			return true;
		}
		
		return false;
	}
}
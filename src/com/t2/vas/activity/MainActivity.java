package com.t2.vas.activity;

import java.util.ArrayList;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends BaseActivity implements OnClickListener, OnItemSelectedListener {
	private static final String TAG = MainActivity.class.getName();
	
	private ArrayAdapter<String> adapter;
	private ArrayList<Group> groupList;
	private Group currentGroup;
	private DBAdapter dbHelper;
	private Toast needScalesToast;

	private ArrayList<String> groupListString = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.main_activity);
        
        this.findViewById(R.id.formActivityButton).setOnClickListener(this);
        this.findViewById(R.id.resultsActivityButton).setOnClickListener(this);
        this.findViewById(R.id.notesActivityButton).setOnClickListener(this);
        
        needScalesToast = Toast.makeText(this, R.string.add_group_scales, 3000);
        dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();
        
        currentGroup = ((Group)dbHelper.getTable("group")).newInstance();
        groupList = currentGroup.getGroups();
        
        initAdapterData();
        
        adapter = new ArrayAdapter<String>(
        		this, 
        		android.R.layout.simple_spinner_item, 
        		groupListString
		);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        // Init the spinner.
        Spinner spinner = (Spinner)this.findViewById(R.id.groupSelector);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        
        dbHelper.close();
        
        this.findViewById(R.id.formActivityButton).setEnabled(false);
		this.findViewById(R.id.resultsActivityButton).setEnabled(false);
		this.findViewById(R.id.notesActivityButton).setEnabled(false);
        
        if(groupList.size() > 0) {
        	selectGroup(groupList.get(0));
        }
	}
	
	private void initAdapterData() {
		groupList = currentGroup.getGroups();
		
		// Convert the group list to an array of strings.
        groupListString.clear();
        for(int i = 0; i < groupList.size(); i++) {
        	groupListString.add(groupList.get(i).title);
        }
		
		if(adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}
	
	private void selectGroup(Group g) {
		this.currentGroup = g;
		this.dbHelper.open();
		
		ArrayList<Scale> scales = this.currentGroup.getScales();
		if(scales.size() <= 0) {
			this.findViewById(R.id.formActivityButton).setEnabled(false);
			this.findViewById(R.id.resultsActivityButton).setEnabled(false);
			this.findViewById(R.id.notesActivityButton).setEnabled(false);
			needScalesToast.show();
		} else {
			this.findViewById(R.id.formActivityButton).setEnabled(true);
			this.findViewById(R.id.resultsActivityButton).setEnabled(true);
			this.findViewById(R.id.notesActivityButton).setEnabled(true);
		}
		
		this.dbHelper.close();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case GROUP_EDITOR:
				Log.v(TAG, "RESULT");
				initAdapterData();
				break;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch(v.getId()) {
			case R.id.formActivityButton:
				intent = new Intent(this, FormActivity.class);
				intent.putExtra("group_id", this.currentGroup._id);
				this.startActivity(intent);
				break;
				
			case R.id.resultsActivityButton:
				intent = new Intent(this, ResultsActivity.class);
				intent.putExtra("group_id", this.currentGroup._id);
				this.startActivity(intent);
				break;
				
			case R.id.notesActivityButton:
				intent = new Intent(this, NotesActivity.class);
				intent.putExtra("group_id", this.currentGroup._id);
				this.startActivity(intent);
				break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		selectGroup(this.groupList.get(arg2));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
}

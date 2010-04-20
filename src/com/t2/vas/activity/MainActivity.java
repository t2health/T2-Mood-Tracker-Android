package com.t2.vas.activity;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivity extends BaseActivity implements OnClickListener {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        this.setContentView(R.layout.main_activity);
        
        this.findViewById(R.id.formActivityButton).setOnClickListener(this);
        this.findViewById(R.id.resultsActivityButton).setOnClickListener(this);
        this.findViewById(R.id.notesActivityButton).setOnClickListener(this);
        
        DBAdapter dbHelper = new DBAdapter(this, Global.Database.name, Global.Database.version);
        dbHelper.open();
        
        Group group = ((Group)dbHelper.getTable("group")).newInstance();
        Cursor cursor = group.select(null);
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
        		this,
        		R.layout.spinner_item,
        		cursor,
        		new String[] {
        			"title"	
        		},
        		new int[] {
        			R.id.text
        		}
		);
        
        Spinner spinner = (Spinner)this.findViewById(R.id.groupSelector);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Test");
        
        dbHelper.close();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		
		switch(v.getId()) {
			case R.id.formActivityButton:
				intent = new Intent(this, FormActivity.class);
				intent.putExtra("group_id", (long)1);
				this.startActivity(intent);
				break;
				
			case R.id.resultsActivityButton:
				intent = new Intent(this, ResultsActivity.class);
				intent.putExtra("group_id", (long)1);
				this.startActivity(intent);
				break;
				
			case R.id.notesActivityButton:
				intent = new Intent(this, NotesActivity.class);
				intent.putExtra("group_id", (long)1);
				this.startActivity(intent);
				break;
		}
	}
}

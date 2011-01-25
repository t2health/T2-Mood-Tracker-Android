package com.t2.vas.activity.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.t2.vas.R;
import com.t2.vas.activity.ABSNavigation;
import com.t2.vas.db.tables.Group;

public class ClearData extends ABSNavigation implements OnItemClickListener {
	private static final String TAG = ClearData.class.getSimpleName();
	private ListView list;
	private Button clearButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.clear_data_activity);
		clearButton = (Button)this.findViewById(R.id.clearButton);
		clearButton.setOnClickListener(this);
		
		Group g = new Group(dbAdapter);
		Cursor cursor = g.getGroupsCursor();
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				android.R.layout.simple_list_item_checked, 
				cursor, 
				new String[] {
						"title",
				}, 
				new int[] {
						android.R.id.text1,
				}
		);
		adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				return false;
			}
		});
		
		list = (ListView)this.findViewById(R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setOnItemClickListener(this);
		list.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.clearButton:
				onClearButtonPressed();
				return;
		}
		super.onClick(v);
	}
	
	private void clearData() {
		SparseBooleanArray positions = list.getCheckedItemPositions();
		for(int i = 0; i < positions.size(); ++i) {
			int pos = positions.keyAt(i);
			
			// If it is checked.
			if(positions.get(pos)) {
				long id = list.getItemIdAtPosition(pos);
				Group g = new Group(dbAdapter);
				g._id = id;
				Log.v(TAG, "Clear:"+id);
				g.clearResults();
			}
		}
		
		Toast.makeText(this, R.string.clear_data_cleared_text, Toast.LENGTH_LONG).show();
	}
	
	private void onClearButtonPressed() {
		new AlertDialog.Builder(this)
			.setMessage(R.string.clear_data_confirm)
			.setPositiveButton(R.string.yes, new Dialog.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					clearData();
				}	
			})
			.setNegativeButton(R.string.no, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.setCancelable(true)
			.create()
			.show();
	}

	private boolean isCategoryChecked() {
		SparseBooleanArray positions = list.getCheckedItemPositions();
		for(int i = 0; i < positions.size(); ++i) {
			int pos = positions.keyAt(0);
			
			// If it is checked.
			if(positions.get(pos)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		clearButton.setEnabled(isCategoryChecked());
	}
}

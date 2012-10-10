/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.view.SeparatedListAdapter;

public abstract class ABSImportExportActivity extends ABSActivity implements OnClickListener, OnItemClickListener {
	private Button finishButton;
	private DatePickerDialog fromDatePicker;
	private DatePickerDialog toDatePicker;

	private long fromTime = 0;
	private long toTime = 0;
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(Global.SHARE_TIME_FORMAT);
	private Button fromTimeButton;
	private Button toTimeButton;
	private SimpleAdapter groupsAdapter;
	private SimpleAdapter otherItemsAdapter;
	private SeparatedListAdapter listAdapter;
	private ListView list;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.export_sdcard_not_mounted, Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		
		File exportDir = Global.EXPORT_DIR;
		if(!exportDir.exists() && !exportDir.mkdirs()) {
			this.finish();
			return;
		}
		
		// Build the progress dialog.
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(this.getProgressMessage());
		progressDialog.setCancelable(false);
		
		// Set the content view
		this.setContentView(R.layout.abs_import_export_activity);
		
		// Setup the buttons.
		fromTimeButton = (Button)this.findViewById(R.id.fromDate);
		fromTimeButton.setOnClickListener(this);
		
		toTimeButton = (Button)this.findViewById(R.id.toDate);
		toTimeButton.setOnClickListener(this);
		
		finishButton = (Button)this.findViewById(R.id.finishButton);
		finishButton.setOnClickListener(this);
		finishButton.setText(this.getFinishButtonText());
		
		// Setup group items.
		groupsAdapter = this.getGroupsAdapter();
		
		// Setup the other clear items.
		otherItemsAdapter = this.getOtherItemsAdapter();
		
		// init the list adapter
		/*listAdapter = new SeparatedListAdapter(this);
		if(groupsAdapter != null) {
			listAdapter.addSection(getString(R.string.export_groups_header), groupsAdapter);
		}
		if(otherItemsAdapter != null) {
			listAdapter.addSection(getString(R.string.export_other_header), otherItemsAdapter);
		}*/
		
		list = (ListView)this.findViewById(R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setOnItemClickListener(this);
		//list.setAdapter(listAdapter);
		initListAdapter();
		
		
		// Build the from date dialog.
		Calendar cal = Calendar.getInstance();
		fromDatePicker = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						onFromDateSet(year, monthOfYear, dayOfMonth);
					}
				},
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
		);
		this.updateFromDate(cal.getTimeInMillis());
		
		// Build the to date dialog.
		toDatePicker = new DatePickerDialog(
				this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						onToDateSet(year, monthOfYear, dayOfMonth);
					}
				},
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
		);
		this.updateToDate(cal.getTimeInMillis());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.fromDate:
				fromDatePicker.show();
				return;
				
			case R.id.toDate:
				toDatePicker.show();
				return;
				
			case R.id.finishButton:
				onFinishButtonPressed();
				return;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		setFinishButtonEnabled();
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<HashMap<String,Object>> getSelectedGroupsItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(!isChecked) {
				continue;
			}
			
			Adapter adapter = listAdapter.getAdapterForItem(position);
			if(adapter == groupsAdapter) {
				items.add((HashMap<String, Object>) listAdapter.getItem(position));
			}
		}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<HashMap<String,Object>> getSelectedOtherItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(!isChecked) {
				continue;
			}
			
			Adapter adapter = listAdapter.getAdapterForItem(position);
			if(adapter == otherItemsAdapter) {
				items.add((HashMap<String, Object>) listAdapter.getItem(position));
			}
		}
		return items;
	}
	
	protected void updateFromDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		updateFromDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
		);
	}
	
	protected void updateToDate(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		updateToDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH)
		);
	}
	
	protected void updateFromDate(int year, int monthOfYear, int dayOfMonth) {
		this.fromDatePicker.updateDate(year, monthOfYear, dayOfMonth);
		onFromDateSet(year, monthOfYear, dayOfMonth);
	}
	
	protected void updateToDate(int year, int monthOfYear, int dayOfMonth) {
		this.toDatePicker.updateDate(year, monthOfYear, dayOfMonth);
		onToDateSet(year, monthOfYear, dayOfMonth);
	}
	
	private void onFromDateSet(int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		setFromTime(cal.getTimeInMillis());
		
		if(toTime < fromTime) {
			cal.setTimeInMillis(fromTime);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			onToDateSet(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)
			);
		}
		
		setFinishButtonEnabled();
	}
	
	private void onToDateSet(int year, int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, monthOfYear, dayOfMonth);
		setToTime(cal.getTimeInMillis());
		
		if(fromTime > toTime) {
			cal.setTimeInMillis(toTime);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			
			onFromDateSet(
					cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)
			);
		}
		
		setFinishButtonEnabled();
	}
	
	private void setFromTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		fromTime = cal.getTimeInMillis();
		
		fromTimeButton.setText(
				dateFormatter.format(cal.getTime())
		);
	}
	
	private void setToTime(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		this.toTime = cal.getTimeInMillis();
		
		toTimeButton.setText(
				dateFormatter.format(cal.getTime())
		);
	}
	
	private void setFinishButtonEnabled() {
		finishButton.setEnabled(isFormDataValid());
	}
	
	private boolean isFormDataValid() {
		SparseBooleanArray checkedPositions = list.getCheckedItemPositions();
		boolean isListItemChecked = false;
		for(int i = 0; i < checkedPositions.size(); ++i) {
			int position = checkedPositions.keyAt(i);
			boolean isChecked = checkedPositions.get(position);
			if(isChecked) {
				isListItemChecked = true;
				break;
			}
		}
		
		boolean isTimeCorrect = false;
		if(fromTime < toTime) {
			isTimeCorrect = true;
		}
		
		return isListItemChecked && isTimeCorrect;
	}
	
	protected abstract String getProgressMessage();
	protected abstract String getFinishButtonText();
	protected abstract SimpleAdapter getGroupsAdapter();
	protected abstract SimpleAdapter getOtherItemsAdapter();
	protected abstract void onFinishButtonPressed();
	
	protected ArrayList<HashMap<String,Object>> getOtherItems() {
		ArrayList<HashMap<String,Object>> otherDataItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item = new HashMap<String,Object>();
		item.put("id", "notes");
		item.put("title", getString(R.string.clear_data_notes));
		otherDataItems.add(item);
		return otherDataItems;
	}
	
	protected void notifyDataSetChanged() {
		initListAdapter();
		listAdapter.notifyDataSetChanged();
	}
	
	private void initListAdapter() {
		listAdapter = new SeparatedListAdapter(this);
		if(groupsAdapter != null && groupsAdapter.getCount() > 0) {
			listAdapter.addSection(getString(R.string.export_groups_header), groupsAdapter);
		}
		if(otherItemsAdapter != null && otherItemsAdapter.getCount() > 0) {
			listAdapter.addSection(getString(R.string.export_other_header), otherItemsAdapter);
		}
		this.list.setAdapter(listAdapter);
	}
	
	protected long getFromTime() {
		return this.fromTime;
	}
	
	protected long getToTime() {
		return this.toTime;
	}
	
	protected void showProgressDialog() {
		this.progressDialog.show();
	}
	
	protected void hideProgressDialog() {
		this.progressDialog.hide();
	}
}

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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.t2.vas.R;

public class BackupFileListActivity extends ABSNavigationActivity implements OnItemClickListener, OnItemLongClickListener {
	public static final String EXTRA_BASE_DIR = "baseDir";
	public static final String EXTRA_SELECTED_FILE = "selectedFile";

	private static final int FILE_SELECTOR_ACTIVITY = 345;
	private File selectedFile;
	private SimpleAdapter fileListAdapter;
	private File srcDir;
	ArrayList<HashMap<String,Object>> fileListItems = new ArrayList<HashMap<String,Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.list_layout);

		ListView listView = (ListView)this.findViewById(R.id.list);
		listView.setEmptyView(this.findViewById(R.id.emptyListTextView));

		((TextView)this.findViewById(R.id.emptyListTextView)).setText(R.string.no_csv_files_found);

		srcDir = (File)getIntent().getSerializableExtra(EXTRA_BASE_DIR);
		if(srcDir == null || !srcDir.exists()) {
			this.finish();
			return;
		}

		buildFileListItems();
		fileListAdapter = new SimpleAdapter(
				this, 
				fileListItems, 
				R.layout.list_item_1, 
				new String[] {
						"title",
				}, 
				new int[] {
						R.id.text1,
				}
				);

		listView.setAdapter(fileListAdapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}

	private void buildFileListItems() {
		fileListItems.clear();

		File[] files = srcDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().toLowerCase().endsWith(".db"))
					return true;
				else
					return false;
			}
		});
		if(files == null || files.length == 0) {
			return;
		}

		HashMap<String,Object> item;
		File file;
		String fileName;
		for(int i = 0; i < files.length; ++i) {
			file = files[i];
			fileName = file.getName();
			if(file.isDirectory()) {
				fileName += "/";
			}

			item = new HashMap<String,Object>();
			item.put("title", fileName);
			item.put("file", file);

			fileListItems.add(item);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		@SuppressWarnings("unchecked")
		HashMap<String,Object> item = (HashMap<String,Object>)arg0.getItemAtPosition(arg2);
		File selectedFile = (File)item.get("file");

		if(selectedFile.isDirectory()) {
			Intent intent = new Intent(this, FileListActivity.class);
			intent.putExtra(EXTRA_BASE_DIR, selectedFile);
			intent.putExtra(FileListActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			this.startActivityForResult(intent, FILE_SELECTOR_ACTIVITY);

		} else {
			Intent intent = new Intent();
			intent.putExtra(EXTRA_SELECTED_FILE, selectedFile);
			this.setResult(RESULT_OK, intent);
			this.finish();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		@SuppressWarnings("unchecked")
		HashMap<String,Object> item = (HashMap<String,Object>)arg0.getItemAtPosition(arg2);
		File selectedFile = (File)item.get("file");

		if(selectedFile.isFile()) {
			this.selectedFile = selectedFile;
			showFileOptionsDialog();
		}

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_OK) {
			this.setResult(RESULT_OK, data);
			this.finish();
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showFileOptionsDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.file_options_title)
		.setNegativeButton(R.string.close, null)
		.setItems(new String[]{
				getString(R.string.delete_selected_file)
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:
					showFileDeleteDialog();
					break;
				}
				dialog.dismiss();
			}
		}
				)
				.create()
				.show();
	}

	private void showFileDeleteDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.delete_file_title)
		.setMessage(R.string.delete_file_desc)
		.setPositiveButton(R.string.delete_file_yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectedFile.delete();
				dialog.dismiss();

				buildFileListItems();
				fileListAdapter.notifyDataSetChanged();
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.create()
		.show();
	}
}

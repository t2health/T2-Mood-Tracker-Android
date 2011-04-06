package com.t2.vas.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.importexport.ImportExport;
import com.t2.vas.importexport.ImportFileStats;

public class ImportActivity extends ABSImportExportActivity {
	private static final int FILE_LIST_ACTIVITY = 24522;
	private static final int IMPORT_COMPLETE = 235;
	private static final int PRE_IMPORT_COMPLETE = 23049;
	private static final int PRE_IMPORT_FAILED = 23253;
	
	private ArrayList<HashMap<String,Object>> groupItems = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String,Object>> otherItems = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter groupsAdapter;
	private SimpleAdapter otherAdapter;
	private File importFile;
	private ImportFileStats importFileStats;
	
	private Handler importHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case PRE_IMPORT_COMPLETE:
				onPreImportComplete();
				break;
				
			case PRE_IMPORT_FAILED:
				onPreImportFailed();
				break;
				
			case IMPORT_COMPLETE:
				onImportComplete();
				break;
			}
			
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startFileListActivity(Global.EXPORT_DIR);
	}
	
	private void startFileListActivity(File baseDir) {
		Intent intent = new Intent(this, FileListActivity.class);
		intent.putExtra(FileListActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		if(baseDir != null) {
			intent.putExtra(FileListActivity.EXTRA_BASE_DIR, baseDir);
		}
		this.startActivityForResult(intent, FILE_LIST_ACTIVITY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == FILE_LIST_ACTIVITY) {
			if(resultCode == RESULT_OK && data != null) {
				File selectedFile = (File)data.getSerializableExtra(FileListActivity.EXTRA_SELECTED_FILE);
				if(selectedFile == null) {
					this.finish();
					return;
				}
				
				this.importFile = selectedFile;
				startPreImport();
				
			} else {
				this.finish();
				return;
			}
		}
	}
	
	protected void startPreImport() {
		showProgressDialog();
		new Thread(new Runnable(){
			@Override
			public void run() {
				ImportFileStats stats = ImportExport.getFileStats(importFile);
				
				if(stats == null || (stats.groups.size() <= 0 && stats.notesCount <= 0)) {
					importHandler.sendEmptyMessage(PRE_IMPORT_FAILED);
					
				} else {
					importFileStats = stats;
					importHandler.sendEmptyMessage(PRE_IMPORT_COMPLETE);
				}
				
				hideProgressDialog();
			}
		}).start();
	}
	
	private void onPreImportFailed() {
		hideProgressDialog();
		Toast.makeText(this, R.string.import_not_valid_file, Toast.LENGTH_LONG).show();
		startFileListActivity(importFile.getParentFile());
	}
	
	private void onPreImportComplete() {
		hideProgressDialog();
		setupListItems(importFileStats);
	}
	
	private void setupListItems(ImportFileStats stats) {
		groupItems.clear();
		otherItems.clear();
		
		if(stats == null) {
			this.notifyDataSetChanged();
			return;
		}
		
		if(stats.notesCount > 0) {
			otherItems.addAll(this.getOtherItems());
		}
		
		long minTimestamp = stats.minNoteTimestamp;
		long maxTimestamp = stats.maxNoteTimestamp;
		
		if(stats.groups.size() > 0) {
			HashMap<String,Object> item;
			for(int i = 0; i < stats.groups.size(); ++i) {
				item = new HashMap<String,Object>();
				item.put("title", stats.groups.get(i).title);
				groupItems.add(item);
			}
			
			long minResultTimestamp = stats.getMinResultTimestamp();
			long maxResultTimestamp = stats.getMaxResultTimestamp();
			
			if(minResultTimestamp < minTimestamp) {
				minTimestamp = minResultTimestamp;
			}
			
			if(maxResultTimestamp < maxTimestamp) {
				maxTimestamp = maxResultTimestamp;
			}
		}
		
		this.updateFromDate(minTimestamp);
		this.updateToDate(maxTimestamp);
		
		this.notifyDataSetChanged();
	}

	@Override
	protected SimpleAdapter getGroupsAdapter() {
		this.groupsAdapter = new SimpleAdapter(
				this,
				groupItems,
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
						"title",
						"desc",
				},
				new int[] {
						android.R.id.text1,
						android.R.id.text2,
				}
		);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		this.groupsAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == android.R.id.text1) {
					((TextView)view).setText(data.toString());
					return true;
				}
				
				return false;
			}
		});
		return this.groupsAdapter;
	}

	@Override
	protected SimpleAdapter getOtherItemsAdapter() {
		this.otherAdapter = new SimpleAdapter(
				this,
				otherItems,
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
						"title",
				},
				new int[] {
						android.R.id.text1,
				}
		);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		this.otherAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if(view.getId() == android.R.id.text1) {
					((TextView)view).setText(data.toString());
					return true;
				}
				
				return false;
			}
		});
		return this.otherAdapter;
	}

	@Override
	protected void onFinishButtonPressed() {
		final ArrayList<String> importNames = new ArrayList<String>();
		ArrayList<HashMap<String,Object>> groupItems = this.getSelectedGroupsItems();
		HashMap<String,Object> item;
		for(int i = 0; i < groupItems.size(); ++i) {
			importNames.add(groupItems.get(i).get("title")+"");
		}
		
		boolean tmpImportNotes = false;
		ArrayList<HashMap<String,Object>> otherItems = this.getSelectedOtherItems();
		for(int i = 0; i < otherItems.size(); ++i) {
			if(otherItems.get(i).get("id").equals("notes")) {
				tmpImportNotes = true;
			}
		}
		final boolean importNotes = tmpImportNotes;
		
		showProgressDialog();
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				
				ImportExport.importData(
						importFile, 
						dbAdapter, 
						importFileStats, 
						importNames,
						importNotes,
						getFromTime(), 
						getToTime()
				);
				
				importHandler.sendEmptyMessage(IMPORT_COMPLETE);
			}
		}).start();
	}

	@Override
	protected String getFinishButtonText() {
		return getString(R.string.import_button);
	}
	
	private void onImportComplete() {
		hideProgressDialog();
		Toast.makeText(this, R.string.import_complete, Toast.LENGTH_LONG).show();
		this.finish();
	}

	@Override
	protected String getProgressMessage() {
		return getString(R.string.import_progress_message);
	}
}

/*
 * 
 */
package com.t2.vas.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.tables.Group;
import com.t2.vas.importexport.ImportExport;

public abstract class ABSExportActivity extends ABSImportExportActivity {
	private static final String TAG = ABSExportActivity.class.getSimpleName();
	private static final int EXPORT_SUCCESS = 1;
	private static final int EXPORT_FAILED = 0;
	
	private ArrayList<Uri> exportFileUris = new ArrayList<Uri>();
	private Handler fileExportCompleteHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// Hide the progress dialog.
			hideProgressDialog();
			
			if(msg.what == EXPORT_SUCCESS) {
				onDataExported(exportFileUris);
			} else if(msg.what == EXPORT_FAILED) {
				onDataExportFailed();
			}
		}
	};
	
	@Override
	protected SimpleAdapter getGroupsAdapter() {
		Group g = new Group(dbAdapter);
		Cursor cursor = g.getGroupsCursor();
		ArrayList<HashMap<String,Object>> groupsItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;
		while(cursor.moveToNext()) {
			item = new HashMap<String,Object>();
			item.put(Group.FIELD_TITLE, cursor.getString(cursor.getColumnIndex(Group.FIELD_TITLE)));
			item.put(Group.FIELD_ID, cursor.getLong(cursor.getColumnIndex(Group.FIELD_ID)));
			groupsItems.add(item);
		}
		cursor.close();
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				groupsItems,
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
						Group.FIELD_TITLE, 
				},
				new int[] {
						android.R.id.text1,
				}
		);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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
		return adapter;
	}
	
	@Override
	protected SimpleAdapter getOtherItemsAdapter() {
		SimpleAdapter otherItemsAdapter = new SimpleAdapter(
				this, 
				getOtherItems(), 
				android.R.layout.simple_list_item_multiple_choice,
				new String[] {
						"title", 
				},
				new int[] {
						android.R.id.text1,
				}
		);
		// Manually binding the title to the text, this resolves a bug in android 2.1 and below.
		otherItemsAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
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
		
		return otherItemsAdapter;
	}
	
	protected String getFinishButtonText() {
		return getString(R.string.export_button);
	}
	
	protected abstract void onDataExported(ArrayList<Uri> uris);
	protected abstract String getExportFilename(long fromTime, long toTime);
	//protected abstract String getNotesFilename(long fromTime, long toTime);
	
	protected void onDataExportFailed() {
		Toast.makeText(this, R.string.fail_to_export_files, Toast.LENGTH_LONG);
	}
	
	protected void onFinishButtonPressed() {
		shareData();
	}
	
	private void shareData() {
		final List<Long> groupIds = new ArrayList<Long>();
		final List<String> otherIds = new ArrayList<String>();
		
		ArrayList<HashMap<String,Object>> groupItems = this.getSelectedGroupsItems();
		for(int i = 0; i < groupItems.size(); ++i) {
			groupIds.add((Long)groupItems.get(i).get(Group.FIELD_ID));
		}
		
		ArrayList<HashMap<String,Object>> otherItems = this.getSelectedOtherItems();
		for(int i = 0; i < otherItems.size(); ++i) {
			otherIds.add(otherItems.get(i).get("id").toString());
		}
		
		
		// Start the progress dialog.
		showProgressDialog();
		exportFileUris.clear();
		
		// Run the export on a separate thread.
		new Thread(new Runnable() {
			@Override
			public void run() {
				long fromTime = getFromTime();
				long toTime = getToTime();
				
				File exportDir = Global.EXPORT_DIR;
				if(!exportDir.exists() && !exportDir.mkdirs()) {
					fileExportCompleteHandler.sendEmptyMessage(EXPORT_FAILED);
					return;
				}
				
				File outputFile = new File(exportDir, getExportFilename(fromTime, toTime));
				
				if(outputFile.exists()) {
					if(!outputFile.delete()) {
						fileExportCompleteHandler.sendEmptyMessage(EXPORT_FAILED);
						return;
					}
				}
				
				Log.v(TAG, "Export thread started");
				
				// Export the groups.
				boolean exportGroupIds = groupIds.size() > 0;
				boolean exportNotes = otherIds.contains("notes");
				if(exportGroupIds) {
					Log.v(TAG, "Exporting groups.");
					ImportExport.exportGroupData(
							dbAdapter, 
							outputFile, 
							true,
							groupIds, 
							fromTime, 
							toTime
					);
				}
				
				// Export the notes.
				if(exportNotes) {
					Log.v(TAG, "Exporting notes.");
					ImportExport.exportNotesData(
							dbAdapter, 
							outputFile,
							true,
							fromTime, 
							toTime
					);
				}
				
				if(exportGroupIds || exportNotes) {
					exportFileUris.add(Uri.fromFile(outputFile));
				}
				
				fileExportCompleteHandler.sendEmptyMessage(EXPORT_SUCCESS);
			}
		}).start();
	}
}

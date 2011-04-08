package com.t2.vas.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.t2.vas.R;

public class FileListActivity extends ABSNavigationActivity implements OnItemClickListener {
	public static final String EXTRA_BASE_DIR = "baseDir";
	public static final String EXTRA_SELECTED_FILE = "selectedFile";
	
	private static final int FILE_SELECTOR_ACTIVITY = 345;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.list_layout);
		
		ListView listView = (ListView)this.findViewById(R.id.list);
		listView.setEmptyView(this.findViewById(R.id.emptyListTextView));
		
		((TextView)this.findViewById(R.id.emptyListTextView)).setText(R.string.no_csv_files_found);
		
		File srcDir = (File)getIntent().getSerializableExtra(EXTRA_BASE_DIR);
		if(srcDir == null || !srcDir.exists()) {
			this.finish();
			return;
		}
		
		File[] files = srcDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".csv");
			}
		});
		if(files == null || files.length == 0) {
			return;
		}
		
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
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
			
			items.add(item);
		}
		
		listView.setAdapter(new SimpleAdapter(
				this, 
				items, 
				R.layout.list_item_1, 
				new String[] {
						"title",
				}, 
				new int[] {
						R.id.text1,
				}
		));
		listView.setOnItemClickListener(this);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == RESULT_OK) {
			this.setResult(RESULT_OK, data);
			this.finish();
			return;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
}

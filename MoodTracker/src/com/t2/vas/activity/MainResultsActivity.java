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
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.view.SeparatedListAdapter;

public class MainResultsActivity extends ABSNavigationActivity implements OnItemClickListener {
	private ListView listView;
	private SeparatedListAdapter listAdapter;

	private static final int FILE_LIST_ACTIVITY = 24522;
	private File importFile;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.list_layout);

		listAdapter = new SeparatedListAdapter(this);

		listAdapter.addSection(this.getString(R.string.results_section_title), new SimpleAdapter(
				this,
				this.getResultsItems(),
				R.layout.list_item_1_image,
				new String[] {
					"text1",
					"image1",
				},
				new int[] {
					R.id.text1,
					R.id.image1,
				}
				));
		listView = (ListView)this.findViewById(R.id.list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

	}

	private ArrayList<HashMap<String,Object>> getResultsItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.graph_results_title));
		item.put("image1", R.drawable.linechart);
		item.put("id", "graph_results");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.view_notes_title));
		item.put("image1", R.drawable.notes);
		item.put("id", "view_notes");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.saved_results_title));
		item.put("image1", R.drawable.share);
		item.put("id", "saved_results");
		items.add(item);


		/*item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.share_title));
		item.put("image1", R.drawable.share);
		item.put("id", "share");
		items.add(item);*/

		/*item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.import_title));
		item.put("image1", R.drawable.share);
		item.put("id", "import");
		items.add(item);*/

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.export_title));
		item.put("image1", R.drawable.share);
		item.put("id", "export");
		items.add(item);


		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		@SuppressWarnings("unchecked")
		HashMap<String,Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);

		String itemId = (String) data.get("id");

		if(itemId.equals("graph_results")) 
		{
			Intent i = new Intent(this, GroupResultsActivity.class);
			this.startActivityForResult(i, 123);
		} 
		if(itemId.equals("saved_results")) 
		{
			startFileListActivity(Global.EXPORT_DIR);
		} 
		else if(itemId.equals("view_notes")) 
		{
			Intent i = new Intent(this, NotesListActivity.class);
			this.startActivityForResult(i, 123);
		} 
		else if(itemId.equals("share")) 
		{
			Intent i = new Intent(this, ShareActivity.class);
			this.startActivityForResult(i, 123);
		} 
		else if(itemId.equals("import")) 
		{
			Intent i = new Intent(this, ImportActivity.class);
			this.startActivityForResult(i, 123);				
		} 
		else if(itemId.equals("export")) 
		{
			Intent i = new Intent(this, ExportActivity.class);
			this.startActivityForResult(i, 123);	
		}

	}
	
	private void startFileListActivity(File baseDir) {
		Intent intent = new Intent(this, FileListActivity.class);
		intent.putExtra(FileListActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		if(baseDir != null) {
			intent.putExtra(FileListActivity.EXTRA_BASE_DIR, baseDir);
		}
		this.startActivityForResult(intent, FILE_LIST_ACTIVITY);
	}

	private void handleFile()
	{

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Would you like to view the file now or send this file via email?")
		.setCancelable(true)
		.setPositiveButton("View", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				viewFile();
			}
		})
		.setNegativeButton("Email", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				emailFile();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	private void viewFile()
	{
		Uri uri = Uri.fromFile(importFile);
		Intent i = new Intent(Intent.ACTION_VIEW);
		if(importFile.getName().contains("pdf"))
			i.setDataAndType(uri, "application/pdf");
		else
			i.setDataAndType(uri, "text/plain");
			
		startActivity(i);
	}
	private void emailFile()
	{
		Uri uri = Uri.fromFile(importFile);
		Intent i = new Intent(Intent.ACTION_SEND);
		//i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "stephen.ody@tee2.org" });
		i.putExtra(Intent.EXTRA_SUBJECT, "My T2 Mood Tracker Results");
		i.putExtra(Intent.EXTRA_TEXT, "Report included as attachment.");
		i.putExtra(Intent.EXTRA_STREAM, uri);
		i.setType("text/plain");
		startActivity(Intent.createChooser(i, "Send mail"));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == FILE_LIST_ACTIVITY) {
			if(resultCode == RESULT_OK && data != null) {
				File selectedFile = (File)data.getSerializableExtra(FileListActivity.EXTRA_SELECTED_FILE);
				if(selectedFile != null) {
				
				this.importFile = selectedFile;
				handleFile();
				}
				
			}
		}
	}

}

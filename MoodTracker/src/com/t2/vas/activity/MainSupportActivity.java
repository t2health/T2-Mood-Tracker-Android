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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.t2.vas.Global;
import com.t2.vas.MarketPlatform;
import com.t2.vas.R;
import com.t2.vas.db.BackupRestore;
import com.t2.vas.view.SeparatedListAdapter;

public class MainSupportActivity extends ABSNavigationActivity implements OnItemClickListener {
	private static final int FILE_LIST_ACTIVITY = 24522;
	public static final int RATE_ACTIVITY = 345;
	public static final int NOTE_ACTIVITY = 355;

	private ListView listView;
	private SeparatedListAdapter listAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.list_layout);

		listAdapter = new SeparatedListAdapter(this);

			listAdapter.addSection(this.getString(R.string.support_section_title), new SimpleAdapter(
					this,
					this.getSupportItems(),
					R.layout.list_item_1_image,
					new String[] {
						"text1",
					},
					new int[] {
						R.id.text1,
					}
					));
		listView = (ListView)this.findViewById(R.id.list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

	}

	private ArrayList<HashMap<String,Object>> getSupportItems() {
		ArrayList<HashMap<String,Object>> items = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item;

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.about_title));
		item.put("id", "about");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.resources_title));
		item.put("id", "resources");
		items.add(item);
		
		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.feedback_title));
		item.put("id", "feedback");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.help_title));
		item.put("id", "help");
		items.add(item);

		item = new HashMap<String,Object>();
		if(MarketPlatform.isGoogleMarket(this))
			item.put("text1", this.getString(R.string.rate_app_google));
		else
			item.put("text1", this.getString(R.string.rate_app_amazon));
		item.put("id", "rate_app");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.tell_a_friend_title));
		item.put("id", "tell_a_friend");
		items.add(item);

//		item = new HashMap<String,Object>();
//		item.put("text1", "");
//		item.put("id", " ");
//		items.add(item);
		
		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.backup_database));
		item.put("id", "backup");
		items.add(item);

		item = new HashMap<String,Object>();
		item.put("text1", this.getString(R.string.restore_database));
		item.put("id", "restore");
		items.add(item);

		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		@SuppressWarnings("unchecked")
		HashMap<String,Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
		//Adapter adapter = listAdapter.getAdapterForItem(arg2);

		{
			String itemId = (String) data.get("id");

			if(itemId.equals("about")) 
			{
				Intent i = new Intent(this, WebViewActivity.class);
				//i.putExtra(WebViewActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				i.putExtra(WebViewActivity.EXTRA_TITLE_ID, R.string.about_title);
				i.putExtra(WebViewActivity.EXTRA_CONTENT_ID, R.string.about_text);
				this.startActivityForResult(i, 123);
			} 
			else if(itemId.equals("resources")) 
			{
				Intent i = new Intent(this, WebViewActivity.class);
				//i.putExtra(WebViewActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				i.putExtra(WebViewActivity.EXTRA_TITLE_ID, R.string.resources_title);
				i.putExtra(WebViewActivity.EXTRA_CONTENT_ID, R.string.resources_text);
				this.startActivityForResult(i, 123);
			} 
			else if(itemId.equals("feedback")) 
			{
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("plain/text");
				i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.feedback_to)});
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
				i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.feedback_content)));
				this.startActivityForResult(Intent.createChooser(i, this.getString(R.string.feedback_title)), 123);

			} 
			else if(itemId.equals("help")) 
			{
				Intent i = new Intent(this, HelpActivity.class);
				//i.putExtra(HelpActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
				this.startActivityForResult(i, 123);

			} 
			else if(itemId.equals("tell_a_friend")) 
			{
				Intent i = new Intent(android.content.Intent.ACTION_SEND);
				i.setType("text/html");
				i.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.tell_a_friend_subject));
				if(MarketPlatform.isGoogleMarket(this))
					i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.tell_a_friend_content_google)));
				else
					i.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(getString(R.string.tell_a_friend_content_amazon)));
				this.startActivityForResult(Intent.createChooser(i, this.getString(R.string.tell_a_friend_title)), 123);

			} 
			else if(itemId.equals("rate_app")) 
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://details?id="+this.getPackageName()));
				this.startActivityForResult(i, 123);

			}
			else if(itemId.equals("backup")) 
			{
				BackupRestore.backupDb(this);
			}
			else if(itemId.equals("restore")) 
			{
				startBackupFileListActivity(Global.EXPORT_DIR);
				//BackupRestore.restoreDb(this);
			}
		}
	}
	
	private void startBackupFileListActivity(File baseDir) {
		Intent intent = new Intent(this, BackupFileListActivity.class);
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
					//this.finish();
					return;
				}
				
				BackupRestore.restoreDb(this, selectedFile);
				
			} else {
				//this.finish();
				return;
			}
		}
	}

}

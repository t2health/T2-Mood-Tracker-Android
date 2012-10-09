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
package com.t2.vas.activity.editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.t2.vas.R;
import com.t2.vas.ReminderService;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.db.tables.Group;

public class GroupListActivity extends ABSNavigationActivity implements OnItemClickListener, android.content.DialogInterface.OnClickListener {
	private SimpleCursorAdapter groupsAdapter;
	private ListView listView;
	private Cursor groupsCursor;

	private EditText addEditText;
	private AlertDialog addGroupDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addEditText = new EditText(this);
		addEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		addGroupDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.add_group_title)
			.setView(addEditText)
			.setPositiveButton(R.string.save, this)
			.setNegativeButton(R.string.cancel, this)
			.create();
		
		this.setContentView(R.layout.list_layout);
		this.setRightButtonText(getString(R.string.add));
		
		Group group = new Group(dbAdapter);
		groupsCursor = group.getGroupsCursor();
		groupsAdapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_1,
				groupsCursor,
				new String[] {
						"title",
						"_id",
				},
				new int[] {
						R.id.text1
				}
		);
		
		listView = (ListView)this.findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		listView.setAdapter(groupsAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		groupsCursor.requery();
		groupsAdapter.notifyDataSetChanged();
	}
	
	

	@Override
	protected void onBackButtonPressed() {
		// restart the reminder service to account for the possible changes in 
		// group visibility.
		ReminderService.stopRunning(this);
		ReminderService.startRunning(this);
		
		super.onBackButtonPressed();
	}

	@Override
	protected void onRightButtonPressed() {
		addEditText.setText("");
		addGroupDialog.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, GroupActivity.class);
		i.putExtra(GroupActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		i.putExtra(GroupActivity.EXTRA_GROUP_ID, arg3);
		this.startActivityForResult(i, 123);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog == addGroupDialog) {
			if(which == AlertDialog.BUTTON_POSITIVE) {
				Group g = new Group(dbAdapter);
				g.title = addEditText.getText().toString().trim().replace('\n', ' ');
				g.save();
				groupsCursor.requery();
				groupsAdapter.notifyDataSetChanged();
			}
		}
	}
}

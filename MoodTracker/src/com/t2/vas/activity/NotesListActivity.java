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

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.tables.Note;
import com.t2.vas.view.SimpleCursorDateSectionAdapter;

public class NotesListActivity extends ABSNavigationActivity implements OnItemClickListener {
	//private static final int NOTE_ACTIVITY = 97;
	
	private SimpleCursorDateSectionAdapter notesAdapter;
	private ListView notesListView;

	private Cursor notesCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.list_layout);
		
		notesCursor = new Note(dbAdapter).queryForNotes(-1, -1, "timestamp DESC");
		this.startManagingCursor(notesCursor);
		notesAdapter = SimpleCursorDateSectionAdapter.buildNotesAdapter(
				this, 
				notesCursor,
				new SimpleDateFormat(Global.NOTES_LONG_DATE_FORMAT),
				new SimpleDateFormat(Global.NOTES_SECTION_DATE_FORMAT),
				R.layout.list_item_2
		);
		
		notesListView = (ListView)this.findViewById(R.id.list);
		notesListView.setAdapter(notesAdapter);
		notesListView.setFastScrollEnabled(true);
		notesListView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		notesCursor.requery();
		notesAdapter.notifyDataSetChanged();
	}

//	@Override
//	public void onRightButtonPressed() {
//		Intent i = new Intent(this, AddEditNoteActivity.class);
//		i.putExtra(AddEditNoteActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
//		this.startActivityForResult(i, NOTE_ACTIVITY);
//	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, AddEditNoteActivity.class);
		i.putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, arg3);
		//i.putExtra(AddEditNoteActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		this.startActivityForResult(i, 123);
	}
}

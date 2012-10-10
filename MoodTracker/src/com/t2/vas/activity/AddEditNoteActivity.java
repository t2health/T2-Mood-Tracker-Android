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

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.t2.vas.Analytics;
import com.t2.vas.R;
import com.t2.vas.db.tables.Note;

public class AddEditNoteActivity extends ABSActivity implements OnClickListener, OnDateChangedListener {
	public static final String EXTRA_NOTE_ID = "noteId";
	public static final String EXTRA_TIMESTAMP = "timestamp";
	private Note currentNote;
	private DatePicker datePicker;
	private TimePicker timePicker;

	public static boolean okToQuit = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try
		{
		// init global variables.
		currentNote = new Note(this.dbAdapter);

		// init the local variables;
		Calendar cal = Calendar.getInstance();
		Intent intent = this.getIntent();
		long dateTimestamp = intent.getLongExtra(EXTRA_TIMESTAMP, cal.getTimeInMillis());

		currentNote._id = intent.getLongExtra(EXTRA_NOTE_ID, -1);

		// Load the note from the DB
		if(currentNote._id > 0) {
			currentNote.load();
			this.setTitle(getString(R.string.edit_note_title));

			// This is a new note, set the date.
		} else {
			this.setTitle(getString(R.string.add_note_title));
			cal.setTimeInMillis(dateTimestamp);
			currentNote.timestamp = cal.getTimeInMillis();
		}

		cal.setTimeInMillis(currentNote.timestamp);

		// Init the view
		this.setContentView(R.layout.add_edit_note_activity);
		//this.findViewById(R.id.cancelButton).setOnClickListener(this);
		//this.findViewById(R.id.saveButton).setOnClickListener(this);
		this.findViewById(R.id.deleteButton).setOnClickListener(this);
		this.findViewById(R.id.saveButton).setOnClickListener(this);
		//this.setRightButtonText(getString(R.string.save));

		// Set the date
		datePicker = (DatePicker)this.findViewById(R.id.date);
		datePicker.init(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH),
				this
				);

		// Set the time
		timePicker = (TimePicker)this.findViewById(R.id.time);
		timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));

		// Set the note text
		((TextView)this.findViewById(R.id.note)).setText(currentNote.note);

		// This is a new note, remove the delete button.
		if(currentNote._id <= 0) {
			this.findViewById(R.id.deleteButton).setVisibility(View.GONE);
			// This is an existing note, don't show the keyboard by default.
		} else {
			this.findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);

			// Hide the keyboard unless the user chooses a text view.
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
		}
		catch(Exception ex){}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		// Start the delete intent
		case R.id.deleteButton:
			new AlertDialog.Builder(this)
			.setMessage(R.string.note_delete_confirm)
			.setPositiveButton(R.string.yes, new Dialog.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					currentNote.delete();
					Analytics.onEvent(AddEditNoteActivity.this, "Note Deleted");
					finish();
				}
			})
			.setNegativeButton(R.string.no, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create()
			.show();
			break;
		case R.id.saveButton:
			saveButtonPressed();
			break;
		}
	}


	private void saveButtonPressed() {
		String noteText = ((TextView)this.findViewById(R.id.note)).getText().toString().trim().replace('\n', ' ');
		Calendar cal = Calendar.getInstance();

		if(noteText.length() <= 0) {
			Toast.makeText(this, R.string.provide_note_text_message, Toast.LENGTH_LONG).show();
			return;
		}

		cal.set(
				datePicker.getYear(),
				datePicker.getMonth(),
				datePicker.getDayOfMonth(),
				timePicker.getCurrentHour(),
				timePicker.getCurrentMinute()
				);

		currentNote.note = noteText;
		currentNote.timestamp = cal.getTimeInMillis();
		Analytics.onEvent(this, currentNote._id <= 0 ? "Note Added" : "Note Edited");
		currentNote.save();

		this.getIntent().putExtra(EXTRA_NOTE_ID, currentNote._id);
		this.setResult(Activity.RESULT_OK, this.getIntent());
		this.finish();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	}

	@Override
	public void onBackPressed() {

		if(!((TextView)this.findViewById(R.id.note)).getText().toString().trim().equals(""))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your note has not been saved. Are you sure you want to exit without saving?")
			.setCancelable(true)
			.setPositiveButton("Yes (Exit)", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					AddEditNoteActivity.super.onBackPressed();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					//AddEditNoteActivity.super.onBackPressed();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
		else
			super.onBackPressed();
	}
}

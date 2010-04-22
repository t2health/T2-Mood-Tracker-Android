package com.t2.vas.activity;

import java.util.Calendar;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;

public class NoteActivity extends BaseActivity implements OnClickListener, OnDateChangedListener {
	private DBAdapter dbAdapter;
	private Note currentNote;
	private Toast toastPopup;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// init global variables.
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		currentNote = ((Note)dbAdapter.getTable("note")).newInstance();
		toastPopup = Toast.makeText(this, R.string.activity_note_saved, 2000);
		
		// init the local variables;
		Calendar cal = Calendar.getInstance();
		Intent intent = this.getIntent();
		long dateTimestamp = intent.getLongExtra("timstamp", cal.getTimeInMillis());
		
		currentNote._id = intent.getLongExtra("noteId", -1);
		
		// Load the note from the DB
		if(currentNote._id > 0) {
			currentNote.load();
		
		// This is a new note, set the date.
		} else {
			cal.setTimeInMillis(dateTimestamp);
			currentNote.timestamp = cal.getTimeInMillis();
		}
		
		dbAdapter.close();
		cal.setTimeInMillis(currentNote.timestamp);
		
		// Init the view
		this.setContentView(R.layout.note_activity);
		this.findViewById(R.id.cancelButton).setOnClickListener(this);
		this.findViewById(R.id.saveButton).setOnClickListener(this);
		this.findViewById(R.id.deleteButton).setOnClickListener(this);
		
		// Set the date
		((DatePicker)this.findViewById(R.id.date)).init(
			cal.get(Calendar.YEAR), 
			cal.get(Calendar.MONTH), 
			cal.get(Calendar.DAY_OF_MONTH), 
			this
		);
		
		// Set the note text
		((TextView)this.findViewById(R.id.note)).setText(currentNote.note);
		
		// This is a new note, remove the delete button.
		if(currentNote._id <= 0) {
			((ViewGroup)this.findViewById(R.id.deleteButton).getParent()).removeView(
					this.findViewById(R.id.deleteButton)
			);
		}
		
		// Focus on the text box will result in the keyboard appearing.
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(this.findViewById(R.id.note), 0);
		((TextView)this.findViewById(R.id.note)).requestFocus();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			// Start the delete intent
			case R.id.deleteButton:
				Intent i = new Intent(this, DeleteNoteActivity.class);
				i.putExtra("noteId", currentNote._id);
				this.startActivity(i);
				this.finish();
				break;
				
			// exit this activity
			case R.id.cancelButton:
				this.setResult(Activity.RESULT_CANCELED);
				this.finish();
				break;
				
			// save the note and exit this activity
			case R.id.saveButton:
				Calendar cal = Calendar.getInstance();
				DatePicker dp = (DatePicker)this.findViewById(R.id.date);
				
				cal.set(
					dp.getYear(), 
					dp.getMonth(), 
					dp.getDayOfMonth()
				);
				
				dbAdapter.open();
				currentNote.note = ((TextView)this.findViewById(R.id.note)).getText().toString();
				currentNote.timestamp = cal.getTimeInMillis();
				currentNote.save();
				dbAdapter.close();
				
				toastPopup.show();
				
				this.getIntent().putExtra("noteId", currentNote._id);
				this.setResult(Activity.RESULT_OK, this.getIntent());
				this.finish();
				break;
		}
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	}
}

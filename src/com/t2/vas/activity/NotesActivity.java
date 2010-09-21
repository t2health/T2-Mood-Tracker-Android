package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.t2.vas.Global;
import com.t2.vas.NotesCursorAdapter;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NotesActivity extends ABSActivity implements OnItemClickListener, OnClickListener {
	private static final String TAG = NotesActivity.class.getName();
	private static final int NOTE_ACTIVITY = 97;
	private static final int PASSWORD_PROMPT = 98;
	protected static final int ADD_NOTES_HEADER_ID = 345987;

	private NotesCursorAdapter notesAdapter;
	private ListView notesListView;
	private static final String NOTE_DATE_FORMAT = "EEEE MMMM, d yyyy";
	private Cursor notesCursor;

	private SharedPreferences sharedPref;
	private Toast notesUnlockToast;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		VASAnalytics.onEvent(VASAnalytics.EVENT_NOTES_ACTIVITY);

		// Init global main variables.
		notesUnlockToast = Toast.makeText(this, R.string.notes_unlocked, 6000);
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		this.setContentView(R.layout.notes_activity);

		this.findViewById(R.id.addNoteButton).setOnClickListener(this);
		this.findViewById(R.id.closeButton).setOnClickListener(this);

		Calendar cal = Calendar.getInstance();
		long nowTime = cal.getTimeInMillis();
		long relockTime = sharedPref.getLong("notes_relock_time", nowTime);
		if(sharedPref.getBoolean("password_protect_notes", false) && relockTime <= nowTime) {
			showPasswordPrompt();
		} else {
			this.initInterface();
		}
	}

	private void showPasswordPrompt() {
		String notesPassword = sharedPref.getString("notes_password", null);

		Intent i = new Intent(this, PasswordActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		i.putExtra("mode", PasswordActivity.MODE_UNLOCK);
		i.putExtra("current_password", notesPassword);
		this.startActivityForResult(i, PASSWORD_PROMPT);
	}

	private void initInterface() {
		Intent intent = this.getIntent();
		long startTimestamp = intent.getLongExtra("start_timestamp", -1);
		long endTimestamp = intent.getLongExtra("end_timestamp", -1);

		// Init global main variables.
		this.notesCursor = ((Note)dbAdapter.getTable("note")).queryForNotes(startTimestamp, endTimestamp, "timestamp DESC");
		this.startManagingCursor(this.notesCursor);
        this.notesAdapter = new NotesCursorAdapter(
        		this,
        		android.R.layout.simple_list_item_2,
        		this.notesCursor,
        		new String[] {
    				"note",
        			"timestamp"
        		},
        		new int[] {
        			android.R.id.text1,
        			android.R.id.text2
        		},
        		new SimpleDateFormat(NOTE_DATE_FORMAT)
        );



        notesListView = ((ListView)this.findViewById(R.id.list));
        notesListView.setAdapter(null);

        View headerView = View.inflate(this, R.layout.simple_list_item_3, null);
        ((ImageView)headerView.findViewById(R.id.image1)).setImageResource(R.drawable.add_blue);
        ((TextView)headerView.findViewById(R.id.text1)).setText(R.string.add_note);
        headerView.setId(ADD_NOTES_HEADER_ID);
        headerView.setOnClickListener(this);

        notesListView.addHeaderView(headerView);

        notesListView.setAdapter(notesAdapter);
        notesListView.setOnItemClickListener(this);
        //notesListView.setOnItemLongClickListener(this);


        /*if(this.findViewById(R.id.addNoteButton) != null) {
        	this.findViewById(R.id.addNoteButton).setOnClickListener(this);
        }*/

        // Hide the no notes message if there are notes.
        if(notesListView.getCount() > 0) {
        	this.findViewById(R.id.noNotesMessage).setVisibility(View.GONE);
        }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode) {
			case NOTE_ACTIVITY:
				long noteId = -1;
				if(data != null) {
					noteId = data.getLongExtra("noteId", -1);
				}

				this.notesAdapter.getCursor().requery();
				this.notesAdapter.notifyDataSetChanged();

				// Select the note
				if(noteId >= 0) {
					this.selectNote(noteId);
				}
				break;

			case PASSWORD_PROMPT:
				if(resultCode == Activity.RESULT_OK) {
					initInterface();

					// Do not require a password to view notes for 1 hour.
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, 1);
					this.sharedPref.edit().putLong("notes_relock_time", cal.getTimeInMillis()).commit();

					notesUnlockToast.show();
				} else {
					this.finish();
					return;
				}
		}

		// Hide the no notes message if there are notes.
        if(notesListView.getCount() == 0) {
        	this.findViewById(R.id.noNotesMessage).setVisibility(View.VISIBLE);
        } else {
        	this.findViewById(R.id.noNotesMessage).setVisibility(View.GONE);
        }

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		/*Long noteId = arg3;
		//Long noteId = (Long)arg1.getTag();
		if(noteId == null || noteId <= 0) {
			return;
		}*/
		long noteId = arg3;

		Intent i = new Intent(this, NoteActivity.class);
		i.putExtra("noteId", noteId);

		this.startActivityForResult(i, NOTE_ACTIVITY);
	}

	/*@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		onItemClick(arg0, arg1, arg2, arg3);
		return false;
	}*/

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.addNoteButton:
			case ADD_NOTES_HEADER_ID:
				Intent i = new Intent(this, NoteActivity.class);
				i.putExtra("timestamp",
						this.getIntent().getLongExtra(
								"start_timestamp",
								Calendar.getInstance().getTimeInMillis()
						)
				);
				this.startActivityForResult(i, NOTE_ACTIVITY);
				break;
			case R.id.closeButton:
				this.finish();
				return;
		}
	}

	private void selectNote(long noteId) {

	}




	@Override
	public int getHelpResId() {
		return R.string.notes_help;
	}
/*
	@Override
	public int getHelp() {
		return R.string.activity_note_help;
	}
*/


}

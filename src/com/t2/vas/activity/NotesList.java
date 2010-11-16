package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.t2.vas.Global;
import com.t2.vas.NotesCursorAdapter;
import com.t2.vas.R;
import com.t2.vas.db.tables.Note;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NotesList extends CustomTitle implements OnItemClickListener {
	private static final int NOTE_ACTIVITY = 97;
	private static final int PASSWORD_PROMPT = 98;
	
	private static final String TAG = NotesList.class.getSimpleName();
	
	private Cursor notesCursor;
	private SimpleCursorAdapter notesAdapter;
	private ListView notesListView;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Global.LONG_DATE_FORMAT);
	private Toast notesUnlockToast;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.notes_list);
		
		this.setExtraButtonImage(R.drawable.add_blue);
		notesUnlockToast = Toast.makeText(this, R.string.notes_unlocked, Toast.LENGTH_LONG);
		
		notesListView = (ListView)this.findViewById(R.id.list);
		notesListView.setEmptyView(findViewById(R.id.empty_list));
		notesListView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Calendar cal = Calendar.getInstance();
		long nowTime = cal.getTimeInMillis();
		long relockTime = sharedPref.getLong("notes_relock_time", nowTime);
		if(sharedPref.getBoolean("password_protect_notes", false) && relockTime <= nowTime) {
			showPasswordPrompt();
		} else {
			this.initInterface();
		}
	}
	
	@Override
	public void onExtraButtonPressed() {
		Intent i = new Intent(this, NoteActivity.class);
		this.startActivityForResult(i, NOTE_ACTIVITY);
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
		
		notesCursor = new Note(dbAdapter).queryForNotes(-1, -1, "timestamp DESC");
		startManagingCursor(notesCursor);
		
		// Figure out where to focus the list.
		int listStartPosition = 0;
		while(notesCursor.moveToNext()) {
			long ts = notesCursor.getLong(notesCursor.getColumnIndex("timestamp"));
			if(ts >= startTimestamp) {
				listStartPosition = notesCursor.getPosition();
			}
		}
		if(listStartPosition < 0) {
			listStartPosition = 0;
		}
		notesCursor.moveToFirst();
		
		
		notesAdapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_2,
				notesCursor,
				new String[] {
    				"note",
        			"timestamp"
        		},
        		new int[] {
        			R.id.text1,
        			R.id.text2
        		}
		);
		notesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if(view.getId() == R.id.text2) {
					long timestamp = cursor.getLong(columnIndex);
					if(timestamp > 0) {
						((TextView)view).setText(
								simpleDateFormat.format(new Date(timestamp))
						);
					} else {
						((TextView)view).setText(
								getString(R.string.never)
						);
					}
					
					return true;
				}
				return false;
			}
		});
		
		notesListView.setAdapter(notesAdapter);
		notesListView.setSelection(listStartPosition);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		switch(requestCode) {
			case NOTE_ACTIVITY:
				notesCursor.requery();
				notesAdapter.notifyDataSetChanged();
				/*long noteId = -1;
				if(data != null) {
					noteId = data.getLongExtra("noteId", -1);
				}

				this.notesAdapter.getCursor().requery();
				this.notesAdapter.notifyDataSetChanged();

				// Select the note
				if(noteId >= 0) {
					this.selectNote(noteId);
				}*/
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
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, NoteActivity.class);
		i.putExtra(NoteActivity.EXTRA_NOTE_ID, arg3);
		this.startActivityForResult(i, 123);
	}
}

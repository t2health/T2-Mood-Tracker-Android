package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import com.t2.vas.Global;
import com.t2.vas.NotesCursorAdapter;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class NotesActivity extends BaseActivity implements OnItemClickListener, OnClickListener, OnItemLongClickListener {
	private static final String TAG = NotesActivity.class.getName();
	private static final int NOTE_ACTIVITY = 97;
	private static final int PASSWORD_PROMPT = 98;
	
	private NotesCursorAdapter notesAdapter;
	private DBAdapter dbAdapter;
	private ListView notesListView;
	private static final String NOTE_DATE_FORMAT = "EEEE MMMM, d yyyy";
	private Cursor notesCursor;

	private SharedPreferences sharedPref;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Init global main variables.
		sharedPref = this.getSharedPreferences(Global.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		
		if(sharedPref.getBoolean("password_protect_notes", false)) {
			showPasswordPrompt();
		} else {
			this.initInterface();
		}
	}

	private void showPasswordPrompt() {
		String notesPassword = sharedPref.getString("notes_password", null);
		
		Intent i = new Intent(this, PasswordActivity.class);
		i.putExtra("mode", PasswordActivity.MODE_UNLOCK);
		i.putExtra("current_password", notesPassword);
		this.startActivityForResult(i, PASSWORD_PROMPT);
	}
	
	private void initInterface() {
		// Init global main variables.
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		
		this.notesCursor = ((Note)dbAdapter.getTable("note")).select(null, "timestamp DESC");
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
        this.setContentView(R.layout.notes_activity);
        
        /*LinearLayout addViewItem = (LinearLayout)ListView.inflate(this, R.layout.simple_list_item_3, null);
        ((TextView)addViewItem.findViewById(R.id.text1)).setText(R.string.add_note);
        ((ImageView)addViewItem.findViewById(R.id.image1)).setImageResource(android.R.drawable.ic_menu_add);*/
        
        notesListView = ((ListView)this.findViewById(R.id.list));
        //notesListView.addHeaderView(addViewItem);
        notesListView.setAdapter(notesAdapter);
        notesListView.setOnItemClickListener(this);
        notesListView.setOnItemLongClickListener(this);
        
        this.findViewById(R.id.addNote).setOnClickListener(this);
        
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
				} else {
					this.finish();
					return;
				}
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Long noteId = (Long)arg1.getTag();
		
		Intent i = new Intent(this, NoteActivity.class);
		if(noteId != null) {
			i.putExtra("noteId", noteId);
		}
		
		this.startActivityForResult(i, NOTE_ACTIVITY);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		onItemClick(arg0, arg1, arg2, arg3);
		return false;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.addNote:
				Intent i = new Intent(this, NoteActivity.class);
				this.startActivityForResult(i, NOTE_ACTIVITY);
				break;
		}
	}
	
	private void selectNote(long noteId) {
		
	}
}

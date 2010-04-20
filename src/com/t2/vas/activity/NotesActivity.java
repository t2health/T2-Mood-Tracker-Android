package com.t2.vas.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.t2.vas.Global;
import com.t2.vas.NoteCursorAdapter;
import com.t2.vas.NotesAdapter;
import com.t2.vas.NotesCursorAdapter;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Note;
import com.t2.vas.view.ChartLayout;
import com.t2.vas.view.NoteLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker.OnDateChangedListener;

public class NotesActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final String TAG = NotesActivity.class.getName();
	
	private NotesCursorAdapter notesAdapter;
	private DBAdapter dbAdapter;
	private ListView notesListView;
	private static final String NOTE_DATE_FORMAT = "EEEE MMMM, d yyyy";
	private Cursor notesCursor;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// init the local variables;
		Intent intent = this.getIntent();
		
		
		// Init global main variables.
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		
		this.notesCursor = ((Note)dbAdapter.getTable("note")).select(null, "timestamp DESC");
		
        this.notesAdapter = new NotesCursorAdapter(
        		this, 
        		R.layout.note_adapter_item, 
        		this.notesCursor, 
        		new String[] {
        			"timestamp",
        			"note"
        		}, 
        		new int[] {
        			R.id.date,
        			R.id.note
        		},
        		new SimpleDateFormat(NOTE_DATE_FORMAT)
        );
        this.setContentView(R.layout.notes_activity);
        
        notesListView = ((ListView)this.findViewById(R.id.list));
        notesListView.setAdapter(notesAdapter);
        notesListView.setOnItemClickListener(this);
        
        this.findViewById(R.id.addNoteButton).setOnClickListener(this);
        
        // Hide the no notes message if there are notes.
        if(notesListView.getCount() > 0) {
        	this.findViewById(R.id.noNotesMessage).setVisibility(View.GONE);
        }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
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
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Long noteId = (Long)arg1.getTag();
		
		Intent i = new Intent(this, NoteActivity.class);
		i.putExtra("noteId", noteId);
		
		this.startActivityForResult(i, 1234567890);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.addNoteButton:
				Intent i = new Intent(this, NoteActivity.class);
				this.startActivityForResult(i, 1234567890);
				break;
		}
	}
	
	private void selectNote(long noteId) {
		
	}
}

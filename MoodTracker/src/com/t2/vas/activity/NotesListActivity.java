/*
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

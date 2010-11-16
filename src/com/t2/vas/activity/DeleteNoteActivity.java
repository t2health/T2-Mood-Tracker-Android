package com.t2.vas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.db.tables.Note;

public class DeleteNoteActivity extends ABSActivity implements OnClickListener {
	private Note currentNote;
	private Toast toastPopup;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VASAnalytics.onEvent(VASAnalytics.EVENT_DELETE_NOTE_ACTIVITY);

		this.setContentView(R.layout.delete_note_activity);

		currentNote = ((Note)dbAdapter.getTable("note")).newInstance();
		toastPopup = Toast.makeText(this, R.string.note_deleted, 2000);

		// init the local variables;
		Intent intent = this.getIntent();
		long currentNoteId = intent.getLongExtra("noteId", -1);

		if(currentNoteId < 0) {
			this.finish();
		}

		currentNote._id = currentNoteId;
		// quit if this note doesn't exist.
		if(!currentNote.load()) {
			this.finish();
		}

		((Button)this.findViewById(R.id.yesButton)).setOnClickListener(this);
		((Button)this.findViewById(R.id.noButton)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.yesButton:

				currentNote.delete();

				toastPopup.show();

				this.finish();

				break;
			case R.id.noButton:
				this.setResult(Activity.RESULT_CANCELED);
				this.finish();
				break;
		}
	}
}

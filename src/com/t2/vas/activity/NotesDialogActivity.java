package com.t2.vas.activity;

import com.t2.vas.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NotesDialogActivity extends NotesActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View addButton = this.findViewById(ADD_NOTES_HEADER_ID);

		if(addButton == null) {
			return;
		}

		((ListView)this.findViewById(R.id.list)).removeHeaderView(addButton);

		this.findViewById(R.id.dialogButtons).setVisibility(View.VISIBLE);
	}
}

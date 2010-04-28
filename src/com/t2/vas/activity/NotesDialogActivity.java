package com.t2.vas.activity;

import com.t2.vas.R;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class NotesDialogActivity extends NotesActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View addButton = this.findViewById(R.id.addNote);
		
		if(addButton == null) {
			return;
		}
		
		((ViewGroup)addButton.getParent()).removeView(addButton);
	}
}

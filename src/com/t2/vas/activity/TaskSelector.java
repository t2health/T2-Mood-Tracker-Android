package com.t2.vas.activity;

import com.t2.vas.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TaskSelector extends ABSActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.task_selector);

		this.findViewById(R.id.formButton).setOnClickListener(this);
		this.findViewById(R.id.resultsButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent i = new Intent();

		switch(v.getId()) {
			case R.id.formButton:
				i.setAction("com.t2.vas.MainActivity2");
				i.putExtra("action", "com.t2.vas.FormActivity");
				this.startActivity(i);
				return;

			case R.id.resultsButton:
				i.setAction("com.t2.vas.MainActivity2");
				i.putExtra("action", "com.t2.vas.ResultsActivity");
				this.startActivity(i);
				return;
		}

		super.onClick(v);
	}



}

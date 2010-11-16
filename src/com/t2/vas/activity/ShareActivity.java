package com.t2.vas.activity;

import com.t2.vas.R;
import com.t2.vas.db.tables.Group;

import android.content.Intent;
import android.os.Bundle;

public class ShareActivity extends CustomTitle {
	public static final String EXTRA_GROUP_ID = "groupId";
	private Group groupTable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		long groupId = intent.getLongExtra(EXTRA_GROUP_ID, 0);
		groupTable = new Group(this.dbAdapter);
		groupTable._id = groupId;
		if(!groupTable.load()) {
			this.finish();
		}
		
		this.setTitle(groupTable.title);
	
		this.setContentView(R.layout.share);
	}
}

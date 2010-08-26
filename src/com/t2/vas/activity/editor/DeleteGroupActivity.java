package com.t2.vas.activity.editor;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DeleteGroupActivity extends ABSActivity implements OnClickListener {
	private DBAdapter dbAdapter;
	private Group currentGroup;
	private Toast toastPopup;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		VASAnalytics.onEvent(VASAnalytics.EVENT_DELETE_GROUP_ACTIVITY);

		this.setContentView(R.layout.delete_group_activity);

		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
		toastPopup = Toast.makeText(this, R.string.activity_group_deleted, 2000);

		// init the local variables;
		Intent intent = this.getIntent();
		long currentGroupId = intent.getLongExtra("group_id", -1);

		if(currentGroupId < 0) {
			this.finish();
		}

		currentGroup._id = currentGroupId;
		// quit if this note doesn't exist.
		if(!currentGroup.load()) {
			this.finish();
		}

		((Button)this.findViewById(R.id.yesButton)).setOnClickListener(this);
		((Button)this.findViewById(R.id.noButton)).setOnClickListener(this);

		dbAdapter.close();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.yesButton:

				dbAdapter.open();
				currentGroup.delete();
				dbAdapter.close();

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

package com.t2.vas.activity;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.GroupReminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class ReminderPreferenceActivity extends BaseActivity implements OnClickListener {
	private DBAdapter dbAdapter;
	private Group currentGroup;
	private GroupReminder currentGroupReminder;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // init global variables.
		dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
		currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
        
		Intent intent = this.getIntent();
		
		currentGroup._id = intent.getLongExtra("group_id", -1);
		
		// Load the note from the DB
		if(currentGroup._id < 0 || !currentGroup.load()) {
			this.finish();
			return;
		}
		
		dbAdapter.close();
        
        this.setContentView(R.layout.reminder_preference_activity);
        
        
        currentGroupReminder = currentGroup.getReminder();
        
        switch(currentGroupReminder.remind_mode) {
	        case GroupReminder.REMIND_NEVER:
	        	((RadioButton)this.findViewById(R.id.reminderPreferenceNever)).setChecked(true);
	        	break;
	        case GroupReminder.REMIND_HOURLY:
	        	((RadioButton)this.findViewById(R.id.reminderPreferenceHourly)).setChecked(true);
	        	break;
	        case GroupReminder.REMIND_DAILY:
	        	((RadioButton)this.findViewById(R.id.reminderPreferenceDaily)).setChecked(true);
	        	break;
	        case GroupReminder.REMIND_WEEKLY:
	        	((RadioButton)this.findViewById(R.id.reminderPreferenceWeekly)).setChecked(true);
	        	break;
        }
        
        ((RadioButton)this.findViewById(R.id.reminderPreferenceNever)).setOnClickListener(this);
        ((RadioButton)this.findViewById(R.id.reminderPreferenceHourly)).setOnClickListener(this);
        ((RadioButton)this.findViewById(R.id.reminderPreferenceDaily)).setOnClickListener(this);
        ((RadioButton)this.findViewById(R.id.reminderPreferenceWeekly)).setOnClickListener(this);
        //((Button)this.findViewById(R.id.closeButton)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.reminderPreferenceNever:
				currentGroupReminder.remind_mode = GroupReminder.REMIND_NEVER;
				currentGroupReminder.save();
				break;
				
			case R.id.reminderPreferenceHourly:
				currentGroupReminder.remind_mode = GroupReminder.REMIND_HOURLY;
				currentGroupReminder.save();
				break;
				
			case R.id.reminderPreferenceDaily:
				currentGroupReminder.remind_mode = GroupReminder.REMIND_DAILY;
				currentGroupReminder.save();
				break;
				
			case R.id.reminderPreferenceWeekly:
				currentGroupReminder.remind_mode = GroupReminder.REMIND_WEEKLY;
				currentGroupReminder.save();
				break;
				
			/*case R.id.closeButton:
				this.finish();
				break;*/
		}
	}
}

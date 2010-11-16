package com.t2.vas.activity.editor;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.CustomTitle;
import com.t2.vas.db.tables.Group;

public class ScaleListActivity extends CustomTitle implements OnItemClickListener {
	public static final String EXTRA_GROUP_ID = "group_id";
	
	private Group currentGroup;

	private Cursor scalesCursor;

	private SimpleCursorAdapter scalesAdapter;
	
//	private static final int START_SCALE_ACTIVITY = 16876;
//	private static final int START_SCALE_GATEWAY_ACTIVITY = 16875;

	private static final int SCALE_GATEWAY_ACTIVITY = 236;
	private static final int SCALE_ACTIVITY = 237;
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VASAnalytics.onEvent(VASAnalytics.EVENT_SCALE_LIST_ACTIVITY);

        this.setContentView(R.layout.scale_list_activity);

        this.setExtraButtonImage(R.drawable.add_blue);
        Intent intent = this.getIntent();

        currentGroup = (Group)dbAdapter.getTable("group");
        currentGroup._id = intent.getLongExtra(EXTRA_GROUP_ID, -1);

        if(currentGroup._id < 0 || !currentGroup.load()) {
        	this.finish();
        	return;
        }

        
        scalesCursor = currentGroup.getScalesCursor();
        this.startManagingCursor(scalesCursor);
        scalesAdapter = new SimpleCursorAdapter(
        		this, 
        		R.layout.list_item_2_inline, 
        		scalesCursor, 
        		new String[] {
        				"min_label",
        				"max_label",
        		},
        		new int[] {
        				R.id.text1,
        				R.id.text2
        		}
		);
        
        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.setEmptyView(this.findViewById(R.id.empty_list));
        listView.setAdapter(scalesAdapter);
        listView.setOnItemClickListener(this);
        /*initAdapterData();

        adapter = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_list_item_1,
        		scaleListString
        );

        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.setEmptyView(this.findViewById(R.id.empty_list));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);*/
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		// Start the add activity if no scales are present.
        if(scalesCursor.getCount() < 1) {
        	startAddActivity();
        }
	}

	@Override
	public void onExtraButtonPressed() {
		startAddActivity();
	}

	private void startAddActivity() {
		Intent i = new Intent(this, AddScaleGateway.class);
		i.putExtra("group_id", currentGroup._id);
		this.startActivityForResult(i, SCALE_GATEWAY_ACTIVITY);
	}

	private void startEditActivity(long scaleId) {
		Intent i = new Intent(this, ScaleActivity.class);
		i.putExtra("scale_id", scaleId);
		this.startActivityForResult(i, SCALE_ACTIVITY);
	}

	/*private void initAdapterData() {
		scaleList = currentGroup.getScales();

		scaleListString.clear();
        for(int i = 0; i < scaleList.size(); i++) {
        	scaleListString.add(scaleList.get(i).min_label+" "+scaleList.get(i).max_label);
        }

        if(adapter != null) {
        	adapter.notifyDataSetChanged();
        }
	}*/

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		this.scalesCursor.requery();
		this.scalesAdapter.notifyDataSetChanged();
		
		if(requestCode == SCALE_GATEWAY_ACTIVITY && data != null) {
			int id = data.getIntExtra("action", 0);
			if(id == AddScaleGateway.ADD_SCALE_ACTIVITY) {
				Intent i = new Intent(this, ScaleActivity.class);
				i.putExtra("group_id", currentGroup._id);
				this.startActivityForResult(i, SCALE_ACTIVITY);
				
			} else if(id == AddScaleGateway.COPY_SCALE_ACTIVITY) {
				Intent i = new Intent(this, CopyScale.class);
				i.putExtra("group_id", currentGroup._id);
				this.startActivityForResult(i, SCALE_ACTIVITY);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		startEditActivity(arg3);
	}
}

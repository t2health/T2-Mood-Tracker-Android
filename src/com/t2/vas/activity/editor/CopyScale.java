package com.t2.vas.activity.editor;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.SimpleCursorTreeAdapter;

import com.t2.vas.R;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;

public class CopyScale extends ABSActivity implements OnChildClickListener {
	public static final String EXTRA_GROUP_ID = "group_id";
	public static final String EXTRA_SCALE_ID = "scale_id";
	
	private long groupId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.copy_scale);
        
        this.groupId = this.getIntent().getLongExtra(EXTRA_GROUP_ID, -1);
        if(this.groupId <= 0) {
        	this.finish();
        	return;
        }
        
        Group group = new Group(this.dbAdapter);
        Cursor groupCursor = group.getGroupsWithScalesCursor();
		this.startManagingCursor(groupCursor);
        
		this.findViewById(R.id.cancelButton).setOnClickListener(this);
		
		ExpandableListView elv = (ExpandableListView)this.findViewById(R.id.list);
		MySimpleCursorTreeAdapter scta = new MySimpleCursorTreeAdapter(
				this,
				groupCursor,
				android.R.layout.simple_expandable_list_item_2,
				new String[] {
					"title",
				},
				new int[] {
					android.R.id.text1,
				},
				android.R.layout.simple_expandable_list_item_2,
				new String[] {
					"title",
				},
				new int[] {
					android.R.id.text1,
				}
		);
		
		elv.setAdapter(scta);
		elv.setOnChildClickListener(this);
	}
	
	
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cancelButton) {
			this.setResult(RESULT_CANCELED);
			this.finish();
			return;
		}
		super.onClick(v);
	}



	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		Scale scale = ((Scale)this.dbAdapter.getTable("scale")).newInstance();
		scale._id = id;
		scale.load();
		
		scale._id = -1;
		scale.group_id = this.groupId;
		scale.save();
		
		Intent i = new Intent();
		i.putExtra(EXTRA_SCALE_ID, scale._id);
		this.setResult(RESULT_OK, i);
		this.finish();
		
		return false;
	}
	
	private class MySimpleCursorTreeAdapter extends SimpleCursorTreeAdapter {

		public MySimpleCursorTreeAdapter(
				Context context, 
				Cursor cursor, 
				int groupLayout,
				String[] groupFrom, 
				int[] groupTo, 
				int childLayout,
				String[] childFrom, 
				int[] childTo) {
			super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom,
					childTo);
		}

		@Override
		protected Cursor getChildrenCursor(Cursor groupCursor) {
			long groupId = groupCursor.getLong(groupCursor.getColumnIndex("_id"));
			
			Group group = ((Group)dbAdapter.getTable("group")).newInstance();
			group._id = groupId;
			group.load();
			
			return group.getNamedScalesCursor();
		}
		
	}

}

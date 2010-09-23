package com.t2.vas.activity.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.VASAnalytics;
import com.t2.vas.activity.ABSActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public class EditGroupActivity extends ABSActivity implements OnItemClickListener {
	private static final String TAG = EditGroupActivity.class.getName();
	private static final int DELETE_ACTIVITY = 342;
	private static final int OTHER_ACTIVITY = 23;
	
	private Group currentGroup;
	private ArrayList<HashMap<String, Object>> items;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VASAnalytics.onEvent(VASAnalytics.EVENT_EDIT_GROUP_ACTIVITY);
        

        long groupId = this.getIntent().getLongExtra("group_id", 0);
        if(groupId <= 0) {
        	this.finish();
        }

		currentGroup = ((Group)dbAdapter.getTable("group")).newInstance();
        currentGroup._id = groupId;

        if(!currentGroup.load()) {
        	this.finish();
        }


        this.setContentView(R.layout.edit_group_activity);

        ((TextView)this.findViewById(R.id.groupName).findViewById(android.R.id.text1)).setText(currentGroup.title);

        items = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object> data;

        if(!(this.currentGroup.immutable > 0)) {
	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.delete_category_title));
	        data.put("text2", this.getString(R.string.delete_category_desc));
	        data.put("action", "com.t2.vas.editor.DeleteGroupActivity");
	        data.put("enabled", !(this.currentGroup.immutable > 0)+"");
	        items.add(data);

	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.edit_category_title));
	        data.put("text2", this.getString(R.string.edit_category_desc));
	        data.put("action", "com.t2.vas.editor.GroupActivity");
	        data.put("enabled", !(this.currentGroup.immutable > 0)+"");
	        items.add(data);

	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.manage_scales_title));
	        data.put("text2", this.getString(R.string.manage_scales_desc));
	        data.put("action", "com.t2.vas.editor.ScaleListActivity");
	        data.put("enabled", !(this.currentGroup.immutable > 0)+"");
	        items.add(data);
        }

        data = new HashMap<String,Object>();
        data.put("text1", this.getString(R.string.clear_data_title));
        data.put("text2", this.getString(R.string.clear_data_desc));
        data.put("action", "com.t2.vas.editor.ClearDataActivity");
        data.put("enabled", "true");
        items.add(data);

        ListView list = (ListView) this.findViewById(R.id.list);
        list.setAdapter(new ActionAdapter(
        		this,
        		items,
        		android.R.layout.simple_list_item_2,
        		new String[] {
        				"text1",
        				"text2",
        		},
        		new int[] {
        				android.R.id.text1,
        				android.R.id.text2,
        		}
		));
        list.setOnItemClickListener(this);
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == DELETE_ACTIVITY && resultCode == Activity.RESULT_OK) {
			this.setResult(Activity.RESULT_OK);
			this.finish();
		}
		
		this.currentGroup.load();
		((TextView)this.findViewById(R.id.groupName).findViewById(android.R.id.text1)).setText(this.currentGroup.title);
	}



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		HashMap<String, Object> item = this.items.get(arg2);
		String action = (String)item.get("action");

		Intent i = new Intent();
		i.setAction(action);
		i.putExtra("group_id", this.currentGroup._id);
		
		if(action.equals("com.t2.vas.editor.DeleteGroupActivity")) {
			this.startActivityForResult(i, DELETE_ACTIVITY);
		} else {
			this.startActivityForResult(i, OTHER_ACTIVITY);
		}
	}


	private class ActionAdapter extends SimpleAdapter {
		public ActionAdapter(Context context, ArrayList<HashMap<String, Object>> items, int resource, String[] from, int[] to) {
			super(context, items, resource, from, to);
		}


		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			HashMap<String, String> item = (HashMap<String, String>) this.getItem(position);
			if(item.get("enabled") != null && item.get("enabled").equals("true")) {
				return true;
			}
			return false;
		}



	}
}

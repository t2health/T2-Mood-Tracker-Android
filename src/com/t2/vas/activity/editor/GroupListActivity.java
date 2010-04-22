package com.t2.vas.activity.editor;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.t2.vas.Global;
import com.t2.vas.R;
import com.t2.vas.activity.BaseActivity;
import com.t2.vas.db.DBAdapter;
import com.t2.vas.db.tables.Group;

public class GroupListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = GroupListActivity.class.getName();
	private DBAdapter dbAdapter;
	private ArrayList<Group> groupList = new ArrayList<Group>();
	private ArrayAdapter<String> adapter;
	private ArrayList<String> groupListString = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.group_list_activity);
        
        dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
        
        initAdapterData();
        
        adapter = new ArrayAdapter<String>(
        		this, 
        		android.R.layout.simple_list_item_1, 
        		groupListString
		);
        
        LinearLayout addViewItem = (LinearLayout)ListView.inflate(this, R.layout.simple_list_item_3, null);
        ((TextView)addViewItem.findViewById(R.id.text1)).setText(R.string.activity_group_list_add_group);
        ((ImageView)addViewItem.findViewById(R.id.image1)).setImageResource(android.R.drawable.ic_menu_add);
        
        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.addHeaderView(addViewItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        
        dbAdapter.close();
	}
	
	private void initAdapterData() {
		groupList = ((Group)dbAdapter.getTable("group")).getGroups();
		
		// Convert the group list to an array
        groupListString.clear();
        for(int i = 0; i < groupList.size(); i++) {
        	groupListString.add(groupList.get(i).title);
        }
        
        if(adapter != null) {
        	adapter.notifyDataSetChanged();
        }
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		dbAdapter.open();
		initAdapterData();
		dbAdapter.close();
		
		if(data == null) {
			return;
		}
		
		// This group was just added, bring up the add scale interface.
		String mode = data.getStringExtra("mode");
		long group_id = data.getLongExtra("group_id", -1);
		
		if(mode.equals("insert") && group_id > 0) {
			Intent i = new Intent(this, ScaleListActivity.class);
			i.putExtra("group_id", group_id);
			this.startActivity(i);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// The add group button was pressed
		if(arg2 == 0) {
			this.startActivityForResult(new Intent(this, GroupActivity.class), 123457890);
			return;
		}
		
		// Load the scale list
		Group group = groupList.get(arg2 - 1);
		Intent i = new Intent(this, ScaleListActivity.class);
		
		if(group != null) {
			i.putExtra("group_id", group._id);
		}
		
		this.startActivity(i);
	}
	

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// The add group button was pressed
		if(arg2 == 0) {
			this.startActivityForResult(new Intent(this, GroupActivity.class), 123457890);
			return false;
		}
		
		// Load the add/edit group activity.
		Group group = groupList.get(arg2 - 1);
		Intent i = new Intent(this, GroupActivity.class);
		
		if(group != null) {
			i.putExtra("group_id", group._id);
		}
		this.startActivityForResult(i, 1234567890);
		
		return false;
	}
}

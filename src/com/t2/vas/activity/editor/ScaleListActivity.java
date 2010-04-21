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
import com.t2.vas.db.tables.Scale;

public class ScaleListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
	private DBAdapter dbAdapter;
	private Group currentGroup;
	private ArrayList<Scale> scaleList;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> scaleListString = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.scale_list_activity);
        
        Intent intent = this.getIntent();
        
        dbAdapter = new DBAdapter(this, Global.Database.name, Global.Database.version);
        
        currentGroup = (Group)dbAdapter.getTable("group");
        currentGroup._id = intent.getLongExtra("groupId", -1);
        
        if(currentGroup._id < 0 || !currentGroup.load()) {
        	this.finish();
        	return;
        }
        
        initAdapterData();
        
        adapter = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_list_item_1, 
        		scaleListString
        );
        
        LinearLayout addViewItem = (LinearLayout)ListView.inflate(this, R.layout.simple_list_item_3, null);
        ((TextView)addViewItem.findViewById(R.id.text1)).setText(R.string.add_scale);
        ((ImageView)addViewItem.findViewById(R.id.image1)).setImageResource(android.R.drawable.ic_menu_add);
        
        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.addHeaderView(addViewItem);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
	}
	
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		// Start the add activity if no scales are present.
        if(scaleList.size() < 1) {
        	startAddActivity(currentGroup._id);
        }
	}

	private void startAddActivity(long groupId) {
		Intent i = new Intent(this, ScaleActivity.class);
		i.putExtra("groupId", currentGroup._id);
		this.startActivityForResult(i, 123457890);
	}
	
	private void startEditActivity(long scaleId) {
		Intent i = new Intent(this, ScaleActivity.class);
		i.putExtra("scaleId", scaleId);
		this.startActivityForResult(i, 123457890);
	}
	
	private void initAdapterData() {
		scaleList = currentGroup.getScales();
		
		scaleListString.clear();
        for(int i = 0; i < scaleList.size(); i++) {
        	scaleListString.add(scaleList.get(i).min_label+" "+scaleList.get(i).max_label);
        }
        
        if(adapter != null) {
        	adapter.notifyDataSetChanged();
        }
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		initAdapterData();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i;
		
		// The add scale button was pressed
		if(arg2 == 0) {
			this.startAddActivity(currentGroup._id);
			/*i = new Intent(this, ScaleActivity.class);
			i.putExtra("groupId", currentGroup._id);
			this.startActivityForResult(i, 123457890);*/
			return;
		}
		
		// Load the edit scale activity.
		startEditActivity(this.scaleList.get(arg2 - 1)._id);
		/*Scale scale = this.scaleList.get(arg2 - 1);
		i = new Intent(this, ScaleActivity.class);
		i.putExtra("scaleId", scale._id);
		this.startActivityForResult(i, 123457890);*/
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		onItemClick(arg0, arg1, arg2, arg3);
		return false;
	}
}

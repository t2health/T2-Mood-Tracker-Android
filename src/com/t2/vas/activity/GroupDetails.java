package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.t2.vas.R;
import com.t2.vas.activity.editor.ClearDataActivity;
import com.t2.vas.activity.editor.DeleteGroupActivity;
import com.t2.vas.activity.editor.GroupActivity;
import com.t2.vas.activity.editor.ScaleListActivity;
import com.t2.vas.db.tables.Group;
import com.t2.vas.view.SimpleAdapterEnableable;

public class GroupDetails extends CustomTitle implements OnItemClickListener {
	public static final String EXTRA_GROUP_ID = "groupId";
	public static final String EXTRA_START_SCALES = "start_scales";
	
	private Group groupTable;
	private ArrayList<HashMap<String, Object>> actionsList = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String, Object>> manageList = new ArrayList<HashMap<String,Object>>();
	private ListView actionsListView;
	private ListView manageListView;
	private SimpleAdapterEnableable actionsAdapter;
	private SimpleAdapterEnableable manageAdapter;

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
		
		this.setContentView(R.layout.group_details);
		
		initGroup();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		if(this.getIntent().getBooleanExtra(EXTRA_START_SCALES, false)) {
			this.startScalesActivity();
		}
	}
	
	

	private void initGroup() {
		groupTable.load();
		this.setTitle(groupTable.title);
		
		setupActionItems();
		actionsAdapter = new SimpleAdapterEnableable(
				this, 
				actionsList, 
				R.layout.list_item_2_image, 
				new String[] {
						"text1",
						"text2",
						"image1",
				}, 
				new int[] {
						R.id.text1,
						R.id.text2,
						R.id.image1,
				}
		);
		// Disable the results dependent options.
		if(!groupTable.hasResults()) {
			actionsAdapter.setDisabledIndex(1);
			actionsAdapter.setDisabledIndex(2);
		}
		actionsAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if(view.getId() == R.id.image1) {
					((ImageView) view).setImageResource((Integer)data);
					return true;
				}
				return false;
			}
		});
		actionsListView = (ListView) this.findViewById(R.id.actionsList);
		actionsListView.setAdapter(actionsAdapter);
		actionsListView.setOnItemClickListener(this);
		
		
		setupManageItems();
		manageAdapter = new SimpleAdapterEnableable(
				this, 
				manageList, 
				R.layout.list_item_2, 
				new String[] {
						"text1",
						"text2",
				}, 
				new int[] {
						R.id.text1,
						R.id.text2,
				}
		);
		manageListView = (ListView) this.findViewById(R.id.manageList);
		manageListView.setAdapter(manageAdapter);
		manageListView.setOnItemClickListener(this);
	}
	
	private void setupActionItems() {
		actionsList.clear();
		HashMap<String,Object> item;
		
		item = new HashMap<String,Object>();
		item.put("text1", getString(R.string.form_title));
		item.put("text2", getString(R.string.form_desc));
		item.put("image1", R.drawable.form_blue);
		item.put("action", "form");
		actionsList.add(item);
		
		item = new HashMap<String,Object>();
		item.put("text1", getString(R.string.results_title));
		item.put("text2", getString(R.string.results_desc));
		item.put("image1", R.drawable.results_blue);
		item.put("action", "results");
		actionsList.add(item);
		
		item = new HashMap<String,Object>();
		item.put("text1", getString(R.string.share_title));
		item.put("text2", getString(R.string.share_desc));
		item.put("image1", R.drawable.results_blue);
		item.put("action", "share");
		actionsList.add(item);
	}
	
	private void setupManageItems() {
		manageList.clear();
		HashMap<String,Object> data;

        if(!(this.groupTable.immutable > 0)) {
	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.delete_category_title));
	        data.put("text2", this.getString(R.string.delete_category_desc));
	        data.put("action", "delete");
	        data.put("enabled", !(this.groupTable.immutable > 0)+"");
	        manageList.add(data);

	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.edit_category_title));
	        data.put("text2", this.getString(R.string.edit_category_desc));
	        data.put("action", "edit");
	        data.put("enabled", !(this.groupTable.immutable > 0)+"");
	        manageList.add(data);

	        data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.manage_scales_title));
	        data.put("text2", this.getString(R.string.manage_scales_desc));
	        data.put("action", "manageScales");
	        data.put("enabled", !(this.groupTable.immutable > 0)+"");
	        manageList.add(data);
        }

        if(groupTable.visible > 0) {
        	data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.hide_from_main_title));
	        data.put("text2", this.getString(R.string.hide_from_main_desc));
	        data.put("action", "hideFromMain");
	        data.put("enabled", "true");
	        manageList.add(data);
        } else {
        	data = new HashMap<String,Object>();
	        data.put("text1", this.getString(R.string.show_in_main_title));
	        data.put("text2", this.getString(R.string.show_in_main_desc));
	        data.put("action", "showInMain");
	        data.put("enabled", "true");
	        manageList.add(data);
        }
        
        data = new HashMap<String,Object>();
        data.put("text1", this.getString(R.string.clear_data_title));
        data.put("text2", this.getString(R.string.clear_data_desc));
        data.put("action", "clearData");
        data.put("enabled", "true");
        manageList.add(data);
	}

	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == DELETE_GROUP_ACTIVITY) {
			if(resultCode == RESULT_OK) {
				this.finish();
			}
			return;
		}
		
		this.initGroup();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i;
		
		if(arg0 == actionsListView) {
			HashMap<String,Object> data = actionsList.get(arg2);
			if(data.get("action").equals("form")) {
				i = new Intent(this, FormActivity.class);
				i.putExtra(FormActivity.EXTRA_GROUP_ID, groupTable._id);
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Group");
				this.startActivityForResult(i, FORM_ACTIVITY);
				return;
			} else if(data.get("action").equals("results")) {
				i = new Intent(this, ResultsActivity.class);
				i.putExtra(ResultsActivity.EXTRA_GROUP_ID, groupTable._id);
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Group");
				this.startActivityForResult(i, RESULTS_ACTIVITY);
				return;
			} else if(data.get("action").equals("share")) {
				i = new Intent(this, ShareActivity.class);
				i.putExtra(ShareActivity.EXTRA_GROUP_ID, groupTable._id);
				i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Group");
				this.startActivityForResult(i, RESULTS_ACTIVITY);
				return;
			}
			
		} else if(arg0 == manageListView) {
			HashMap<String,Object> data = manageList.get(arg2);
			String action = (String) data.get("action");
			
			if(action.equals("delete")) {
				i = new Intent(this, DeleteGroupActivity.class);
				i.putExtra(DeleteGroupActivity.EXTRA_GROUP_ID, groupTable._id);
				this.startActivityForResult(i, DELETE_GROUP_ACTIVITY);
				
			} else if(action.equals("edit")) {
				i = new Intent(this, GroupActivity.class);
				i.putExtra(GroupActivity.EXTRA_GROUP_ID, groupTable._id);
				this.startActivityForResult(i, 123);
				
			} else if(action.equals("manageScales")) {
				startScalesActivity();
				
			} else if(action.equals("clearData")) {
				i = new Intent(this, ClearDataActivity.class);
				i.putExtra(ClearDataActivity.EXTRA_GROUP_ID, groupTable._id);
				this.startActivityForResult(i, 123);
			
			} else if(action.equals("hideFromMain")) {
				groupTable.visible = 0;
				groupTable.save();
				setupManageItems();
				manageAdapter.notifyDataSetChanged();
				Log.v("TEST", "Hide");
				
			} else if(action.equals("showInMain")) {
				groupTable.visible = 1;
				groupTable.save();
				setupManageItems();
				manageAdapter.notifyDataSetChanged();
				Log.v("TEST", "Show");
				
			}
		}
	}
	
	private void startScalesActivity() {
		Intent i = new Intent(this, ScaleListActivity.class);
		i.putExtra(ScaleListActivity.EXTRA_GROUP_ID, groupTable._id);
		i.putExtra(CustomTitle.EXTRA_BACK_BUTTON_TEXT, "Group Details");
		this.startActivityForResult(i, 123);
	}
}

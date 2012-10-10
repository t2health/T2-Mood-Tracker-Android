/*
 * 
 * T2 Mood Tracker
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2MoodTracker001
 * Government Agency Original Software Title: T2 Mood Tracker
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.t2.vas.activity.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.t2.vas.Analytics;
import com.t2.vas.R;
import com.t2.vas.SharedPref;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Scale;
import com.t2.vas.view.SeparatedListAdapter;

public class GroupActivity extends ABSNavigationActivity implements
OnItemClickListener, DialogInterface.OnClickListener, OnCheckedChangeListener {
	public static final String TAG = GroupActivity.class.getSimpleName();

	public static final String EXTRA_GROUP_ID = "group_id";
	private Group group;
	private ListView listView;
	private SeparatedListAdapter listAdapter;
	private ItemsAdapter generalAdapter;
	private SimpleCursorAdapter scaleAdapter;
	private Cursor scalesCursor;

	private AlertDialog addDialog;
	private ViewGroup addScaleLayout;
	private AlertDialog editDialog;

	private EditText addMinLabel;
	private EditText addMaxLabel;
	private long editScaleId;

	private EditText renameEditText;

	private AlertDialog renameDialog;
	private AlertDialog deleteGroupDialog;

	private AlertDialog addGatewayDialog;

	private AlertDialog deleteScaleDialog;

	private ViewGroup editScaleLayout;

	private EditText editMinLabel;

	private EditText editMaxLabel;

	private AlertDialog copyDialog;

	private SimpleCursorAdapter copyScalesAdapter;

	private Cursor copyScalesCursor;

	private static final int Menu1 = Menu.FIRST + 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		copyScalesCursor = new Scale(dbAdapter).getUniqueScalesCursor();
		this.startManagingCursor(copyScalesCursor);
		copyScalesAdapter = new SimpleCursorAdapter(
				this, 
				R.layout.list_item_2_inline, 
				copyScalesCursor,
				new String[] {
						"max_label",
						"min_label",
				}, 
				new int[] {
						R.id.text1,
						R.id.text2,
				}
				);

		addScaleLayout = (ViewGroup) this.getLayoutInflater().inflate(
				R.layout.add_edit_scale_activity, null);
		addMinLabel = (EditText) addScaleLayout.findViewById(R.id.minLabel);
		addMaxLabel = (EditText) addScaleLayout.findViewById(R.id.maxLabel);

		editScaleLayout = (ViewGroup) this.getLayoutInflater().inflate(
				R.layout.add_edit_scale_activity, null);
		editMinLabel = (EditText) editScaleLayout.findViewById(R.id.minLabel);
		editMaxLabel = (EditText) editScaleLayout.findViewById(R.id.maxLabel);

		renameEditText = new EditText(this);
		renameEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		renameDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.edit_group_title).setView(renameEditText)
		.setPositiveButton(R.string.save, this)
		.setNegativeButton(R.string.cancel, this).create();
		deleteGroupDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.delete)
		.setMessage(R.string.delete_category_confirm)
		.setPositiveButton(R.string.yes, this)
		.setNegativeButton(R.string.no, this).create();
		deleteScaleDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.delete)
		.setMessage(R.string.delete_scale_message)
		.setPositiveButton(R.string.yes, this)
		.setNegativeButton(R.string.no, this).create();
		addGatewayDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.add_rating_scale)
		.setItems(R.array.add_scale_gateway_options, this)
		.setNegativeButton(R.string.cancel, this).create();
		addDialog = new AlertDialog.Builder(this).setTitle(R.string.add_rating_scale)
				.setPositiveButton(R.string.save, this)
				.setNegativeButton(R.string.cancel, this)
				.setView(addScaleLayout).create();
		editDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.edit_scale).setView(editScaleLayout)
		.setPositiveButton(R.string.save, this)
		.setNegativeButton(R.string.cancel, this).create();
		copyDialog = new AlertDialog.Builder(this)
		.setTitle(R.string.copy_scale)
		.setNegativeButton(R.string.cancel, this)
		.setAdapter(copyScalesAdapter, this)
		.create();

		group = new Group(dbAdapter);
		group._id = this.getIntent().getLongExtra(EXTRA_GROUP_ID, 0);
		if (!group.load()) {
			renameEditText.setText("");
			renameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			    @Override
			    public void onFocusChange(View v, boolean hasFocus) {
			        if (hasFocus) {
			        	renameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			        }
			    }
			});
			renameDialog.show();
		}

		this.setContentView(R.layout.list_layout);
		this.setTitle(group.title);
		

		listView = (ListView) this.findViewById(R.id.list);
		
		
		ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> item;

		item = new HashMap<String, Object>();
		item.put("id", "rename");
		item.put("title", getString(R.string.rename));
		items.add(item);

		item = new HashMap<String, Object>();
		item.put("id", "delete");
		item.put("title", getString(R.string.delete));
		items.add(item);

		item = new HashMap<String, Object>();
		item.put("id", "inverseData");
		item.put("title", getString(R.string.is_desirable));
		items.add(item);

		item = new HashMap<String, Object>();
		item.put("id", "visible");
		item.put("title", getString(R.string.show_on_main));
		items.add(item);

		generalAdapter = new ItemsAdapter(this, items, R.layout.list_item_1,
				new String[] { "title", }, new int[] { R.id.text1, });

		setupScalesListview();

		// disable certain components
		if(group.immutable > 0) {
			//this.setRightButtonVisibility(View.GONE);
		}
	}

	private void setupScalesListview()
	{
		scalesCursor = group.getScalesCursor();
		this.startManagingCursor(scalesCursor);
		scaleAdapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_2_stacked_delete, 
				scalesCursor,
				new String[] { 
						"max_label", 
						"min_label", 
						"_id", 
				}, 
				new int[] {
						R.id.text1, 
						R.id.text2, 
						R.id.deleteButton, 
				}
				);
		scaleAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				if(group.immutable > 0) {
					view.setEnabled(false);
				}

				if (view.getId() == R.id.deleteButton) {
					final long id = cursor.getLong(columnIndex);
					view.setFocusable(false);

					view.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							onScaleDeleteButtonPressed(id);
						}
					});
					return true;
				}
				return false;
			}
		});

		listAdapter = new SeparatedListAdapter(this);
		listAdapter.addSection(getString(R.string.general_group_operations), generalAdapter);
		listAdapter.addSection(getString(R.string.scales_list), scaleAdapter);

		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
	}
	
	private void onScaleDeleteButtonPressed(long scaleId) {
		editScaleId = scaleId;
		deleteScaleDialog.show();
	}

	//	@Override
	//	protected void onRightButtonPressed() {
	//		addGatewayDialog.show();
	//	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Adapter adapter = listAdapter.getAdapterForItem(arg2);

		boolean isGeneralAdapter = adapter == generalAdapter;
		boolean isScaleAdapter = adapter == scaleAdapter;
		boolean isMutable = group.immutable == 0;

		if(isGeneralAdapter) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> data = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
			String itemId = (String) data.get("id");

			if(isMutable) {
				if (itemId.equals("rename")) {
					renameEditText.setText(group.title);
					renameDialog.show();
					return;

				} else if (itemId.equals("delete")) {
					deleteGroupDialog.show();
					return;

				} else if(itemId.equals("inverseData")) {
					CheckedTextView ctv = (CheckedTextView)arg1.findViewById(R.id.text1);
					group.inverseResults = !ctv.isChecked();
					group.save();

					listAdapter.notifyDataSetChanged();
					listAdapter.notifyDataSetInvalidated();
					return;
				}
			} else {
				if(itemId.equals("visible")) {
					CheckedTextView ctv = (CheckedTextView)arg1.findViewById(R.id.text1);
					ArrayList<Long> hiddenGroupIds = SharedPref.getHiddenGroups(sharedPref);
					if(ctv.isChecked()) {
						hiddenGroupIds.add(group._id);
					} else {
						hiddenGroupIds.remove(group._id);
					}
					SharedPref.setHiddenGroups(sharedPref, hiddenGroupIds);

					listAdapter.notifyDataSetChanged();
					listAdapter.notifyDataSetInvalidated();
					return;
				} else {
					Toast.makeText(this, R.string.group_immutable_message, Toast.LENGTH_LONG).show();
				}
			}

		} else if(isScaleAdapter) {
			if(isMutable) {
				long id = listAdapter.getItemId(arg2);

				editScaleId = id;
				Scale scale = new Scale(dbAdapter);
				scale._id = id;
				scale.load();

				editScaleId = id;
				editMinLabel.setText(scale.min_label);
				editMaxLabel.setText(scale.max_label);
				editMinLabel.requestFocus();
				editDialog.show();
			} else {
				Toast.makeText(this, R.string.group_immutable_message, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == addDialog) {
			if (which == AlertDialog.BUTTON_POSITIVE) {
				String minLabel = addMinLabel.getText().toString().trim().replace('\n', ' ');
				String maxLabel = addMaxLabel.getText().toString().trim().replace('\n', ' ');

				Scale scale = new Scale(dbAdapter);
				scale.min_label = minLabel;
				scale.max_label = maxLabel;
				scale.group_id = group._id;

				scale.save();
				Analytics.onEvent(this,  "Add Scale," + scale.min_label + "-" + scale.max_label);
			}

		} else if (dialog == editDialog) {
			if (which == AlertDialog.BUTTON_POSITIVE) {
				Scale scale = new Scale(dbAdapter);
				scale._id = editScaleId;
				scale.load();
				scale.min_label = editMinLabel.getText().toString().trim().replace('\n', ' ');
				scale.max_label = editMaxLabel.getText().toString().trim().replace('\n', ' ');
				scale.group_id = group._id;

				scale.save();
				Analytics.onEvent(this,  "Edit Scale," + scale.min_label + "-" + scale.max_label);
			}

		} else if (dialog == renameDialog) {
			if (which == AlertDialog.BUTTON_POSITIVE) {
				if(group == null)
				{
					//Steveo: creating a new group
					group = new Group(dbAdapter);
					group.title = renameEditText.getText().toString().trim().replace('\n', ' ');
					group.save();
					Analytics.onEvent(this,  "Add Category," + group.title);
				}
				else
				{
					//Editing existing group
					group.title = renameEditText.getText().toString().trim().replace('\n', ' ');
					Analytics.onEvent(this,  (group._id > 0 ? "Rename Category," : "Add Category,") + group.title);
					group.save();
					setTitle(group.title);
					
				}
			}
			else
			{
				if(group == null)
				{
					this.finish();
					return;
				}
			}

		} else if (dialog == deleteGroupDialog) {
			if (which == AlertDialog.BUTTON_POSITIVE) {
			    Analytics.onEvent(this,  "Delete Category," + group.title);
				group.delete();
				
				this.finish();
				return;
			}

		} else if (dialog == deleteScaleDialog) {
			if (which == AlertDialog.BUTTON_POSITIVE) {
				Scale scale = new Scale(dbAdapter);
				scale._id = editScaleId;
				Analytics.onEvent(this,  "Delete Scale," + scale.min_label + "-" + scale.max_label);
				scale.delete();
				
			}

		} else if (dialog == addGatewayDialog) {
			addGatewayDialog.dismiss();

			if (which == 0) {
				copyDialog.show();

			} else if (which == 1) {
				addMinLabel.setText("");
				addMaxLabel.setText("");
				addMinLabel.requestFocus();
				addDialog.show();
			}

		} else if(dialog == copyDialog) {
			//Log.v(TAG, "SEL:"+which);
			if(which >= 0) {
				long id = copyScalesAdapter.getItemId(which);
				//Log.v(TAG, "SEL ID:"+id);
				//Log.v(TAG, "id:"+id);
				Scale scale = new Scale(dbAdapter);
				scale._id = id;
				scale.load();
				scale.group_id = group._id;
				scale._id = 0;
				scale.insert();
				Analytics.onEvent(this,  "Copy Scale," + scale.min_label + "-" + scale.max_label);
			}
		}

		setupScalesListview();
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {}


	private class ItemsAdapter extends SimpleAdapter {

		private LayoutInflater layoutInflater;
		private int defaultLayout;

		public ItemsAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
						String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.defaultLayout = resource;
			this.layoutInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> item = (HashMap<String, Object>) this.getItem(position);

			View newView = convertView;
			if(item.get("id").equals("inverseData")) {
				newView = layoutInflater.inflate(R.layout.list_item_1_checked, null);
				((CheckedTextView)newView.findViewById(R.id.text1)).setChecked(group.inverseResults);

			} else if(item.get("id").equals("visible")) {
				List<Long> hiddenGroupIds = SharedPref.getHiddenGroups(sharedPref);
				newView = layoutInflater.inflate(R.layout.list_item_1_checked, null);
				((CheckedTextView)newView.findViewById(R.id.text1)).setChecked(!hiddenGroupIds.contains(group._id));

			} else {
				newView = layoutInflater.inflate(defaultLayout, null);
			}

			((TextView)newView.findViewById(R.id.text1)).setText(item.get("title")+"");

			if(group.immutable > 0 && !item.get("id").equals("visible")) {
				((TextView)newView.findViewById(R.id.text1)).setEnabled(false);
			}

			return newView;
		}
	}
	
	public void populateMenu(Menu menu) {

		menu.setQwertyMode(true);

		MenuItem item1 = menu.add(0, Menu1, 0, R.string.add_rating_scale);
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_add);
		}
		
	}

	public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			addGatewayDialog.show();
			break;
		
		}
		return false;
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		populateMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/** when menu button option selected */
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		return applyMenuChoice(item) || super.onOptionsItemSelected(item);
	}
}

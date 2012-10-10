/*
 * 
 */
package com.t2.vas.activity.preference;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.t2.vas.R;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.db.tables.Group;
import com.t2.vas.db.tables.Note;
import com.t2.vas.view.SeparatedListAdapter;

public class ClearDataActivity extends ABSNavigationActivity implements OnItemClickListener, android.content.DialogInterface.OnClickListener {
	private ListView list;
	private SeparatedListAdapter listAdapter;
	private AlertDialog confirmGroupClearDialog;
	private AlertDialog confirmOtherClearDialog;
	private SimpleCursorAdapter groupsAdapter;
	private SimpleAdapter otherItemsAdapter;
	private String clearedMessage;
	
	private long selectedGroupId;
	private String selectedOtherId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Build the confirm dialogs.
		confirmGroupClearDialog = new AlertDialog.Builder(this)
			.setMessage("")
			.setPositiveButton(R.string.yes, this)
			.setNegativeButton(R.string.no, this)
			.setCancelable(true)
			.create();
		confirmOtherClearDialog = new AlertDialog.Builder(this)
			.setMessage("")
			.setPositiveButton(R.string.yes, this)
			.setNegativeButton(R.string.no, this)
			.setCancelable(true)
			.create();
		
		// Set the content view.
		this.setContentView(R.layout.list_layout);
		
		// Setup the group clear items.
		Group g = new Group(dbAdapter);
		Cursor cursor = g.getGroupsCursor();
		groupsAdapter = new SimpleCursorAdapter(
				this, 
				R.layout.list_item_1, 
				cursor, 
				new String[] {
						"title",
				}, 
				new int[] {
						R.id.text1,
				}
		);
		groupsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				return false;
			}
		});
		
		// Setup the other clear items.
		ArrayList<HashMap<String,Object>> otherDataItems = new ArrayList<HashMap<String,Object>>();
		HashMap<String,Object> item = new HashMap<String,Object>();
		item.put("id", "notes");
		item.put("title", getString(R.string.clear_data_notes));
		otherDataItems.add(item);
		otherItemsAdapter = new SimpleAdapter(
				this, 
				otherDataItems, 
				R.layout.list_item_1, 
				new String[] {
						"title", 
				},
				new int[] {
						R.id.text1,
				}
		);
		
		// init the list adapter
		listAdapter = new SeparatedListAdapter(this);
		listAdapter.addSection(getString(R.string.clear_data_groups), groupsAdapter);
		listAdapter.addSection(getString(R.string.clear_data_other), otherItemsAdapter);
		
		list = (ListView)this.findViewById(R.id.list);
		list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		list.setOnItemClickListener(this);
		list.setAdapter(listAdapter);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Adapter adapter = listAdapter.getAdapterForItem(arg2);
		
		// if an item from the group section was selected.
		if(adapter == groupsAdapter) {
			long id = listAdapter.getItemId(arg2);
			Group g = new Group(dbAdapter);
			g._id = id;
			
			// get the item count.
			selectedGroupId = id;
			int resCount = g.getResultsCount();
			
			// change the dialog message.
			confirmGroupClearDialog.setMessage(getString(R.string.clear_data_groups_confirm).replace("_#_", resCount+""));
			confirmGroupClearDialog.show();
			
			// create the confirmation message.
			clearedMessage = getString(R.string.clear_data_groups_cleared_text).replace("_#_", resCount+"");
			
		// if an item from the other section was selected.
		} else if(adapter == otherItemsAdapter) {
			@SuppressWarnings("unchecked")
			HashMap<String,Object> item = (HashMap<String, Object>) listAdapter.getItem(arg2);
			String id = item.get("id").toString();
			selectedOtherId = id;
			
			if(id.equals("notes")) {
				// get the item count.
				Note n = new Note(dbAdapter);
				int noteCount = n.getCount();
				
				// change the dialog message.
				confirmOtherClearDialog.setMessage(getString(R.string.clear_data_notes_confirm).replace("_#_", noteCount+""));
				confirmOtherClearDialog.show();
				
				// create the confirmation message.
				clearedMessage = getString(R.string.clear_data_notes_cleared_text).replace("_#_", noteCount+"");
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// if a group dialog was interacted.
		if(dialog == confirmGroupClearDialog) {
			if(which == AlertDialog.BUTTON_POSITIVE) {
				Group g = new Group(dbAdapter);
				g._id = selectedGroupId;
				g.clearResults();
				
				Toast.makeText(this, clearedMessage, Toast.LENGTH_LONG).show();
			}
		
		// if an other dialog was interacted.
		} else if(dialog == confirmOtherClearDialog) {
			if(which == AlertDialog.BUTTON_POSITIVE) {
				
				// if this was a notes item.
				if(selectedOtherId.equals("notes")) {
					Note n = new Note(dbAdapter);
					n.clearAll();
				}
				Toast.makeText(this, clearedMessage, Toast.LENGTH_LONG).show();
			}
		}
	}
}

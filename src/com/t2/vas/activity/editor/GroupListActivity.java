/*
 * 
 */
package com.t2.vas.activity.editor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.t2.vas.Analytics;
import com.t2.vas.R;
import com.t2.vas.activity.ABSNavigationActivity;
import com.t2.vas.db.tables.Group;

public class GroupListActivity extends ABSNavigationActivity implements OnItemClickListener, android.content.DialogInterface.OnClickListener {
	private SimpleCursorAdapter groupsAdapter;
	private ListView listView;
	private Cursor groupsCursor;

	private EditText addEditText;
	private AlertDialog addGroupDialog;
	
	private static final int Menu1 = Menu.FIRST + 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addEditText = new EditText(this);
		addEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		addGroupDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.add_group_title)
			.setView(addEditText)
			.setPositiveButton(R.string.save, this)
			.setNegativeButton(R.string.cancel, this)
			.create();
		
		this.setContentView(R.layout.list_layout);
		
		
		Group group = new Group(dbAdapter);
		groupsCursor = group.getGroupsCursor();
		groupsAdapter = new SimpleCursorAdapter(
				this,
				R.layout.list_item_1,
				groupsCursor,
				new String[] {
						"title",
						"_id",
				},
				new int[] {
						R.id.text1
				}
		);
		
		listView = (ListView)this.findViewById(R.id.list);
		listView.setOnItemClickListener(this);
		listView.setAdapter(groupsAdapter);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		groupsCursor.requery();
		groupsAdapter.notifyDataSetChanged();
	}
	
	

	@Override
	protected void onBackButtonPressed() {

		
		super.onBackButtonPressed();
	}

//	@Override
//	protected void onRightButtonPressed() {
//		addEditText.setText("");
//		addGroupDialog.show();
//	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, GroupActivity.class);
		i.putExtra(GroupActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
		i.putExtra(GroupActivity.EXTRA_GROUP_ID, arg3);
		this.startActivityForResult(i, 123);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(dialog == addGroupDialog) {
			if(which == AlertDialog.BUTTON_POSITIVE) {
				Group g = new Group(dbAdapter);
				g.title = addEditText.getText().toString().trim().replace('\n', ' ');
				g.save();
				groupsCursor.requery();
				groupsAdapter.notifyDataSetChanged();
				Analytics.onEvent(this, "Add Category," + g.title);
			}
		}
	}
	
	public void populateMenu(Menu menu) {

		menu.setQwertyMode(true);

		MenuItem item1 = menu.add(0, Menu1, 0, R.string.add_group_title);
		{
			//item1.setAlphabeticShortcut('a');
			item1.setIcon(android.R.drawable.ic_menu_add);
		}
		
	}

	public boolean applyMenuChoice(MenuItem item) {
		switch (item.getItemId()) {
		case Menu1:
			Intent i = new Intent(this, GroupActivity.class);
			i.putExtra(GroupActivity.EXTRA_BACK_BUTTON_TEXT, getString(R.string.back_button));
			i.putExtra(GroupActivity.EXTRA_GROUP_ID, 0L);
			this.startActivityForResult(i, 123);
			/*addEditText.setText("");
			addGroupDialog.show();*/
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

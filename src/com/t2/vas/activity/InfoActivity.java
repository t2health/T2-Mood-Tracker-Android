package com.t2.vas.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.t2.vas.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoActivity extends ABSActivity implements OnItemClickListener {
	private ArrayList<HashMap<String, Object>> items;
	private int helpResourceId;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.info_activity);

        this.helpResourceId = this.getIntent().getIntExtra("help_string_resource_id", 0);

        this.findViewById(R.id.closeButton).setOnClickListener(this);

        items = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> item;

        if(this.helpResourceId > 0) {
	        item = new HashMap<String,Object>();
	        item.put("action", "com.t2.vas.HelpActivity");
	        item.put("text1", this.getString(R.string.help_title));
	        item.put("text2", this.getString(R.string.help_desc));
	        item.put("image1", R.drawable.help_blue);
	        items.add(item);
        }

        item = new HashMap<String,Object>();
        item.put("action", "com.t2.vas.NotesActivity");
        item.put("text1", this.getString(R.string.notes_title));
        item.put("text2", this.getString(R.string.notes_desc));
        item.put("image1", R.drawable.notes_blue);
        items.add(item);

        item = new HashMap<String,Object>();
        item.put("action", "com.t2.vas.Settings.Reminder");
        item.put("text1", this.getString(R.string.reminder_title));
        item.put("text2", this.getString(R.string.reminder_desc));
        item.put("image1", R.drawable.reminder_blue);
        items.add(item);


        item = new HashMap<String,Object>();
        item.put("action", "com.t2.vas.Settings");
        item.put("text1", this.getString(R.string.settings_title));
        item.put("text2", this.getString(R.string.settings_desc));
        item.put("image1", R.drawable.settings_blue);
        items.add(item);

        ListView listView = (ListView)this.findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new SimpleAdapter(
        		this,
        		items,
        		R.layout.simple_list_item_3,
        		new String[]{
        				"text1",
        				"text2",
        				"image1",
        		},
        		new int[] {
        				R.id.text1,
        				R.id.text2,
        				R.id.image1,
        		}
        ));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent();
		i.setAction(this.items.get(arg2).get("action")+"");
		i.putExtra("help_string_resource_id", this.helpResourceId);
		this.startActivity(i);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.closeButton:
				this.finish();
				return;
		}
		super.onClick(v);
	}
}

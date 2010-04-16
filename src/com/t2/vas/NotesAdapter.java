package com.t2.vas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.t2.vas.db.tables.Note;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesAdapter extends ArrayAdapter<Note> {
	private static final String TAG = NotesAdapter.class.getName();
	private ArrayList<Note> items;
	private LayoutInflater inflater;
	private String dateFormat;

	public NotesAdapter(Context context, int layoutResourceId, ArrayList<Note> objects, String dateFormat) {
		super(context, layoutResourceId, objects);
		this.items = objects;
		this.dateFormat = dateFormat;
		this.inflater = ((LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout v = (LinearLayout)inflater.inflate(R.layout.note_adapter_item, null);
        
		Note data = items.get(position);
        if (data != null) {
        	v.setTag(data);
        	
        	TextView timestampTextView = (TextView)v.findViewById(R.id.postedOn);
        	if(timestampTextView != null) {
        		Calendar c = Calendar.getInstance();
        		c.setTimeInMillis(data.timestamp);
        		
        		SimpleDateFormat f = new SimpleDateFormat(this.dateFormat);
        		timestampTextView.setText(f.format(c.getTime()));
        	}
        	
        	TextView noteTextView = (TextView)v.findViewById(R.id.note);
        	if(noteTextView != null) {
        		noteTextView.setText(data.note);
        	}
        }
        return v;
	}
}

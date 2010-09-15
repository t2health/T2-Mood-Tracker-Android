package com.t2.vas;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class NotesCursorAdapter extends SimpleCursorAdapter {
	private static final String TAG = NotesCursorAdapter.class.getName();
	private SimpleDateFormat dateFormat;
	private Calendar cal = Calendar.getInstance();
	private String[] from;
	private int[] to;
	private int timestampIndex = 0;

	public NotesCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, SimpleDateFormat sdf) {
		super(context, layout, c, from, to);
		
		this.dateFormat = sdf;
		this.from = from;
		this.to = to;
		
		for(int i = 0; i < from.length; i++) {
			if(from[i].toLowerCase().equals("timestamp")) {
				timestampIndex = i;
				break;
			}
		}
	}

	
	@Override
	public void setViewText(TextView v, String text) {
		super.setViewText(v, text);
		
		if(v.getId() == to[timestampIndex]) {
			cal.setTimeInMillis(Long.parseLong(v.getText().toString()));
			v.setText(this.dateFormat.format(cal.getTime()));
		}
	}

	
}
